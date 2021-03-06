package it.polito.wa2.wallet.service

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.dto.TransactionResponseDTO
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

        val ret = wallet.get();

        if (!ret.enabled) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Walled disabled")
        }

        return wallet.get().toDTO()
    }

    override fun getWalletByUserId(userId: Long): WalletDTO {
        return walletRepository.findByCustomerIdAndEnabledIsTrue(userId).get().toDTO()
    }

    override fun getWalletsByUserId(userId: Long): List<WalletDTO> {
        return walletRepository.findAllByCustomerIdAndEnabledIsTrue(userId).map(Wallet::toDTO)
    }

    override fun createTransaction(orderId: Long?, customerId: Long, amount: Float): TransactionDTO {
        val optionalWallet = walletRepository.findByCustomerIdAndEnabledIsTrue(customerId);
        if (!optionalWallet.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")
        }

        return createTransactionByWalletId(orderId, optionalWallet.get().getId()!!, amount)
    }

    override fun createTransactionByWalletId(orderId: Long?, walletId: Long, amount: Float): TransactionDTO {
        val optionalCustomer = walletRepository.findByIdAndEnabledIsTrue(walletId)
        if (!optionalCustomer.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")
        }

        val customer = optionalCustomer.get()

        if (amount < 0 && customer.amount < (-amount)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction amount")
        }

        val transaction = Transaction(Date(), amount, orderId, customer)
        transactionRepository.save(transaction)

        customer.amount += amount
        walletRepository.save(customer)

        return transaction.toDTO()
    }

    override fun disableWalletById(walletId: Long): WalletDTO {
        val optWallet = walletRepository.findById(walletId)
        if (!optWallet.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")
        }
        val wallet = optWallet.get();
        wallet.enabled = false;
        walletRepository.save(wallet);
        return wallet.toDTO()
    }
    override fun disableWalletByCustomerId(customerId: Long): WalletDTO {
        val optWallet = walletRepository.findByCustomerIdAndEnabledIsTrue(customerId)
        if (!optWallet.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")
        }
        val wallet = optWallet.get()
        wallet.enabled = false

        walletRepository.save(wallet)

        return wallet.toDTO()
    }

    override fun createTransactionForOutbox(
        orderId: Long,
        customerId: Long,
        amount: Float,
        correlationId: String,
        replyTopic: String
    ): TransactionResponseDTO {

        var ret: TransactionResponseDTO
        try {
            val transactionDto = createTransaction(orderId, customerId, amount)
            ret = TransactionResponseDTO(true, transactionDto.id)

        } catch (e: Exception) {
            ret = TransactionResponseDTO(false, null)
        }

        val outbox =
            WalletOutbox(correlationId, replyTopic, ret.javaClass.name, ObjectMapper().writeValueAsString(ret));

        walletOutboxRepository.save(outbox)
        walletOutboxRepository.delete(outbox)

        return ret
    }

    override fun getTransactionsByWalletIdHavingTimestampBetween(
        walletId: Long,
        fromDate: Date,
        toDate: Date
    ): List<TransactionDTO> {
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

        val optionalWallet = walletRepository.findByIdAndEnabledIsTrue(walletId)
        if (!optionalWallet.isPresent) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found")
        }

        val wallet = optionalWallet.get()
        wallet.amount += -transactionDTO.amount

        walletRepository.save(wallet)
        transactionRepository.deleteById(transactionDTO.id)

        return transactionDTO;
    }

}