package it.polito.wa2.orchestrator.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.requestreply.CorrelationKey
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import java.nio.charset.StandardCharsets
import java.util.*

@Configuration
class KafkaConfig(val producerFactory: ProducerFactory<*, *>) {

    /**
     * All topics this service is going to wait for
     */
    private val allTopics = arrayOf(
        "transaction-created",
        "inventory-changed",
        "inventory-returned",
    )

    /**
     * The target group for all messages
     */
    private val consumerGroup = "orchestrator-group"

    @Primary
    @Bean
    fun kafkaTemplate(): KafkaTemplate<*, *> {
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun replyingKafkaTemplate(
        pf: ProducerFactory<String?, Any?>?,
        container: KafkaMessageListenerContainer<String?, Any?>?
    ): ReplyingKafkaTemplate<*, *, *> {
        val replyingTemplate =  ReplyingKafkaTemplate(pf, container)

        replyingTemplate.setCorrelationIdStrategy {
            val bytes = UUID.randomUUID().toString().toByteArray(StandardCharsets.UTF_8)
            CorrelationKey(bytes)
        }
        return replyingTemplate;
    }

    @Bean
    fun replyContainer(cf: ConsumerFactory<String, Any>): KafkaMessageListenerContainer<String, Any> {
        val containerProperties = ContainerProperties(*allTopics)
        containerProperties.setGroupId(consumerGroup)
        return KafkaMessageListenerContainer(cf, containerProperties)
    }
}