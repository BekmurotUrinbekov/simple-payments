package org.example.simplepayments

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

interface UserService {
    fun create(request: UserRequest)
    fun getAll(pageable: Pageable): Page<UserResponse>
    fun getOne(id: Long): UserResponse
    fun update(id: Long, request: UserUpdateRequest)
    fun delete(id: Long)
    fun getEntity(id: Long): User
}

interface UserPaymentTransactionService {
    fun deposit(request: UserPaymentTransactionRequest): UserPaymentTransactionResponse
    fun withdraw(request: UserPaymentTransactionRequest): UserPaymentTransactionResponse
    fun transfer(transferRequest: UserPaymentTransactionTransferRequest): UserPaymentTransactionResponse
    fun getPaymentHistory(userId: Long,pageable: Pageable): Page<UserPaymentTransactionResponse>
    fun checkBalance(userId: Long): BigDecimal
    fun getUser(userId: Long): User
}

interface TransactionService {
    fun createTransaction(request: TransactionRequest): TransactionResponse
    fun getTransactionHistory(userId: Long,pageable: Pageable): Page<TransactionResponse>
    fun getAllTransactions(pageable: Pageable): Page<TransactionResponse>
}

interface TransactionItemService {
    fun addItem(request: TransactionItemRequest): TransactionItemResponse
    fun getItemsByTransaction(transactionId: Long,pageable: Pageable): Page<TransactionItemResponse>
}
interface ProductService {
    fun create(request: ProductRequest)
    fun getAll(pageable: Pageable): Page<ProductResponse>
    fun getOne(id: Long): ProductResponse
    fun update(id: Long, request: ProductUpdateRequest)
    fun delete(id: Long)
}
interface CategoryService {
    fun create(request: CategoryRequest)
    fun getAll(pageable: Pageable): Page<CategoryResponse>
    fun getOne(id: Long): CategoryResponse
    fun update(id: Long, request: CategoryUpdateRequest)
    fun delete(id: Long)
    fun getEntity(id: Long) : Category
}

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    override fun create(request: CategoryRequest) {
        request.run {
            categoryRepository.findByNameAndDeletedFalse(name)?.let { throw CategoryAlreadyExistsException() }
            categoryRepository.save(CategoryMapper.toEntity(request))
        }
    }

    override fun getAll(pageable: Pageable): Page<CategoryResponse> {
        return categoryRepository.findAllNotDeletedForPageable(pageable).map {
            CategoryMapper.toResponse(it)
        }
    }

    override fun getOne(id: Long): CategoryResponse {
        return getEntity(id).let { CategoryMapper.toResponse(it) }
    }

    override fun update(id: Long, request: CategoryUpdateRequest) {
        val category = getEntity(id)

        request.run {
            name?.let {
                categoryRepository.findByCategoryName(id, it)?.let { throw CategoryAlreadyExistsException() }
                category.name = it
            }
            order?.let { category.order = it }
            description?.let { category.description = it }
        }

        categoryRepository.save(category)
    }

    @Transactional
    override fun delete(id: Long) {
        categoryRepository.trash(id) ?: throw CategoryNotFoundExistsException()
    }

    override fun getEntity(id: Long): Category {
        return categoryRepository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundExistsException()
    }
}
@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val entityManager: EntityManager,
    private val categoryRepository: CategoryRepository
) : ProductService {

    override fun create(request: ProductRequest) {
        request.run {
            productRepository.findByNameAndDeletedFalse(name)?.let { throw ProductAlreadyExistsException() }
            val category =
                categoryRepository.findByIdAndDeletedFalse(categoryId) ?: throw CategoryNotFoundExistsException()
            productRepository.save(ProductMapper.toEntity(this, category))

        }
    }

    override fun getAll(pageable: Pageable): Page<ProductResponse> {
        return productRepository.findAllNotDeletedForPageable(pageable).map {
            ProductMapper.toResponse(it)
        }
    }

    override fun getOne(id: Long): ProductResponse {
        return getEntity(id).let { ProductMapper.toResponse(it) }
    }

    override fun update(id: Long, request: ProductUpdateRequest) {
        val product = getEntity(id)

        request.run {
            name?.let {
                productRepository.findByProductName(id, it)?.let { throw ProductAlreadyExistsException() }
                product.name = it
            }
            count?.let { product.count = it }
            categoryId?.let { product.category=categoryRepository.findByIdAndDeletedFalse(categoryId) ?: throw CategoryNotFoundExistsException()}
        }

        productRepository.save(product)
    }

    @Transactional
    override fun delete(id: Long) {
        productRepository.trash(id) ?: throw ProductNotFoundExistsException()
    }

    private fun getEntity(id: Long): Product {
        return productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundExistsException()
    }
}
@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun create(request: UserRequest) {
        request.run {
            userRepository.findByUsernameAndDeletedFalse(username)?.let { throw UserAlreadyExistsException() }
            userRepository.save(UserMapper.toEntity(request))
        }
    }

    override fun getAll(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAllNotDeletedForPageable(pageable).map {
            UserMapper.toResponse(it)
        }
    }

    override fun getOne(id: Long): UserResponse {
        return getEntity(id).let {
            UserMapper.toResponse(it)
        }
    }

    override fun update(id: Long, request: UserUpdateRequest) {
        val user = getEntity(id)
        request.run {
            username?.let {
                    userRepository.findByUsername(id, it)?.let { throw UserAlreadyExistsException() }
                        user.username = it
            }
            fullName?.let { user.fullName = it }
        }
        userRepository.save(user)
    }

    @Transactional
    override fun delete(id: Long) {
        userRepository.trash(id) ?: throw UserNotFoundExistsException()
    }

    override fun getEntity(id: Long): User {
        return userRepository.findByIdAndDeletedFalse(id)?: throw UserNotFoundExistsException()
    }
}
@Service
class UserPaymentTransactionServiceImpl(private val userRepository: UserRepository,
                                             private val userPaymentRepository: UserPaymentTransactionRepository
) : UserPaymentTransactionService {
    @Transactional
    override fun deposit(request: UserPaymentTransactionRequest): UserPaymentTransactionResponse {
        val user = getUser(request.userId)
        user.balance = user.balance.add(request.amount)
        userRepository.save(user)

        val transaction = UserPaymentTransaction(
            user = user,
            amount = request.amount,
            date = Date()
        )
        return UserPaymentMapper.toResponse(userPaymentRepository.save(transaction))
    }

    @Transactional
    override fun withdraw(request: UserPaymentTransactionRequest): UserPaymentTransactionResponse {
        val user = getUser(request.userId)

        if (user.balance < request.amount) throw UserHasInsufficientBalance()

        user.balance = user.balance.subtract(request.amount)
        userRepository.save(user)

        val transaction = UserPaymentTransaction(user = user, amount = request.amount.negate(), date = Date())
        return UserPaymentMapper.toResponse(userPaymentRepository.save(transaction))
    }

    @Transactional
    override fun transfer(transferRequest: UserPaymentTransactionTransferRequest): UserPaymentTransactionResponse {
        val fromUser = getUser(transferRequest.fromUserId)
        val toUser = getUser(transferRequest.toUserId)

        if (fromUser.balance < transferRequest.amount) throw UserHasInsufficientBalance()

        fromUser.balance = fromUser.balance.subtract(transferRequest.amount)
        toUser.balance = toUser.balance.add(transferRequest.amount)

        userRepository.save(fromUser)
        userRepository.save(toUser)

        val transaction = UserPaymentTransaction(user = fromUser, amount = transferRequest.amount.negate(), date = Date())
        userPaymentRepository.save(transaction)

        userPaymentRepository.save(
            UserPaymentTransaction(user = toUser, amount = transferRequest.amount, date = Date())
        )

        return UserPaymentMapper.toResponse(transaction)
    }

    override fun getPaymentHistory(userId: Long, pageable: Pageable): Page<UserPaymentTransactionResponse> {

        return userPaymentRepository.findByUserIdAndDeletedFalse(userId, pageable).map { UserPaymentMapper.toResponse(it) }
    }

    override fun checkBalance(userId: Long): BigDecimal {
        return getUser(userId).balance
    }

    override fun getUser(userId: Long): User {
        return userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundExistsException()
    }


}

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : TransactionService {

    @Transactional
    override fun createTransaction(request: TransactionRequest): TransactionResponse {
        val user = userRepository.findByIdAndDeletedFalse(request.userId)?: throw UserNotFoundExistsException()


        val transaction = Transaction(
            user = user,
            totalAmount = BigDecimal.ZERO,
            date = Date()
        )

        return TransactionMapper.toResponse(transactionRepository.save(transaction))
    }

    override fun getTransactionHistory(userId: Long, pageable: Pageable): Page<TransactionResponse> {
        return transactionRepository.findByUserIdAndDeletedFalse(userId,pageable).map { TransactionMapper.toResponse(it) }
    }

    override fun getAllTransactions(pageable: Pageable): Page<TransactionResponse> {
        return transactionRepository.findAllNotDeletedForPageable(pageable).map { TransactionMapper.toResponse(it) }
    }
}

