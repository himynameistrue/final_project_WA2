package it.polito.wa2.wallet.controller

import it.polito.wa2.dto.WalletRequestDTO
import it.polito.wa2.dto.WalletResponseDTO
import it.polito.wa2.enums.PaymentStatus
import it.polito.wa2.wallet.service.WalletService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("payment")
class WalletController(val service: WalletService) {


    @PostMapping("/transaction")
    fun debit(@RequestBody requestDTO: WalletRequestDTO): WalletResponseDTO? {
        return try {
            //TODO: valuta di eliminare TransactionDTO e invece innescare direttamente il WalletResponseDTO
            val transaction = service.createTransaction(requestDTO.orderId, requestDTO.userId, requestDTO.amount)
            WalletResponseDTO(transaction.customerId, requestDTO.orderId, transaction.amount, PaymentStatus.PAYMENT_APPROVED)
        } catch (e: Exception){
            WalletResponseDTO(requestDTO.userId, requestDTO.orderId, requestDTO.amount, PaymentStatus.PAYMENT_REJECTED)
        }
    }

}
