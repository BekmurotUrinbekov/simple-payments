package org.example.simplepayments

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler(private val errorMessageSource: ResourceBundleMessageSource) {

    @ExceptionHandler(SimplePaymentsExceptionHandler::class)
    fun handleException(exception: SimplePaymentsExceptionHandler): ResponseEntity<BaseMessage> {
        return ResponseEntity.badRequest().body(exception.getErrorMessage(errorMessageSource))
    }


}




@RestController
@RequestMapping(BaseUrl.USER)
class UserController(private val userService: UserService) {

    @PostMapping(BaseUrl.CREATE)
    fun createUser(@Valid @RequestBody request: UserRequest) {
        userService.create(request)
    }

    @GetMapping(BaseUrl.SHOW)
    fun getAllUsers(pageable: Pageable): Page<UserResponse> {
        return userService.getAll(pageable)
    }

    @GetMapping(BaseUrl.PATH_ID)
    fun getUser(@PathVariable id: Long): UserResponse {
        return userService.getOne(id)
    }

    @PostMapping(BaseUrl.UPDATE_BY_ID)
    fun updateUser(@PathVariable id: Long, @Valid @RequestBody request: UserUpdateRequest) {
        userService.update(id, request)
    }

    @GetMapping(BaseUrl.DELETE_BY_ID)
    fun deleteUser(@PathVariable id: Long) {
        userService.delete(id)
    }
}

@RestController
@RequestMapping(BaseUrl.USER_PAYMENT)
class UserPaymentTransactionController(
    private val userPaymentTransactionService: UserPaymentTransactionService
) {

    @PostMapping(BaseUrl.DEPOSIT)
    fun deposit(@Valid @RequestBody request: UserPaymentTransactionRequest): UserPaymentTransactionResponse {
        return userPaymentTransactionService.deposit(request)
    }

    @PostMapping(BaseUrl.WITHDRAW)
    fun withdraw(@Valid @RequestBody request: UserPaymentTransactionRequest): UserPaymentTransactionResponse {
        return userPaymentTransactionService.withdraw(request)
    }

    @PostMapping(BaseUrl.TRANSFER)
    fun transfer(@Valid @RequestBody request: UserPaymentTransactionTransferRequest): UserPaymentTransactionResponse {
        return userPaymentTransactionService.transfer(request)
    }

    @GetMapping(BaseUrl.GET_HISTORY_BY_ID)
    fun getPaymentHistory(@PathVariable(name = "id") userId: Long, pageable: Pageable): Page<UserPaymentTransactionResponse> {
        return userPaymentTransactionService.getPaymentHistory(userId, pageable)
    }

    @GetMapping(BaseUrl.CHECK_BALANCE_BY_ID)
    fun checkBalance(@PathVariable(name = "id") userId: Long): BigDecimal {
        return userPaymentTransactionService.checkBalance(userId)
    }
}

@RestController
@RequestMapping(BaseUrl.TRANSACTION)
class TransactionController(private val transactionService: TransactionService) {

    @PostMapping(BaseUrl.CREATE)
    fun createTransaction(@Valid @RequestBody request: TransactionRequest): TransactionResponse {
        return transactionService.createTransaction(request)
    }

    @GetMapping(BaseUrl.GET_HISTORY_BY_ID)
    fun getTransactionHistory(@PathVariable(name="id") userId: Long, pageable: Pageable): Page<TransactionResponse> {
        return transactionService.getTransactionHistory(userId, pageable)
    }

    @GetMapping(BaseUrl.SHOW)
    fun getAllTransactions(pageable: Pageable): Page<TransactionResponse> {
        return transactionService.getAllTransactions(pageable)
    }
    @GetMapping(BaseUrl.DELETE_BY_ID)
    fun deleteProduct(@PathVariable id: Long) {
        transactionService.delete(id)
    }
}

@RestController
@RequestMapping(BaseUrl.TRANSACTION_ITEM)
class TransactionItemController(private val transactionItemService: TransactionItemService) {

    @PostMapping(BaseUrl.CREATE)
    fun addTransactionItem(@Valid @RequestBody request: TransactionItemRequest): TransactionItemResponse {
        return transactionItemService.addItem(request)
    }

    @GetMapping(BaseUrl.GET_HISTORY_BY_ID)
    fun getItemsByTransaction(@PathVariable(name = "id") transactionId: Long, pageable: Pageable): Page<TransactionItemResponse> {
        return transactionItemService.getItemsByTransaction(transactionId, pageable)
    }
    @GetMapping(BaseUrl.SHOW)
    fun getAllProducts(pageable: Pageable): Page<TransactionItemResponse> {
        return transactionItemService.getAll(pageable)
    }
    @GetMapping(BaseUrl.DELETE_BY_ID)
    fun deleteProduct(@PathVariable id: Long) {
        transactionItemService.delete(id)
    }
}

@RestController
@RequestMapping(BaseUrl.PRODUCT)
class ProductController(private val productService: ProductService) {

    @PostMapping(BaseUrl.CREATE)
    fun createProduct(@Valid @RequestBody request: ProductRequest) {
        productService.create(request)
    }

    @GetMapping(BaseUrl.SHOW)
    fun getAllProducts(pageable: Pageable): Page<ProductResponse> {
        return productService.getAll(pageable)
    }

    @GetMapping(BaseUrl.PATH_ID)
    fun getProduct(@PathVariable id: Long): ProductResponse {
        return productService.getOne(id)
    }

    @PostMapping(BaseUrl.UPDATE_BY_ID)
    fun updateProduct(@PathVariable id: Long, @Valid @RequestBody request: ProductUpdateRequest) {
        productService.update(id, request)
    }

    @GetMapping(BaseUrl.DELETE_BY_ID)
    fun deleteProduct(@PathVariable id: Long) {
        productService.delete(id)
    }
}

@RestController
@RequestMapping(BaseUrl.CATEGORY)
class CategoryController(private val categoryService: CategoryService) {

    @PostMapping(BaseUrl.CREATE)
    fun createCategory(@Valid @RequestBody request: CategoryRequest) {
        categoryService.create(request)
    }

    @GetMapping(BaseUrl.SHOW)
    fun getAllCategories(pageable: Pageable): Page<CategoryResponse> {
        return categoryService.getAll(pageable)
    }

    @GetMapping(BaseUrl.PATH_ID)
    fun getCategory(@PathVariable id: Long): CategoryResponse {
        return categoryService.getOne(id)
    }

    @PostMapping(BaseUrl.UPDATE_BY_ID)
    fun updateCategory(@PathVariable id: Long, @Valid @RequestBody request: CategoryUpdateRequest) {
        categoryService.update(id, request)
    }

    @GetMapping(BaseUrl.DELETE_BY_ID)
    fun deleteCategory(@PathVariable id: Long) {
        categoryService.delete(id)
    }
}
