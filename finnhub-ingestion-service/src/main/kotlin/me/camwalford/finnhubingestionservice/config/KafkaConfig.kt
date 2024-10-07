package me.camwalford.finnhubingestionservice.config


import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfig() {

    @Value("\${kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun producerFactory(): ProducerFactory<String, ByteArray> {
        val configProps = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, ByteArray> {
        return KafkaTemplate(producerFactory())
    }
}