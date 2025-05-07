package com.epam.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.epam.model.Customer;

@Service
public class KafkaMessageConsumer {

//	@KafkaListener(groupId = "gs-consumer", topics = "gs-topic")
//	public void receiveMessage1(String message) {
//		System.out.println(message + " consumer1");
//	}
//
//	@KafkaListener(groupId = "gs-consumer", topics = "gs-topic")
//	public void receiveMessage2(String message) {
//		System.out.println(message + " consumer2");
//	}
//
//	@KafkaListener(groupId = "gs-consumer", topics = "gs-topic")
//	public void receiveMessage3(String message) {
//		System.out.println(message + " consumer3");
//	}
//
//	@KafkaListener(groupId = "gs-consumer", topics = "gs-topic")
//	public void receiveMessage4(String message) {
//		System.out.println(message + " consumer4");
//	}
//	
//	@KafkaListener(groupId = "gs-consumer", topics = "gs-topic")
//	public void receiveMessage5(String message) {
//		System.out.println(message + " consumer5");
//	}
	
	@KafkaListener(groupId = "event-consumer", topics = "event-topic")
	public void receiveEvent(Customer customer) {
		System.out.println(customer.toString() + " consumer5");
	}
}
