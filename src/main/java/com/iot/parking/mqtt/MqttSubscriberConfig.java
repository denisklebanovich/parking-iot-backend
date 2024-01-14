package com.iot.parking.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.parking.parking.ParkingEventRequest;
import com.iot.parking.parking.ParkingEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MqttSubscriberConfig {

	private final ObjectMapper objectMapper = new ObjectMapper();
	@Value("${spring.integration.mqtt.default.topic}")
	private String defaultTopic;

	private final ParkingEventService parkingEventService;

	@Bean
	public DirectChannel mqttInputChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler mqttMessageHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				String payload = message.getPayload().toString();
				log.info("Received MQTT message: " + payload);
				try {
					ParkingEventRequest request = objectMapper.readValue(payload, ParkingEventRequest.class);
					parkingEventService.registerParkingEvent(request);
				} catch (Exception e) {
					log.error("Error while parsing MQTT message: " + payload, e);
				}
			}
		};
	}

	@Bean
	public IMqttMessageListener mqttMessageListener() {
		return (topic, message) -> {
			String payload = new String(message.getPayload());
			log.info("Received MQTT message: " + payload);
			mqttMessageHandler().handleMessage(MessageBuilder.withPayload(payload).build());
		};
	}
}

