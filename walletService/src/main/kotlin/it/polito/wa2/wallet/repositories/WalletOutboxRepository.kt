package it.polito.wa2.wallet.repositories;

import it.polito.wa2.wallet.domain.WalletOutbox
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletOutboxRepository : JpaRepository<WalletOutbox, Long> {
}