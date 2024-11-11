//package me.camwalford.finnhubingestionservice.config
//
//import kotlinx.serialization.json.Json
//import org.apache.kafka.clients.producer.ProducerConfig.*
//import org.apache.kafka.common.serialization.ByteArraySerializer
//import org.apache.kafka.common.serialization.StringSerializer
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.kafka.core.DefaultKafkaProducerFactory
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.core.ProducerFactory
//import org.springframework.kafka.support.serializer.JsonSerializer
//
//@Configuration
//class KafkaConfig() {
//
//    @Value("\${spring.kafka.bootstrap-servers}")
//    private lateinit var bootstrapServers: String
//
//    private val logger = LoggerFactory.getLogger(KafkaConfig::class.java)
//    //TODO: Look into schema registries
//    @Bean
//    fun producerFactory(): ProducerFactory<String, Any> {
//        val configProps = mapOf(
//            BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
//            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
//            VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
//        )
//        logger.info("Creating Kafka producer factory with config: $configProps")
//        return DefaultKafkaProducerFactory(configProps)
//    }
//
//    @Bean
//    fun kafkaTemplate(): KafkaTemplate<String, Any> {
//        return KafkaTemplate(producerFactory())
//    }
//}