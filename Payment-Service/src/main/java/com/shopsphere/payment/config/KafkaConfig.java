package com.shopsphere.payment.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.shopsphere.payment.common.event.OrderEvents;
import com.shopsphere.payment.constants.AppConstants;

@Configuration
@EnableKafka
public class KafkaConfig {
	@Bean
	public ConsumerFactory<String, OrderEvents> consumerFactory() {

	    Map<String, Object> props = new HashMap<>();

	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConstants.HOST);
	    props.put(ConsumerConfig.GROUP_ID_CONFIG, AppConstants.GROUP_ID);

	    // 🔑 ErrorHandlingDeserializer
	    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
	            org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.class);
	    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
	            org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.class);

	    // Delegate deserializers
	    props.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
	    props.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);

	    // Trust producer package OR common package
	    props.put(JsonDeserializer.TRUSTED_PACKAGES,
	            "com.shopsphere.order.domain.messages,com.shopsphere.payment.common.event");

	    // Tell Jackson target class
	    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEvents.class);
	    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

	    return new DefaultKafkaConsumerFactory<>(props);
	}

	 @Bean
	    public ConcurrentKafkaListenerContainerFactory<String, OrderEvents>
	    kafkaListenerContainerFactory() {

	        ConcurrentKafkaListenerContainerFactory<String, OrderEvents> factory =
	                new ConcurrentKafkaListenerContainerFactory<>();

	        factory.setConsumerFactory(consumerFactory());
	        return factory;
	 }
	
}
