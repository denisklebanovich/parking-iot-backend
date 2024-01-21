package com.iot.parking.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
@Slf4j
@RequiredArgsConstructor
public class MqttSenderConfig {

	private final IntegrationFlowContext integrationFlowContext;

	@Value("${mqtt.broker.url}")
	private String brokerUrl;

	@Value("${mqtt.client.id}")
	private String clientId;

	@Value("${mqtt.topic.outbound}")
	private String outboundTopic;

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler =
				new MqttPahoMessageHandler(brokerUrl, clientId + "-out");
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic(outboundTopic);
		return messageHandler;
	}

	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow mqttOutboundFlow() {
		return IntegrationFlow.from("mqttOutboundChannel")
				.handle(mqttOutbound())
				.get();
	}

	@Bean
	public IntegrationFlowContext.IntegrationFlowRegistration mqttOutFlowRegistration(
			@Qualifier("mqttOutboundFlow") IntegrationFlow mqttOutboundFlow) {
		return this.integrationFlowContext.registration(mqttOutboundFlow)
				.id("mqttOutboundFlow")
				.register();
	}

	public void send(String payload) {
		mqttOutboundChannel().send(MessageBuilder.withPayload(payload).build());
	}
}

