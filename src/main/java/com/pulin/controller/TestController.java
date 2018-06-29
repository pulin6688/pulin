package com.pulin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pulin on 2018/6/29.
 */
@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "hello";
    }
}
