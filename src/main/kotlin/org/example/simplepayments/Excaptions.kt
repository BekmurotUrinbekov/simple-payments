package org.example.simplepayments

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

sealed class SimplePaymentsExceptionHandler : RuntimeException() {
    abstract fun errorCode(): ErrorCodes
    open fun getAllArguments(): Array<Any?>? = null

    fun getErrorMessage(resourceBundle: ResourceBundleMessageSource): BaseMessage {
        val message = try {
            resourceBundle.getMessage(  // USER_NOT_FOUND
                errorCode().name, getAllArguments(), LocaleContextHolder.getLocale()
            )
        } catch (e: Exception) {
            e.message
        }
        return BaseMessage(errorCode().code, message)
    }
}


class UserAlreadyExistsException : SimplePaymentsExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_ALREADY_EXISTS
}

class UserNotFoundExistsException : SimplePaymentsExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_NOT_FOUND
}

class UserHasInsufficientBalance: SimplePaymentsExceptionHandler() {
    override fun errorCode()= ErrorCodes.USER_HAS_INSUFFICIENT_BALANCE
}

class TransactionNotFoundExistsException : SimplePaymentsExceptionHandler() {
    override fun errorCode()= ErrorCodes.TRANSACTION_NOT_FOUND
}

class ProductNotFoundExistsException : SimplePaymentsExceptionHandler() {
    override fun errorCode()= ErrorCodes.PRODUCT_NOT_FOUND
}

class ProductAlreadyExistsException : SimplePaymentsExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_ALREADY_EXISTS
}
class CategoryNotFoundExistsException : SimplePaymentsExceptionHandler() {
    override fun errorCode()= ErrorCodes.CATEGORY_NOT_FOUND}
class CategoryAlreadyExistsException : SimplePaymentsExceptionHandler() {
    override fun errorCode() = ErrorCodes.CATEGORY_ALREADY_EXISTS
}

class CountNotEnoughException : SimplePaymentsExceptionHandler() {
    override fun errorCode() = ErrorCodes.COUNT_NOT_ENOUGH
}