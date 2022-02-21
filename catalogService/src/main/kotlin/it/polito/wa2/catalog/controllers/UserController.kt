package it.polito.wa2.catalog.controllers

import it.polito.wa2.catalog.domain.User
import it.polito.wa2.catalog.dto.InformationUpdateDTO
import it.polito.wa2.catalog.dto.UserDetailsDTO
import it.polito.wa2.catalog.dto.PasswordUpdateDTO
import it.polito.wa2.catalog.services.UserDetailsService
import it.polito.wa2.dto.WalletDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import javax.validation.Valid

@RestController
@RequestMapping("/user")
class UserController(
    val userDetailsService: UserDetailsService,
    val gatewayController: GatewayController
) {
    @PatchMapping("/updateInformation")
    fun updateInformation(@Valid @RequestBody body: InformationUpdateDTO, br: BindingResult): ResponseEntity<Any> {
        if (br.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Message(br.allErrors[0].defaultMessage))
        }
        val principal = (SecurityContextHolder.getContext().authentication)
        userDetailsService.updateInformation(body.email, body.name, body.surname, body.deliveryAddress, principal.name)
        if (body.email != null) {
            return ResponseEntity.status(HttpStatus.OK)
                .body(Message("Information updated: email updated, please login again"))
        }
        return ResponseEntity.status(HttpStatus.OK).body(Message("Information updated"))
    }

    @PutMapping("/updatePassword")
    fun updatePassword(@Valid @RequestBody body: PasswordUpdateDTO, br: BindingResult): ResponseEntity<Any> {
        if (br.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Message(br.allErrors[0].defaultMessage))
        }
        if (body.newPassword != body.confirmPassword)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Message("New password mismatch"))
        val principal = (SecurityContextHolder.getContext().authentication)

        userDetailsService.updatePassword(body.oldPassword, body.newPassword, principal.name)

        return ResponseEntity.status(HttpStatus.OK).body(Message("Password updated"))
    }

    @GetMapping("/retrieveInformation")
    fun retrieveInformation(): InformationUpdateDTO {
        val principal = (SecurityContextHolder.getContext().authentication)
        val user = userDetailsService.loadUserByUsername(principal.name) as UserDetailsDTO

        return InformationUpdateDTO(user.email, user.name, user.surname, user.deliveryAddress)
    }

    @PostMapping("{username}/enable")
    fun adminEnableUser(@PathVariable("username") username: String) {
        userDetailsService.enableUser(username)

        if (userDetailsService.isCustomer(username)) {
            // Add a wallet too
            gatewayController.createWallet(username)
        }
    }

    @PostMapping("{username}/disable")
    fun adminDisableUser(@PathVariable("username") username: String) {
        userDetailsService.disableUser(username)
    }

    @PostMapping("{username}/addRole")
    fun adminAddRole(@PathVariable("username") username: String, @RequestParam("role") role: String) {
        userDetailsService.addRole(username, User.RoleName.valueOf(role))

        if (role.compareTo("CUSTOMER") == 0) {
            gatewayController.createWallet(username)
        }
    }

    @PostMapping("{username}/removeRole")
    fun adminRemoveRole(@PathVariable("username") username: String, @RequestParam("role") role: String) {
        userDetailsService.removeRole(username, User.RoleName.valueOf(role))

        if (role.compareTo("CUSTOMER") == 0) {
            val customerId = userDetailsService.getIdFromEmail(username)
            val uri = URI("http", null, "wallet", 8085, "/wallets/$customerId", null, null)

            try {
                RestTemplate().exchange(
                    uri,
                    HttpMethod.DELETE,
                    null,
                    WalletDTO::class.java
                )
            } catch (e: HttpStatusCodeException) {
                throw ResponseStatusException(e.statusCode, e.message)
            }

        }
    }

    @GetMapping("/list")
    fun listUsers(): List<InformationUpdateDTO> {
        return userDetailsService.getAll()
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleCustomException(ce: Exception): Message {
        return Message(ce.message.toString())
    }
}