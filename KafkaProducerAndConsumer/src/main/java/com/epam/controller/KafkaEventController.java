package com.epam.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.epam.model.Customer;
import com.epam.service.KafkaMessagePublisher;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class KafkaEventController {

	@Autowired
	private KafkaMessagePublisher kafkaMessagePublisher;

//	@GetMapping("/publish/{message}")
//	public ResponseEntity<?> publishMessage(@PathVariable String message)
//			throws InterruptedException, ExecutionException {
//		try {
//			for (int i = 0; i < 100000; i++) {
//				kafkaMessagePublisher.sendMessage(message + " " + i);
//			}
//			return ResponseEntity.ok("Message published successfully...");
//		} catch (Exception exception) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//	}

	@PostMapping("/publish")
	public ResponseEntity<?> publishEvent(@RequestBody Customer customer)
			throws InterruptedException, ExecutionException {
		try {
			log.info("Entered controller");
			kafkaMessagePublisher.sendEvent(customer);
			return ResponseEntity.ok("Message published successfully...");
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
