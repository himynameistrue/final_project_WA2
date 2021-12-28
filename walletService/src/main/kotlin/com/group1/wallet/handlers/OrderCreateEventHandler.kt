package com.group1.wallet.handlers

import com.group1.dto.WalletRequestDTO
import com.group1.dto.WalletResponseDTO
import com.group1.wallet.service.WalletService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo

@Configuration
class OrderCreateEventHandler(val walletService: WalletService) {

    @KafkaListener(topics = ["order-create-orchestrator-to-wallet"], groupId = "orchestrator-group")
    @SendTo
    fun consumer(requestDTO: WalletRequestDTO): WalletResponseDTO {
        println("Received request")
        println(requestDTO)

        return walletService.debit(requestDTO);
    }
}