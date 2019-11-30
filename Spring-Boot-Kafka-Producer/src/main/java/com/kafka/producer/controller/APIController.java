package com.kafka.producer.controller;

import org.apache.kafka.common.errors.InvalidTopicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.producer.model.Message;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/kafka/producer/api")
public class APIController {
	
	@Autowired
	private KafkaTemplate<String,Message> kafkaTemplate;
	@Value("${broker.name}")
    private String TOPIC;
	
	@PostMapping("/produce/{topic}")
	public ResponseEntity<String> produce(@PathVariable(value="topic",required=true) String topic,
			@RequestBody Message message) {
		try {
		StringBuilder logMsg=new StringBuilder();
		logMsg.append("API :: /kafka/producer/api/produce/"+topic+"/ Request Data: "+message.toString());
		log.debug(logMsg.toString());
		logMsg=null;
		kafkaTemplate.send(topic,message.getMsgKey(),message);
		return ResponseEntity.status(HttpStatus.OK).body("success");
		}catch(InvalidTopicException te) {
			log.error("InvalidTopicException :: "+te.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Kafka topic");
		}catch(Exception e) {
			log.error("Exception :: "+e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception occure while processing request");
		}
		
		
	}

}
