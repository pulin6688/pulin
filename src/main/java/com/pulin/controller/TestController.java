package com.pulin.controller;

//import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/test")
public class TestController {
	
	static Logger logger = LoggerFactory.getLogger(TestController.class);
	
	
    @RequestMapping(value="/a")
    @ResponseBody
    public String a() {
        return "ok_a";
    }
    
    @RequestMapping(value="/a/b")
    @ResponseBody
    public String b() {
        return "ok_a_b";
    }
    
   
}
