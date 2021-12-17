package com.group1.wallet.controller

import com.group1.wallet.dto.WalletRequestDTO
import com.group1.wallet.dto.WalletResponseDTO
import com.group1.wallet.service.WalletService
import org.springframework.beans.factory.annotation.Autowired
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
