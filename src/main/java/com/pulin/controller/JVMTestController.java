package com.pulin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.test.JvmUtils;


@Controller
@RequestMapping("/jvm")
public class JVMTestController {
	
	static Logger logger = LoggerFactory.getLogger(JVMTestController.class);
	
	
    @RequestMapping(value="/oom")
    @ResponseBody
    public String oom() {
    	JvmUtils.heapOOM();
        return "ok_oom";
    }
    
    @RequestMapping(value="/stack")
    @ResponseBody
    public String stack() {
    	//JvmUtils.stackLeakTest();
    	JvmUtils.stackOverThreadTest();
        return "ok_stack";
    }
  
}
