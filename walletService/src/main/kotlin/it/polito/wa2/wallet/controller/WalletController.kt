package it.polito.wa2.wallet.controller

import it.polito.wa2.dto.TransactionDTO
import it.polito.wa2.dto.WalletDTO
import it.polito.wa2.dto.WalletRequestDTO
import it.polito.wa2.dto.WalletResponseDTO
import it.polito.wa2.enums.PaymentStatus
import it.polito.wa2.wallet.service.WalletService
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/wallets")
class WalletController(val service: WalletService) {

    //TODO: in caso di eccezione, ritorna un errore
    @GetMapping
    fun getListOfWalletByUserId(@RequestParam userId: Long): List<WalletDTO>{
        return try{
            service.getWalletsByUserId(userId)
        }
        catch (e: Exception){
            return emptyList()
        }
    }

    @GetMapping("/{walletId}")
    fun getWalletById(@PathVariable walletId: Long): WalletDTO?{
        return try{
            service.getWalletById(walletId)
        }
        catch (e: Exception){
            null
        }
    }

    @PostMapping("/{walletId}/transactions")
    fun doTransaction(@RequestBody requestDTO: WalletRequestDTO, @PathVariable walletId: Long): WalletResponseDTO? {
        return try {
            //TODO: valuta di eliminare TransactionDTO e invece innescare direttamente il WalletResponseDTO
            val transaction = service.createTransactionByWalletId(requestDTO.orderId, walletId, requestDTO.amount)
            WalletResponseDTO(transaction.customerId, requestDTO.orderId, transaction.amount, PaymentStatus.PAYMENT_APPROVED)
        } catch (e: Exception){
            WalletResponseDTO(requestDTO.userId, requestDTO.orderId, requestDTO.amount, PaymentStatus.PAYMENT_REJECTED)
        }
    }

    @PostMapping
    fun createWallet(@RequestBody customerId: Long): WalletDTO{
        return service.createWalletForCustomer(customerId);
    }

    @GetMapping("/{walletId}/transactions")
    fun getTransactions(@PathVariable walletId: Long, @RequestParam from: Long, @RequestParam to: Long): List<TransactionDTO>{
        return try{
            return service.getTransactionsByWalletIdHavingTimestampBetween(walletId, Date.from(Instant.ofEpochMilli(from)), Date.from(Instant.ofEpochMilli(to)))
        }
        catch (e: Exception){
            emptyList()
        }
    }

     @GetMapping("/{walletId}/transactions/{transactionId}")
     fun getTransactionById(@PathVariable walletId: Long, @PathVariable transactionId: Long): TransactionDTO?{
         return try{
             service.getTransactionByWalletIdAndTransactionId(walletId, transactionId)
         }
         catch (e: Exception){
             null
         }
     }

}
