package it.polito.wa2.wallet.controller;

import it.polito.wa2.dto.WalletDTO
import it.polito.wa2.wallet.service.WalletService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customers")
class CustomerController(val service: WalletService) {

    @DeleteMapping("/{customerId}")
    fun disableWallet(@PathVariable customerId: Long): WalletDTO {
        return service.disableWalletByCustomerId(customerId)
    }
}