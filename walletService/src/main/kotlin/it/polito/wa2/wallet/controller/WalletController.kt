package it.polito.wa2.wallet.controller

import it.polito.wa2.dto.WalletRequestDTO
import it.polito.wa2.dto.WalletResponseDTO
import it.polito.wa2.wallet.service.WalletService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("payment")
class WalletController(val service: WalletService) {


    @PostMapping("/debit")
    fun debit(@RequestBody requestDTO: WalletRequestDTO): WalletResponseDTO? {
        return service.debit(requestDTO)
    }

    @PostMapping("/credit")
    fun credit(@RequestBody requestDTO: WalletRequestDTO) {
        service.credit(requestDTO)
    }
}
