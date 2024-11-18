package org.example.simplepayments

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.*


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @CreatedBy var createdBy: Long? = null,
    @LastModifiedBy var lastModifiedBy: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)
@Entity(name = "users")
class User(
    @Column(nullable = false, unique = true) var username: String,
    var fullName: String,
    var balance: BigDecimal,
) : BaseEntity()

@Entity(name = "user_payment_transaction")
class UserPaymentTransaction(
    @JoinColumn(name="user_id",nullable = false)@ManyToOne var user: User,
    var amount: BigDecimal,
    var date: Date
):BaseEntity()

@Entity
class Category(
    var name: String,
    @Column(name = "orders")var order: Long,
    var description: String
):BaseEntity()


@Entity
class Product(
    var name: String,
    var count: Long,
    @ManyToOne @JoinColumn(name = "category_id", nullable = false)var category: Category
): BaseEntity()

@Entity
class Transaction(
    @ManyToOne @JoinColumn(name = "user_id", nullable = false)var user: User,
    var totalAmount: BigDecimal,
    var date: Date

): BaseEntity()

@Entity
class TransactionItem(
    @ManyToOne @JoinColumn(name = "product_id", nullable = false)var product: Product,
    var count: Long,
    var amount: BigDecimal,
    var totalAmount: BigDecimal,
    @ManyToOne @JoinColumn(name = "transaction_id", nullable = false)var transaction: Transaction
): BaseEntity()

