package org.example.simplepayments

class UserMapper{
    companion object{
        fun toEntity(user: UserRequest): User {
            return User(user.username,user.fullName,user.balance)
        }
        fun toResponse(user: User): UserResponse {
            return user.run { UserResponse(id!!,username,fullName,balance) }
        }
    }
}
class UserPaymentMapper{
    companion object{
        fun toResponse(userPayment: UserPaymentTransaction): UserPaymentTransactionResponse {
            return userPayment.run { UserPaymentTransactionResponse(id !! , UserMapper.toResponse(userPayment.user), userPayment.amount, userPayment.date) }
        }
    }
}

class TransactionMapper{
    companion object{
        fun toResponse(transaction: Transaction): TransactionResponse{
            return transaction.run { TransactionResponse(id!!,UserMapper.toResponse(user),totalAmount,date) }
        }
    }
}
class TransactionItemMapper{
    companion object{
        fun toResponse(transactionItem: TransactionItem): TransactionItemResponse{
            return transactionItem.run { TransactionItemResponse(id!!,ProductMapper.toResponse(product),count,amount,totalAmount,TransactionMapper.toResponse(transactionItem.transaction)) }
        }
    }
}

class ProductMapper{
    companion object{
        fun toEntity(request: ProductRequest,category: Category): Product {
            return request.run { Product(name,count,category) }
        }

        fun toResponse(product: Product): ProductResponse{
            return product.run { ProductResponse(id!!,name,count,CategoryMapper.toResponse(category)) }
        }
    }
}

class CategoryMapper{
    companion object{
        fun toResponse(category: Category): CategoryResponse{
            return category.run { CategoryResponse(id!!,name,order,description) }
        }

        fun toEntity(request:  CategoryRequest): Category {
            return request.run { Category(name,order,description) }
        }
    }
}