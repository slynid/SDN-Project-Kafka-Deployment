package com.kafka.producer.scheduler;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.common.errors.InvalidTopicException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kafka.producer.model.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WeatherDataReciever {
	 private static final SimpleDateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY HH:mm:ss");
	 private final AtomicLong counter = new AtomicLong();
	
	 @Value("${broker.name}")
	 private String TOPIC; 
	@Autowired
	private KafkaTemplate<String,Message> kafkaTemplate;
	
	@Scheduled(fixedRate = 120000)
	  public void getCurrencyExchangeData() {
		String logHead="getCurrencyExchangeData(USD to INR ) :: "+dateFormat.format(new Date())+" :: ";
	   // log.info("The time is now {}", dateFormat.format(new Date()));
		
		try {
		String url="https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=INR&apikey=O3M37L0UKP38QNW1";
		RestTemplate restTemplate = new RestTemplate();
		
		String response=restTemplate.getForObject(url, String.class);
		Message message=new Message();
		message.setMsgKey(""+System.currentTimeMillis());
		message.setMsgVal(response);
		log.info(logHead+"Fetching Data From :: "+url);
		log.info(logHead+"Data :: "+message);
		kafkaTemplate.send(TOPIC,message.getMsgKey(),message);
		log.info(logHead+"Data send to Topic "+TOPIC);
		}catch(InvalidTopicException te) {
			log.error("InvalidTopicException :: "+te.getMessage());
		}catch(Exception e) {
			log.error("Exception :: "+e.getMessage());
		}
	  }
}
