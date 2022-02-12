package it.polito.wa2.wallet.repositories

import it.polito.wa2.wallet.domain.Transaction
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TransactionRepository : CrudRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :wallet_id AND t.timestamp >= :from_date AND t.timestamp <= :to_date")
    fun getTransactionsByWalletIdHavingTimestampBetween(
        @Param("wallet_id") walletID: Long,
        @Param("from_date") fromDate: Date,
        @Param("to_date") toDate: Date
    ): Iterable<Transaction>
}