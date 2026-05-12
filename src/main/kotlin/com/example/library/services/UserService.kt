package com.example.library.services

import com.example.library.db.TransactionManager
import com.example.library.domain.DeleteUserResult
import com.example.library.domain.RegisterUserRequest
import com.example.library.domain.RegisterUserResult
import com.example.library.domain.User
import com.example.library.repository.CheckoutRepository
import com.example.library.repository.UserRepository
import org.jdbi.v3.core.Handle

class UserService(
    private val userRepository: UserRepository,
    private val checkoutRepository: CheckoutRepository,
    private val transactionManager: TransactionManager
) {
    fun registerUser(userRequest: RegisterUserRequest): RegisterUserResult {
        val existingUser = userRepository.getUserByUsername(userRequest.username)
        if(existingUser != null) {
            return RegisterUserResult.DuplicateUsername
        }

        val registeredUser = userRepository.registerUser(userRequest)
        if (registeredUser == 1) {
            return RegisterUserResult.Success
        }

        return RegisterUserResult.Failure
    }

    fun getUser(userId: Int): User? {
        return userRepository.getUserById(userId)
    }

    fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    fun deleteUser(userId: Int): DeleteUserResult {
        fun perform(handle: Handle): DeleteUserResult {
            if (userRepository.getUserById(userId, handle) == null) {
                return DeleteUserResult.UserNotFound
            }
            if (checkoutRepository.hasActiveCheckouts(userId, handle)) {
                return DeleteUserResult.HasActiveCheckouts
            }
            userRepository.softDeleteUser(userId, handle)
            return DeleteUserResult.Success
        }
        
        return transactionManager.inTransaction(::perform)
    }
}
