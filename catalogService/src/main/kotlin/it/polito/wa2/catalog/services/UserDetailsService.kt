package it.polito.wa2.catalog.services

import it.polito.wa2.catalog.domain.User
import it.polito.wa2.catalog.dto.InformationUpdateDTO
import it.polito.wa2.catalog.dto.UserDetailsDTO
import org.springframework.security.core.userdetails.UserDetailsService

interface UserDetailsService: UserDetailsService {
    fun create(
        password: String,
        email: String,
        isEnabled: Boolean,
        roles: String,
        name : String,
        surname: String,
        deliveryAddress: String
    ): UserDetailsDTO

    fun getAll(): List<InformationUpdateDTO>

    fun addRole(email: String, role: User.RoleName): UserDetailsDTO

    fun removeRole(email: String, role: User.RoleName): UserDetailsDTO

    fun enableUser(email: String): UserDetailsDTO

    fun disableUser(email: String): UserDetailsDTO

    fun validateVerificationToken(token: String): Boolean

    fun emailAlreadyExist(email: String): Boolean

    fun createTokenAndSendMailConfirmation(email: String)

    fun updateInformation(
               email: String?,
               name : String?,
               surname: String?,
               deliveryAddress: String?,
               oldEmail: String
    ): UserDetailsDTO

    fun updatePassword(oldPassword: String, newPassword: String, email: String): UserDetailsDTO

    fun isAdmin(email: String): Boolean

    fun correctID(email: String, userID: Long): Boolean

    fun getIdFromEmail(email: String): Long?

    fun getAdminsEmail(): List<String>
}