package it.polito.wa2.orchestrator.services

import it.polito.wa2.dto.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import java.util.concurrent.CompletableFuture

@Service
class OrchestratorService(
    val transactionCreateTemplate: ReplyingKafkaTemplate<String, TransactionRequestDTO, TransactionResponseDTO>,
    val inventoryChangeTemplate: ReplyingKafkaTemplate<String, InventoryChangeRequestDTO, InventoryChangeResponseDTO>,
    val inventoryCancelTemplate: ReplyingKafkaTemplate<String, InventoryCancelOrderRequestDTO, InventoryCancelOrderResponseDTO>,
    val transactionCreateRollbackTemplate: KafkaTemplate<String, WalletRequestDTO>,
    val inventoryChangeRollbackTemplate: KafkaTemplate<String, InventoryChangeResponseDTO>,
    val inventoryReturnRollbackTemplate: KafkaTemplate<String, InventoryCancelOrderResponseDTO>,
) {

}