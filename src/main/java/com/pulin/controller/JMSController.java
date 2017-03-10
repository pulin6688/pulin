package com.pulin.controller;

//import org.apache.log4j.spi.LoggerFactory;

import com.pulin.jms.JMSListener;
import com.pulin.jms.JMSProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/jms")
public class JMSController {
	
	static Logger logger = LoggerFactory.getLogger(JMSController.class);
	
	@Autowired(required=false)
	private JMSProducer jmsProducer;

	
    @RequestMapping(value="/s")
    @ResponseBody
    public String s(@RequestParam(name="size") Integer size) {
		if(size == null){
			size=1;
		}
    	for(int i=1;i<=size;i++){
    		jmsProducer.sendMessage(i+"", i+"");
    	}
        return "ok:"+System.currentTimeMillis();
    }

	@RequestMapping(value="/set")
	@ResponseBody
	public String set(@RequestParam(name="mm",required =true) Long mm){
		JMSListener.mm =mm;
		return "ok";
	}
    
   
}
