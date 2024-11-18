package org.example.simplepayments

object BaseUrl {
    const val PATH_ID = "/{id}"
    const val USER = "/user"
    const val PAYMENT = "/payment"
    const val TRANSACTION = "/transaction"
    const val ITEM = "/item"
    const val PRODUCT = "/product"
    const val CATEGORY = "/category"

    const val CREATE = "/create"
    const val UPDATE = "/update"
    const val DELETE = "/delete"
    const val HISTORY = "/history"
    const val DEPOSIT = "/deposit"
    const val WITHDRAW = "/withdraw"
    const val TRANSFER = "/transfer"
    const val BALANCE = "/balance"
    const val SHOW = "/show"

    const val TRANSACTION_ITEM = TRANSACTION+ ITEM
    const val USER_PAYMENT = USER+ PAYMENT
    const val DELETE_BY_ID = DELETE + PATH_ID
    const val UPDATE_BY_ID = UPDATE + PATH_ID
    const val GET_HISTORY_BY_ID = HISTORY + PATH_ID
    const val CHECK_BALANCE_BY_ID = BALANCE + PATH_ID
}
