package com.pulin.controller;

import com.pulin.configuration.auto.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pulin on 2018/6/29.
 */
@RestController
public class TestController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/test")
    public String test(){
        return "hello";
    }

    @RequestMapping(value = "/session", method = RequestMethod.GET)
    public String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }


    @RequestMapping(value = "/hello")
    public String sayHello() {
        return helloService.sayHello();
    }

}
