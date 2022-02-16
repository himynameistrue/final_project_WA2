package it.polito.wa2.catalog.controllers

import it.polito.wa2.catalog.domain.User
import it.polito.wa2.catalog.dto.*
import it.polito.wa2.catalog.security.JwtUtils
import it.polito.wa2.catalog.services.UserDetailsService
import it.polito.wa2.dto.ProductDTO
import it.polito.wa2.dto.WalletDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
class AuthController(val userDetailsService: UserDetailsService,
                     val authenticationManager: AuthenticationManager,
                     val jwtUtils: JwtUtils,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginDTO,
        response: HttpServletResponse
    ) {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        if (authentication.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = authentication
            val jwtToken = jwtUtils.generateJwtToken(authentication)

            response.setHeader(jwtUtils.jwtHeader, "${jwtUtils.jwtHeaderStart} ${jwtToken}")
        }
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody body: RegisterDTO, br: BindingResult): ResponseEntity<Any> {
        if (br.hasErrors()) {
            var errors: Map<String, String?> = HashMap()
            br.allErrors.forEach { error ->
                errors = errors.plus(Pair((error as FieldError).field, error.getDefaultMessage()))
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        }
        if (body.password != body.confirmPassword)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Message("Password mismatch"))
        else {
            userDetailsService.create(
                body.password,
                body.email,
                false,
                User.RoleName.CUSTOMER.toString(),
                body.name,
                body.surname,
                body.deliveryAddress
            )
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Message("Registered, please confirm the email"))
    }

    @GetMapping("/registrationConfirm")
    fun confirmRegistration(@RequestParam("token") token: String): ResponseEntity<Any> {
        val userEmail = userDetailsService.validateVerificationToken(token)
        return if (userEmail!=null) {
            val customerId = userDetailsService.getIdFromEmail(userEmail)
            val uri = URI("http", null, "wallet", 8085, "/wallets", null, null)

            val responseEntity = RestTemplate().exchange(
                uri,
                HttpMethod.POST,
                HttpEntity<Long>(customerId!!),
                WalletDTO::class.java
            )

            // TODO posso essere certa che è stato creato o devo controllare?
            responseEntity.body

            ResponseEntity.status(HttpStatus.ACCEPTED).body(Message("Account enabled and created a Wallet"))
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Message("Token expired, please select sendAgain"))
        }
    }

    @GetMapping("/sendAgain")
    fun sendConfirmationAgain(@RequestParam("email") email: String): ResponseEntity<Any> {
        userDetailsService.createTokenAndSendMailConfirmation(email)

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Message("Please confirm the email"))
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleCustomException(ce: Exception): Message {
        return Message(ce.message.toString())
    }
}

class Message(val message: String?) {}