@Service
class TransactionItemServiceImpl(
    private val transactionItemRepository: TransactionItemRepository,
    private val transactionRepository: TransactionRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : TransactionItemService {

    @Transactional
    override fun addItem(request: TransactionItemRequest): TransactionItemResponse {
        val transaction = transactionRepository.findById(request.transactionId)
            .orElseThrow { TransactionNotFoundExistsException() }
        val product = productRepository.findByIdAndDeletedFalse(request.productId)?: throw ProductNotFoundExistsException()

        if (product.count < request.count) {
            throw CountNotEnoughException()
        }

        product.count -= request.count
        productRepository.save(product)

        val item = TransactionItem(
            product = product,
            count = request.count,
            amount = request.amount,
            totalAmount = request.amount.multiply(BigDecimal(request.count)),
            transaction = transaction
        )
        transaction.run {
            if (user.balance<item.totalAmount) {
                throw UserHasInsufficientBalance()
            }
            user.balance -= item.totalAmount
            userRepository.save(user)
            transaction.totalAmount += item.totalAmount
            transactionRepository.save(this)
        }

        return TransactionItemMapper.toResponse(transactionItemRepository.save(item))
    }

    override fun getItemsByTransaction(transactionId: Long, pageable: Pageable): Page<TransactionItemResponse> {
        return transactionItemRepository.findByTransactionIdAndDeletedFalse(transactionId,pageable).map { TransactionItemMapper.toResponse(it) }
    }
}

