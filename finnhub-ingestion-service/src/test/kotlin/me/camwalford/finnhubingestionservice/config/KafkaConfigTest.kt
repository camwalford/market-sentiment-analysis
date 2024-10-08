//package me.camwalford.finnhubingestionservice.config
//
//import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.StringSerializer
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import org.springframework.kafka.core.DefaultKafkaProducerFactory
//
//class KafkaConfigTest {
//
//    @Test
//    fun `test producerFactory creation`() {
//        // Arrange
//        val kafkaConfig = KafkaConfig()
//
//        // Act
//        val producerFactory = kafkaConfig.producerFactory()
//
//        // Assert
//        assertNotNull(producerFactory)
//        val configProps = producerFactory.configurationProperties
//        assertEquals("localhost:9092", configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG])
//        assertEquals(StringSerializer::class.java, configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG])
//        assertEquals("io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer", configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG]?.toString())
//    }
//
//    @Test
//    fun `test kafkaTemplate creation`() {
//        // Arrange
//        val kafkaConfig = KafkaConfig()
//
//        // Act
//        val kafkaTemplate = kafkaConfig.kafkaTemplate()
//
//        // Assert
//        assertNotNull(kafkaTemplate)
//        val producerFactory = kafkaTemplate.producerFactory
//        assertNotNull(producerFactory)
//    }
//}
