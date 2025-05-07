package com.epam.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.epam.model.Customer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaMessagePublisher {
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
//	public void sendMessage(String message) throws InterruptedException, ExecutionException {
//		log.info("Before sending message");
//		CompletableFuture<SendResult<String,Object>> future=kafkaTemplate.send("gs-topic",message);
//		future.whenComplete((result,ex)->{
//			if(ex==null) {
//				System.out.println("sent message= "+message+" "+result.getRecordMetadata().offset());
//			}else {
//				System.out.println("Unable to send a message "+ex.getMessage());
//			}
//		});
//		log.info("After Sending message",future.get().toString());
//	}
	
	public void sendEvent(Customer customer) throws InterruptedException, ExecutionException {
		log.info("Before sending message");
		try {
		CompletableFuture<SendResult<String,Object>> future=kafkaTemplate.send("event-topic",customer);
		future.whenComplete((result,ex)->{
			if(ex==null) {
				System.out.println("sent message= "+customer+" "+result.getRecordMetadata().offset());
			}else {
				System.out.println("Unable to send a message "+ex.getMessage());
			}
			
		});
		log.info("After Sending message",future.get().toString());
		}catch(Exception e) {
			System.out.println("Exception is "+e);
		}
		
	}

}
