package org.example.simplepayments

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.util.*
import jakarta.validation.constraints.NotBlank

data class BaseMessage(val code: Int, val message: String?)


data class UserRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val fullName: String,
    @field:Positive val balance: BigDecimal=BigDecimal.ZERO
)

data class UserPaymentTransactionRequest(
    @field:Min(1) val userId: Long,
    @field:Positive val amount: BigDecimal
)

data class UserPaymentTransactionTransferRequest(
    @field:Min(1) val fromUserId: Long,
    @field:Min(1) val toUserId: Long,
    @field:Positive val amount: BigDecimal
)

data class CategoryRequest(
    @field:NotBlank val name: String,
    @field:Positive val order: Long,
    val description: String
)

data class ProductRequest(
    @field:NotBlank val name: String,
    @field:Positive val count: Long,
    @field:Min(1) val categoryId: Long
)

data class TransactionRequest(
    @field:Min(1) val userId: Long
)

data class TransactionItemRequest(
    @field:Min(1) val productId: Long,
    @field:Positive val count: Long,
    @field:Positive val amount: BigDecimal,
    @field:Min(1) val transactionId: Long
)



data class UserResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val balance: BigDecimal
)

data class UserPaymentTransactionResponse(
    val id: Long,
    val userResponse: UserResponse,
    val amount: BigDecimal,
    val date: Date
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val order: Long,
    val description: String
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val count: Long,
    val categoryResponse: CategoryResponse
)

data class TransactionResponse(
    val id: Long,
    val userResponse: UserResponse,
    val totalAmount: BigDecimal,
    val date: Date
)

data class TransactionItemResponse(
    val id: Long,
    val product: ProductResponse,
    val count: Long,
    val amount: BigDecimal,
    val totalAmount: BigDecimal,
    val transactionResponse: TransactionResponse
)


data class UserUpdateRequest(
    val username: String?,
    val fullName: String?,
)

data class CategoryUpdateRequest(
    val name: String?,
    @field:Positive val order: Long?,
    val description: String?
)

data class ProductUpdateRequest(
    val name: String?,
    @field:Positive val count: Long?,
    @field:Min(1) val categoryId: Long?
)

data class TransactionItemUpdateRequest(
    @field:Min(1) val productId: Long?,
    @field:Positive val count: Long?,
    @field:Positive val amount: BigDecimal?,
    @field:Positive val totalAmount: BigDecimal?,
    @field:Min(1) val transactionId: Long?
)
