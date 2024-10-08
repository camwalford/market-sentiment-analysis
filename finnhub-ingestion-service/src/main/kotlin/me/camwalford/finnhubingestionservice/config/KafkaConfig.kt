package me.camwalford.finnhubingestionservice.config


import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfig() {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    private val logger = LoggerFactory.getLogger(KafkaConfig::class.java)

    @Bean
    fun producerFactory(): ProducerFactory<String, MarketNewsList> {
        val configProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java
        )
        logger.info("Creating Kafka producer factory with config: $configProps")
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, MarketNewsList> {
        return KafkaTemplate(producerFactory())
    }
}