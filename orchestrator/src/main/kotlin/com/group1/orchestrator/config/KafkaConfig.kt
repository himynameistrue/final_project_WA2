package com.group1.orchestrator.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate

@Configuration
class KafkaConfig(val producerFactory: ProducerFactory<*, *>) {

    /**
     * All topics this service is going to wait for
     */
    private val allTopics = arrayOf(
        "order-create-wallet-to-orchestrator",
        "order-create-warehouse-to-orchestrator",
        "order-create-rollback-wallet-to-orchestrator"
    )

    /**
     * The target group for all messages
     */
    private val consumerGroup = "orchestrator-group"

    @Bean
    fun kafkaTemplate(): KafkaTemplate<*, *>? {
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun replyingKafkaTemplate(
        pf: ProducerFactory<String?, Any?>?,
        container: KafkaMessageListenerContainer<String?, Any?>?
    ): ReplyingKafkaTemplate<*, *, *>? {
        return ReplyingKafkaTemplate(pf, container);
    }

    @Bean
    fun replyContainer(cf: ConsumerFactory<String, Any>): KafkaMessageListenerContainer<String, Any>? {
        val containerProperties = ContainerProperties(*allTopics)
        containerProperties.setGroupId(consumerGroup)
        return KafkaMessageListenerContainer(cf, containerProperties)
    }
}