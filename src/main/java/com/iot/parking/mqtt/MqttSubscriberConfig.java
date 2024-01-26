package com.iot.parking.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.parking.parking.event.ParkingEventRequest;
import com.iot.parking.parking.event.ParkingEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@Slf4j
@RequiredArgsConstructor
public class MqttSubscriberConfig {

	@Value("${mqtt.broker.url}")
	private String brokerUrl;

	@Value("${mqtt.client.id}")
	private String clientId;

	@Value("${mqtt.topic.inbound}")
	private String inboundTopic;

	private final ParkingEventService parkingEventService;
	private final IntegrationFlowContext integrationFlowContext;
	private final MqttSenderConfig mqttSenderConfig;
	private final ObjectMapper objectMapper = new ObjectMapper();


	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public MqttPahoMessageDrivenChannelAdapter mqttInbound(MessageChannel mqttInputChannel) {
		MqttPahoMessageDrivenChannelAdapter adapter =
				new MqttPahoMessageDrivenChannelAdapter(brokerUrl, clientId + "-in", inboundTopic);
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel);
		return adapter;
	}

	@Bean
	public IntegrationFlow mqttInFlow() {
		return f -> f
				.channel(mqttInputChannel())
				.handle((GenericHandler<String>) (payload, headers) -> {
					log.info("Received MQTT message: " + payload);
					try {
						ParkingEventRequest request = objectMapper.readValue(payload, ParkingEventRequest.class);
						registerParkingEvent(request);
					} catch (Exception e) {
						log.error("Error while parsing MQTT message: " + payload + ", " + e.getMessage());
					}
					return null;
				});
	}

	@Bean
	public IntegrationFlowContext.IntegrationFlowRegistration mqttInFlowRegistration(IntegrationFlow mqttInFlow) {
		return this.integrationFlowContext.registration(mqttInFlow)
				.id("mqttInFlow")
				.register();
	}

	private void registerParkingEvent(ParkingEventRequest request) {
		try {
			parkingEventService.registerParkingEvent(request);
			if (request.isEntry()) {
				mqttSenderConfig.sendToEntry(Boolean.TRUE.toString());
			} else {
				mqttSenderConfig.sendToExit(Boolean.TRUE.toString());
			}
		} catch (Exception e) {
			if (request.isEntry()) {
				mqttSenderConfig.sendToEntry(Boolean.FALSE.toString());
			} else {
				mqttSenderConfig.sendToExit(Boolean.FALSE.toString());
			}
			log.error("Error while registering parking event: " + request.toString() + ", " + e.getMessage());
		}
	}
}
