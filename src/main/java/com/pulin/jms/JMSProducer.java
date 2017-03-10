package com.pulin.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class JMSProducer {
	
	final static Logger logger = LoggerFactory.getLogger(JMSProducer.class);
	
	@Autowired(required=false)
	private JmsTemplate jmsTemplate;
	
	@Autowired(required=false)
	@Qualifier("testQueue")
	private Queue testQueue;
	
	
	@Autowired(required=false)
	@Qualifier("testTopic")
	private Topic testTopic;

	public void sendMessage(final String _id, final String consumerKey) {

		int index = (int) (Math.random()*9+1);
		Map<String, Object> param = new HashMap<>();
		param.put("_id", _id);
		param.put("consumerKey", consumerKey);
		String msg = JSON.toJSONString(param);
		
//		jmsTemplate.convertAndSend(testQueue, msg, message -> {
//			//message.setLongProperty(PRODUCTTIME, System.currentTimeMillis());
//			  message.setStringProperty("JMSXGroupID","A"+index);
//			return message;
//		});
		
		
		jmsTemplate.convertAndSend(testTopic, msg, message -> {
			message.setStringProperty("JMSXGroupID","A"+index);
			return message;
		});
		
		
	}

	
}
