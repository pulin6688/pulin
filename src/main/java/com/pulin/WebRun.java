package com.pulin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication//配置控制
//@EnableAutoConfiguration(exclude = {
//        DataSourceAutoConfiguration.class
//        ,MongoAutoConfiguration.class
//        ,SolrAutoConfiguration.class,
//        //,FeignClientConfiguration.class
//})
@ComponentScan("com.pulin")//组件扫描
//@EnableAsync(proxyTargetClass = true,mode= AdviceMode.ASPECTJ)
@EnableAsync
@EnableDiscoveryClient
@EnableEurekaClient
@EnableTransactionManagement
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy = true)
@RibbonClient(value="IDSERVER")
@EnableCaching
@EnableCircuitBreaker
@EnableHystrixDashboard
public class WebRun {

    final static Logger logger = LoggerFactory.getLogger(WebRun.class);

    public static void main(String[] args) {
        SpringApplication.run(WebRun.class, args);
    }


    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
