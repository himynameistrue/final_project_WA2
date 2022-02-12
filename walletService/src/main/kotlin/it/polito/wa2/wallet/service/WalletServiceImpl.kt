package it.polito.wa2.wallet.service

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.dto.OrderCreateWalletResponseDTO
import it.polito.wa2.dto.WalletDTO
import it.polito.wa2.wallet.repositories.TransactionRepository
import it.polito.wa2.wallet.repositories.WalletRepository
import it.polito.wa2.wallet.domain.Transaction
import it.polito.wa2.wallet.domain.Wallet
import it.polito.wa2.dto.TransactionDTO
import it.polito.wa2.wallet.domain.WalletOutbox
import it.polito.wa2.wallet.repositories.WalletOutboxRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class WalletServiceImpl(
    val walletRepository: WalletRepository,
    val transactionRepository: TransactionRepository,
    val walletOutboxRepository: WalletOutboxRepository
) : WalletService {

    override fun createWalletForCustomer(customerId: Long): WalletDTO {
        val wallet = Wallet(0.0f, customerId)
        return walletRepository.save(wallet).toDTO()
    }


    override fun getWalletById(walletId: Long): WalletDTO {
        val wallet = walletRepository.findById(walletId)

        if (!wallet.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")
        }

        return wallet.get().toDTO()
    }

    override fun getWalletByUserId(userId: Long): WalletDTO {
        return walletRepository.findByCustomerId(userId).toDTO()
    }

    override fun createTransaction(customerId: Long, amount: Float): TransactionDTO {
        val optionalCustomer = walletRepository.findById(customerId)
        if (!optionalCustomer.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")
        }

        val customer = optionalCustomer.get()

        if (amount < 0 && customer.amount < (-amount)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction amount")
        }

        val transaction = Transaction(Date(), amount, customer)
        transactionRepository.save(transaction)

        customer.amount += amount
        walletRepository.save(customer)

        return transaction.toDTO()
    }

    override fun createTransactionForOutbox(customerId: Long, amount: Float, correlationId : String, replyTopic : String): OrderCreateWalletResponseDTO {
        return try {
            val transactionDto = createTransaction(customerId, amount)
            val ret = OrderCreateWalletResponseDTO(true, transactionDto.id)

            val outbox = WalletOutbox(correlationId, replyTopic, ret.javaClass.name, ObjectMapper().writeValueAsString(ret));

            walletOutboxRepository.save(outbox)
            //warehouseOutboxRepository.delete(outbox)

            ret
        } catch (e: Exception){
            val ret = OrderCreateWalletResponseDTO(false, null)
            ret
        }
    }

    override fun getTransactionsByWalletIdHavingTimestampBetween(
        walletId: Long,
        fromDate: Date,
        toDate: Date
    ): Iterable<TransactionDTO> {
        return transactionRepository.getTransactionsByWalletIdHavingTimestampBetween(walletId, fromDate, toDate)
            .map { t -> t.toDTO() }
    }

    override fun getTransactionByWalletIdAndTransactionId(walletId: Long, transactionId: Long): TransactionDTO {
        val optionalTransaction = transactionRepository.findById(transactionId)
        if (!optionalTransaction.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
        }

        val transaction = optionalTransaction.get()
        if (transaction.customer.getId() != walletId) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid wallet id")
        }

        return optionalTransaction.get().toDTO()
    }

    override fun deleteTransactionByWalletIdAndTransactionId(walletId: Long, transactionId: Long): TransactionDTO {
        val transactionDTO = getTransactionByWalletIdAndTransactionId(walletId, transactionId)
        transactionRepository.deleteById(transactionDTO.id);
        return transactionDTO;
    }
}