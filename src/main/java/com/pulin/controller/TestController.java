package com.pulin.controller;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

//import org.apache.log4j.spi.LoggerFactory;
import com.alibaba.fastjson.JSON;


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


    @RequestMapping(value="/c")
    @ResponseBody
    public String ttttt(
            @RequestBody(required = false)  BaiduTakeoutRequest request,
            @RequestParam(required = false) String cmd,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String timestamp,
            @RequestParam(required = false) String ticket,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String sign,
            @RequestParam(required = false) String body
        ) {


            logger.info( JSON.toJSONString(request));
            logger.info("cmd:{}",cmd);
            logger.info("version:{}",version);
            logger.info("timestamp:{}",timestamp);
            logger.info("ticket:{}",ticket);
            logger.info("source:{}",source);
            logger.info("sign:{}",sign);
            logger.info("body:{}",body);




        return "ok_ok_ok_hahaha";
    }

    @RequestMapping(value="/d")
    @ResponseBody
    public String d(HttpServletRequest request) {


        String cmd = request.getParameter("cmd");
        String version = request.getParameter("version");
        String timestamp = request.getParameter("timestamp");
        String ticket = request.getParameter("ticket");
        String source = request.getParameter("source");
        String sign = request.getParameter("sign");
        String body = request.getParameter("body");

        if(cmd == null){
            try {
                String json =  StreamUtils.copyToString(request.getInputStream(),Charset.forName("utf-8"));
                BaiduTakeoutRequest to = JSON.parseObject(json,BaiduTakeoutRequest.class);
                logger.info("toJSONString:{}",JSON.toJSONString(to));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            logger.info("cmd:{}",cmd);
            logger.info("version:{}",version);
            logger.info("timestamp:{}",timestamp);
            logger.info("ticket:{}",ticket);
            logger.info("source:{}",source);
            logger.info("sign:{}",sign);
            logger.info("body:{}",body);
        }

        return "ok_RequestBody";
    }
    
   
}
