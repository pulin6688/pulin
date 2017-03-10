package com.pulin.jms;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMSListener implements MessageListener {
	
	final  static Logger logger = LoggerFactory.getLogger(JMSListener.class);
	public static Long mm = 5000L;
	public void onMessage(Message message) {
		ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
		String name = msg.getGroupID();
		try {
			Thread.sleep(mm);
			logger.info(name+""+msg.toString());
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}


}
