package it.polito.wa2.wallet.service


import it.polito.wa2.dto.OrderCreateWalletResponseDTO
import it.polito.wa2.dto.WalletDTO
import it.polito.wa2.dto.TransactionDTO
import java.util.*

interface WalletService {
    fun createWalletForCustomer(customerId: Long): WalletDTO

    fun getWalletById(walletId: Long): WalletDTO

    fun getWalletByUserId(userId: Long): WalletDTO

    fun createTransaction(customerId: Long, amount: Float): TransactionDTO

    fun createTransactionForOutbox(customerId: Long, amount: Float, correlationId: String, replyTopic: String) : OrderCreateWalletResponseDTO

    fun getTransactionsByWalletIdHavingTimestampBetween(
        walletId: Long,
        fromDate: Date,
        toDate: Date
    ): Iterable<TransactionDTO>

    fun getTransactionByWalletIdAndTransactionId(walletId: Long, transactionId: Long): TransactionDTO

    fun deleteTransactionByWalletIdAndTransactionId(walletId: Long, transactionId: Long): TransactionDTO
}

/*
@Service
class WalletService {
    private var userBalanceMap: MutableMap<Int, Double>? = null

    @PostConstruct
    private fun init() {
        userBalanceMap = mutableMapOf(
            1 to 15000.0,
            2 to 1000.0,
            3 to 1000.0
        )
    }

    fun debit(requestDTO: WalletRequestDTO): WalletResponseDTO {
        val balance = userBalanceMap!!.getOrDefault(requestDTO.userId, 0.0)

        var status = PaymentStatus.PAYMENT_REJECTED
        if (balance >= requestDTO.amount) {
            userBalanceMap!![requestDTO.userId] = balance - requestDTO.amount
            status = PaymentStatus.PAYMENT_APPROVED
        }

        return WalletResponseDTO(
            requestDTO.userId,
            requestDTO.orderId,
            requestDTO.amount,
            status
        )
    }

    fun credit(requestDTO: WalletRequestDTO) {
        userBalanceMap!!.computeIfPresent(requestDTO.userId) { _: Int?, v: Double -> v + requestDTO.amount }
    }

}
*/