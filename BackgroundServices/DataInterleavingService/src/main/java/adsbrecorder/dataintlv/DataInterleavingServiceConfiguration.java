package adsbrecorder.dataintlv;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import com.mongodb.MongoClient;

import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.kafka.TrackingRecordDeserializer;

@EnableKafka
@Configuration
public class DataInterleavingServiceConfiguration {
    
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${mongodb.address}")
    private String mongodbAddress;

    @Value(value = "${mongodb.port:27017}")
    private int mongodbPort;

    @Value(value = "${mongodb.collection}")
    private String mongodbCollectionName;

    @Bean
    public MongoClient mongo() throws Exception {
        return new MongoClient(mongodbAddress, mongodbPort);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), mongodbCollectionName);
    }

    @Bean
    public ConsumerFactory<String, TrackingRecord> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, 1);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TrackingRecordDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }
 
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TrackingRecord> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TrackingRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
