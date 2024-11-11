//package me.camwalford.finnhubingestionservice.config
//
//import me.camwalford.finnhubingestionservice.model.CompanyNewsRequest
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.kafka.annotation.EnableKafka
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
//import org.springframework.kafka.core.ConsumerFactory
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory
//import org.springframework.kafka.support.serializer.JsonDeserializer
//
//@Configuration
//@EnableKafka
//class KafkaConsumerConfig {
//
//    private val logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java)
//
//    @Value("\${spring.kafka.bootstrap-servers}")
//    private lateinit var bootstrapServers: String
//
//    @Bean
//    fun consumerFactory(): ConsumerFactory<String, CompanyNewsRequest> {
//        val props = mapOf(
//            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
//            ConsumerConfig.GROUP_ID_CONFIG to "company-news-group",
//            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
//            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java
//        )
//        logger.info("Creating Kafka consumer factory with config: $props")
//
//        val jsonDeserializer = JsonDeserializer(CompanyNewsRequest::class.java).apply {
//            addTrustedPackages("me.camwalford.finnhubingestionservice.model")
//        }
//
//        return DefaultKafkaConsumerFactory(
//            props,
//            StringDeserializer(),
//            jsonDeserializer
//        )
//    }
//
//    @Bean
//    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CompanyNewsRequest> {
//        val factory = ConcurrentKafkaListenerContainerFactory<String, CompanyNewsRequest>()
//        factory.consumerFactory = consumerFactory()
//        return factory
//    }
//}
