package com.pulin.controller.test;

import com.pulin.zookeeper.AsyncTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//import org.apache.log4j.spi.LoggerFactory;


@Controller
@RequestMapping("/test")
public class AopTestController {

	static Logger logger = LoggerFactory.getLogger(AopTestController.class);


	@Autowired
	private AsyncTestService asyncTestService;


    @RequestMapping(value="/aop")
    @ResponseBody
    public String aop() {
        long s = System.currentTimeMillis();
        String date = asyncTestService.test();
        long e = System.currentTimeMillis();
        return "ok_a|"+(e-s)+"|"+date;
    }
   
}
