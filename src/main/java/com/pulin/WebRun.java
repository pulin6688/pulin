package com.pulin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication//配置控制
//@EnableAutoConfiguration(exclude = {
//        DataSourceAutoConfiguration.class
//        ,MongoAutoConfiguration.class
//        ,SolrAutoConfiguration.class,
//        //,FeignClientConfiguration.class
//})
@ComponentScan("com.pulin")//组件扫描
//@EnableScheduling
//@EnableAsync(proxyTargetClass = true,mode= AdviceMode.ASPECTJ)

//@EnableDiscoveryClient
//@EnableEurekaClient
//@EnableTransactionManagement
//@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy = true)
public class WebRun {

    final static Logger logger = LoggerFactory.getLogger(WebRun.class);

    public static void main(String[] args) {
        SpringApplication.run(WebRun.class, args);
    }


}
