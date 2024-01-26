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

	@Value("${mqtt.topic.outbound.entry}")
	private String outboundEntryTopic;

	@Value("${mqtt.topic.outbound.exit}")
	private String outboundExitTopic;

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundEntryChannel")
	public MessageHandler mqttOutboundEntry() {
		MqttPahoMessageHandler messageHandler =
				new MqttPahoMessageHandler(brokerUrl, clientId + "-entry");
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic(outboundEntryTopic);
		return messageHandler;
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundExitChannel")
	public MessageHandler mqttOutboundExit() {
		MqttPahoMessageHandler messageHandler =
				new MqttPahoMessageHandler(brokerUrl, clientId + "-exit");
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic(outboundExitTopic);
		return messageHandler;
	}

	@Bean
	public MessageChannel mqttOutboundEntryChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel mqttOutboundExitChannel() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow mqttOutboundEntryFlow() {
		return IntegrationFlow.from("mqttOutboundEntryChannel")
				.handle(mqttOutboundEntry())
				.get();
	}

	@Bean
	public IntegrationFlow mqttOutboundExitFlow() {
		return IntegrationFlow.from("mqttOutboundExitChannel")
				.handle(mqttOutboundExit())
				.get();
	}

	@Bean
	public IntegrationFlowContext.IntegrationFlowRegistration mqttOutEntryFlowRegistration(
			@Qualifier("mqttOutboundEntryFlow") IntegrationFlow mqttOutboundEntryFlow) {
		return this.integrationFlowContext.registration(mqttOutboundEntryFlow)
				.id("mqttOutboundFlow1")
				.register();
	}

	@Bean
	public IntegrationFlowContext.IntegrationFlowRegistration mqttOutExitFlowRegistration(
			@Qualifier("mqttOutboundExitFlow") IntegrationFlow mqttOutboundExitFlow) {
		return this.integrationFlowContext.registration(mqttOutboundExitFlow)
				.id("mqttOutboundFlow2")
				.register();
	}

	public void sendToEntry(String payload) {
		mqttOutboundEntryChannel().send(MessageBuilder.withPayload(payload).build());
	}

	public void sendToExit(String payload) {
		mqttOutboundExitChannel().send(MessageBuilder.withPayload(payload).build());
	}
}


