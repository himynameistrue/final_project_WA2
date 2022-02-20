package it.polito.wa2.wallet.handlers

import it.polito.wa2.dto.TransactionRequestDTO
import it.polito.wa2.wallet.service.WalletService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

@Configuration
class OrderCreateEventHandler(val walletService: WalletService) {

    @KafkaListener(topics = ["transaction-create"], groupId = "orchestrator-group")
    fun consumer(requestDTO: TransactionRequestDTO, @Header(KafkaHeaders.CORRELATION_ID) correlationId: String,
                 @Header(KafkaHeaders.REPLY_TOPIC) replyTopic: String) {
        println("Received request")
        println(requestDTO)

        walletService.createTransactionForOutbox(requestDTO.orderId, requestDTO.buyerId, requestDTO.amount, correlationId, replyTopic);
    }
}