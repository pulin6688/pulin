package com.pulin.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.pulin.bean.ObjectBean;
import com.pulin.bean.Request;



@Controller
@RequestMapping("/index")
public class IndexController {
	
	private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value="/{num}")
    @ResponseBody
    public String num(@PathVariable("num") Integer num) {
    	t(num);
        return ""+num;
    }
    
    private void t(Integer num){
    	ExecutorService exec = Executors.newCachedThreadPool();
    	for(int i=0;i<num;i++){
    		exec.submit(new Runnable(){
				public void run() {
					try {
						TimeUnit.MILLISECONDS.sleep(10);
						System.out.println(Thread.currentThread().getName()+"_"+num);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}});
    	}
    }
    
    @RequestMapping(value="/form")
    @ResponseBody
    public String aaa(@RequestParam(name="form",required=true) String form) {
    	System.out.println("form:"+form+System.currentTimeMillis());
        return "form:"+form+System.currentTimeMillis();
    }
    
    
    @RequestMapping(value="/json")
    @ResponseBody
    public String json(@RequestBody Request<ObjectBean> o) {
    	System.out.println(JSON.toJSONString(o));
        return "json:"+System.currentTimeMillis();
    }
    
    /**
     * 模拟接口
     * @param obj
     * @return
     */
    @RequestMapping(value = "/moni", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String t(@RequestBody Object obj) {
    	String json = new Gson().toJson(obj);
    	logger.info("==============================");
    	logger.info("dada:"+json);
    	logger.info("++++++++++++++++++++++++++++++");
        return json;
    }
    
}
