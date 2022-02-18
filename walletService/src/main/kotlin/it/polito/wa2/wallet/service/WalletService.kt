package it.polito.wa2.wallet.service


import it.polito.wa2.dto.TransactionResponseDTO
import it.polito.wa2.dto.WalletDTO
import it.polito.wa2.dto.TransactionDTO
import java.util.*

interface WalletService {
    fun createWalletForCustomer(customerId: Long): WalletDTO

    fun getWalletById(walletId: Long): WalletDTO

    fun getWalletByUserId(userId: Long): WalletDTO

    fun getWalletsByUserId(userId: Long): List<WalletDTO>

    fun createTransaction(orderId: Long?, customerId: Long, amount: Float): TransactionDTO

    fun createTransactionByWalletId(orderId: Long?, walletId: Long, amount: Float): TransactionDTO

    fun createTransactionForOutbox(orderId: Long, customerId: Long, amount: Float, correlationId: String, replyTopic: String) : TransactionResponseDTO

    fun getTransactionsByWalletIdHavingTimestampBetween(
        walletId: Long,
        fromDate: Date,
        toDate: Date
        ): List<TransactionDTO>

    fun getTransactionByWalletIdAndTransactionId(walletId: Long, transactionId: Long): TransactionDTO

    fun deleteTransactionByWalletIdAndTransactionId(walletId: Long, transactionId: Long): TransactionDTO

    fun disableWalletById(walletId: Long): WalletDTO
}
