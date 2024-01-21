package com.iot.parking.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.parking.parking.event.ParkingEventRequest;
import com.iot.parking.parking.event.ParkingEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
@Slf4j
@RequiredArgsConstructor
public class MqttConfig {

	@Value("${mqtt.broker.url}")
	private String brokerUrl;

	@Value("${mqtt.client.id}")
	private String clientId;

	@Value("${mqtt.topic.inbound}")
	private String inboundTopic;

	@Value("${mqtt.topic.outbound}")
	private String outboundTopic;


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
				new MqttPahoMessageDrivenChannelAdapter(brokerUrl, clientId + "-inbound", inboundTopic);
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
						mqttOutputChannel().send(MessageBuilder.withPayload(false).build());
					} catch (Exception e) {
						log.error("Error while parsing MQTT message: " + payload + ", " + e.getMessage());
					}
					return null;
				});
	}

	private void registerParkingEvent(ParkingEventRequest request) {
		try {
			var registered = parkingEventService.registerParkingEvent(request);
			mqttOutputChannel().send(MessageBuilder.withPayload(registered).build());
		} catch (Exception e) {
			log.error("Error while registering parking event: " + request.toString() + ", " + e.getMessage());
//			mqttOutputChannel().send(MessageBuilder.withPayload(false).build());
		}
	}

	@Bean
	public IntegrationFlowContext.IntegrationFlowRegistration mqttInFlowRegistration(IntegrationFlow mqttInFlow) {
		return this.integrationFlowContext.registration(mqttInFlow)
				.id("mqttInFlow")
				.register();
	}

	@Bean
	public MessageChannel mqttOutputChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutputChannel")
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "-outbound", brokerUrl);
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic(outboundTopic);
		return messageHandler;
	}

	@Bean
	public IntegrationFlow mqttOutFlow() {
		return f -> f
				.handle(mqttOutbound());
	}

	@Bean
	public IntegrationFlowContext.IntegrationFlowRegistration mqttOutFlowRegistration(IntegrationFlow mqttOutFlow) {
		return this.integrationFlowContext.registration(mqttOutFlow)
				.id("mqttOutFlow")
				.autoStartup(true)
				.register();
	}
}
