package it.polito.wa2.wallet.handlers

import it.polito.wa2.dto.WalletRequestDTO
import it.polito.wa2.wallet.service.WalletService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo

@Configuration
class OrderCreateRollbackEventHandler(val walletService: WalletService)  {

    @KafkaListener(topics = ["order-create-rollback-orchestrator-to-wallet"], groupId = "orchestrator-group")
    @SendTo
    fun consumer(requestDTO: WalletRequestDTO): Boolean {
        println("Received rollback request")
        println(requestDTO)

        walletService.credit(requestDTO);

        return true;
    }
}