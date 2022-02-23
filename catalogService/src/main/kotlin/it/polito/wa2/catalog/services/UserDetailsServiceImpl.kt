package it.polito.wa2.catalog.services

import it.polito.wa2.catalog.domain.User
import it.polito.wa2.catalog.dto.InformationUpdateDTO
import it.polito.wa2.catalog.dto.UserDetailsDTO
import it.polito.wa2.catalog.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalog.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import javax.transaction.Transactional
import kotlin.RuntimeException

@Service
@Transactional
class UserDetailsServiceImpl(
            val repository: UserRepository,
            val mailService: MailService,
            val notificationService: NotificationService,
            val emailVerificationTokenRepository: EmailVerificationTokenRepository
        ): UserDetailsService {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    override fun loadUserByUsername(email: String): UserDetailsDTO? {
        val user = repository.findByEmail(email)
            ?: return null
        return user.toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun getAll(): List<InformationUpdateDTO> {
        return repository.findAll().map{user->InformationUpdateDTO(user.email, user.name, user.surname, user.deliveryAddress)}
    }

    override fun create(
        password: String,
        email: String,
        isEnabled: Boolean,
        roles: String,
        name: String,
        surname: String,
        deliveryAddress: String
    ): UserDetailsDTO {
        if (emailAlreadyExist(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "There is already an user with this email: $email")
        }

        val encPassword = passwordEncoder.encode(password)
        var user = User(encPassword, email, isEnabled, roles, name, surname, deliveryAddress)
        user = repository.save(user)

        createTokenAndSendMailConfirmation(user.email)

        return user.toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun addRole(email: String, role: User.RoleName): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        user.addRole(role)

        return repository.save(user).toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun removeRole(email: String, role: User.RoleName): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        if (user.numberRoles() == 1) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Role can't be removed, must remain at least one role.")
        }
        user.removeRole(role)

        return repository.save(user).toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun enableUser(email: String): UserDetailsDTO{
        return this.enable(email)
    }

    @Secured("ROLE_ADMIN")
    override fun disableUser(email: String): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        user.isEnabled = false

        return repository.save(user).toDTO()
    }

    override fun validateVerificationToken(token: String): String? {
        val storedToken = emailVerificationTokenRepository.findByToken(token)
        if(storedToken == null || storedToken.expiryDate.before(Date())) return null

        enable(storedToken.user.email)
        emailVerificationTokenRepository.removeByToken(token)
        return storedToken.user.email
    }

    override fun emailAlreadyExist(email: String): Boolean {
        return repository.findByEmail(email) != null
    }

    override fun createTokenAndSendMailConfirmation(email: String) {
        val expiry = LocalDateTime.now().plus(Duration.of(30, ChronoUnit.MINUTES))
        val expiryDate = Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant())
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val confirmationToken = notificationService.createEmailVerificationToken(user, expiryDate, UUID.randomUUID().toString())

        mailService.sendMessage(
            user.email,
            "Please confirm your email address",
            "Click on the following link: http://localhost:8080/auth/registrationConfirm?token=${confirmationToken.token}"
        )
    }

    private fun enable(email: String): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        user.isEnabled = true
        return repository.save(user).toDTO()
    }

    override fun updateInformation(
        email: String?,
        name: String?,
        surname: String?,
        deliveryAddress: String?,
        oldEmail: String
    ): UserDetailsDTO {
        val user = repository.findByEmail(oldEmail) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        if(name!=null)
            user.name = name
        if(surname!=null)
            user.surname = surname
        if(deliveryAddress!=null)
            user.deliveryAddress = deliveryAddress
        if(email!=null)
            user.email = email

        return repository.save(user).toDTO()
    }

    override fun updatePassword(oldPassword: String, newPassword: String, email: String): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        if (!passwordEncoder.matches(oldPassword, user.password))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The old password is wrong")

        val encPassword = passwordEncoder.encode(newPassword)
        user.password = encPassword

        return repository.save(user).toDTO()
    }

    override fun isAdmin(email: String): Boolean {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        if (user.getRoleList().contains(User.RoleName.ADMIN))
            return true
        return false
    }

    override fun isCustomer(email: String): Boolean {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        if (user.getRoleList().contains(User.RoleName.CUSTOMER))
            return true
        return false
    }

    override fun correctID(email: String, userID: Long): Boolean {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        return user.getId()==userID
    }

    override fun getIdFromEmail(email: String): Long {
        val user = repository.findByEmail(email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        return user.getId()!!
    }

    override fun getAdminsEmail(): List<String> {
        val admin = repository.findByRolesContaining("ADMIN")

        return admin.map { it -> it.email }
    }

    override fun getEmailFromId(id: Long): String {
        val user = repository.findById(id)
        if (user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        return user.get().email
    }
}