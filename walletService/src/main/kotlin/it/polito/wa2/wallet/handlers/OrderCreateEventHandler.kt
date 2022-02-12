package it.polito.wa2.wallet.handlers

import it.polito.wa2.dto.OrderCreateWalletRequestDTO
import it.polito.wa2.dto.OrderCreateWalletResponseDTO
import it.polito.wa2.dto.WalletRequestDTO
import it.polito.wa2.dto.WalletResponseDTO
import it.polito.wa2.enums.PaymentStatus
import it.polito.wa2.wallet.service.WalletService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.SendTo

@Configuration
class OrderCreateEventHandler(val walletService: WalletService) {

    @KafkaListener(topics = ["order-create-orchestrator-to-wallet"], groupId = "orchestrator-group")
    fun consumer(requestDTO: OrderCreateWalletRequestDTO, @Header(KafkaHeaders.CORRELATION_ID) correlationId: String,
                 @Header(KafkaHeaders.REPLY_TOPIC) replyTopic: String) {
        println("Received request")
        println(requestDTO)

        walletService.createTransactionForOutbox(requestDTO.orderId, requestDTO.buyerId, requestDTO.amount, correlationId, replyTopic);
    }
}