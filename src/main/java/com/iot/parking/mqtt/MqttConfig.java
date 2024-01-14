package com.iot.parking.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class MqttConfig {

	@Value("${mqtt.broker.url}")
	private String brokerUrl;

	@Value("${mqtt.client.id}")
	private String clientId;

	@Value("${mqtt.topic}")
	private String topic;


	private final IntegrationFlowContext integrationFlowContext;
	private final ParkingEventService parkingEventService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public MqttPahoMessageDrivenChannelAdapter mqttInbound(MessageChannel mqttInputChannel) {
		MqttPahoMessageDrivenChannelAdapter adapter =
				new MqttPahoMessageDrivenChannelAdapter(brokerUrl, clientId, topic);
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
						parkingEventService.registerParkingEvent(request);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
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
}
