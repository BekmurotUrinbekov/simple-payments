package org.example.simplepayments

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): List<T>
    fun findAllNotDeletedForPageable(pageable: Pageable): Page<T>
    fun saveAndRefresh(t: T): T
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findAllNotDeleted(pageable: Pageable): List<T> = findAll(isNotDeletedSpecification, pageable).content
    override fun findAllNotDeletedForPageable(pageable: Pageable): Page<T> =
        findAll(isNotDeletedSpecification, pageable)

    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }

    @Transactional
    override fun saveAndRefresh(t: T): T {
        return save(t).apply { entityManager.refresh(this) }
    }
}

@Repository
interface UserRepository : BaseRepository<User> {
    fun findByUsernameAndDeletedFalse(username: String): User?
    @Query("""
        select u from users u
        where u.id != :id
        and u.username = :username
        and u.deleted = false 
    """)
    fun findByUsername(id: Long, username: String): User?
}

@Repository
interface TransactionRepository : BaseRepository<Transaction> {
    fun findByUserIdAndDeletedFalse(userId: Long,pageable: Pageable): Page<Transaction>
}

@Repository
interface TransactionItemRepository : BaseRepository<TransactionItem> {
    fun findByTransactionIdAndDeletedFalse(transactionId: Long,pageable: Pageable): Page<TransactionItem>
    fun findByTransactionIdAndDeletedFalse(transactionId: Long): List<TransactionItem>
}

@Repository
interface UserPaymentTransactionRepository : BaseRepository<UserPaymentTransaction> {
    @Query("""
        SELECT t FROM user_payment_transaction t
        JOIN t.user u
        WHERE u.id = :userId
        AND u.deleted = false
        AND t.deleted = false
    """)
    fun findByUserIdAndDeletedFalse(userId: Long,pageable: Pageable): Page<UserPaymentTransaction>
}

@Repository
interface CategoryRepository : BaseRepository<Category> {
    fun findByNameAndDeletedFalse(name: String): Category?

    @Query("""
    select c from Category c
    where c.id != :id
    and c.name = :name
    and c.deleted = false
""")
    fun findByCategoryName(id: Long, name: String): Category?
}

@Repository
interface ProductRepository : BaseRepository<Product> {
    @Query("""
    select p from Product p
    where p.id != :id
    and p.name = :name
    and p.deleted = false
""")
    fun findByProductName(id: Long, name: String): Product?

    fun findByNameAndDeletedFalse(name: String): Product?
}
