package com.group1.orchestrator.handlers

import com.group1.dto.OrchestratorRequestDTO
import com.group1.dto.OrchestratorResponseDTO
import com.group1.orchestrator.service.OrchestratorService
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo

@Configuration
class OrderCreatedEventHandler(val orchestratorService: OrchestratorService) {

    @KafkaListener(topics = ["order-created"], groupId = "orchestrator-group")
    @SendTo
    fun consumer(requestDTO: OrchestratorRequestDTO): OrchestratorResponseDTO? {
        println("Received request")
        println(requestDTO)
        return orchestratorService.orderProduct(requestDTO).block();
    }
}