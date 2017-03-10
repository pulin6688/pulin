package com.pulin;

import java.util.List;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.google.common.collect.Lists;
import com.pulin.jms.JMSListener;


//@EnableAutoConfiguration//启用自动配置
//@EnableDiscoveryClient
//@EnableEurekaClient
@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class
		,MongoAutoConfiguration.class
        ,SolrAutoConfiguration.class, 
        //,FeignClientConfiguration.class
        })
@ComponentScan("com.pulin")//组件扫描
@SpringBootApplication//配置控制
@ImportResource(value = {"classpath:jms.xml"}) // 导入配置文件
public class WebRun {

    public static void main(String[] args) {
        SpringApplication.run(WebRun.class, args);
    }








    final static Logger logger = LoggerFactory.getLogger(WebRun.class);
    @Autowired
    private JMSListener jmsListener;
    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Bean
    public List<DefaultMessageListenerContainer> bbb() {
        List<DefaultMessageListenerContainer> list = Lists.newArrayList();
        for(int i=1;i<=9;i++){
            DefaultMessageListenerContainer c = new DefaultMessageListenerContainer();
            c.setMessageListener(jmsListener);
            c.setConnectionFactory(connectionFactory);
            c.setSessionTransacted(true);
            c.setPubSubDomain(true);
            c.setConcurrency("10-100");
            c.setMessageSelector("JMSXGroupID='A"+i+"'");
            ActiveMQQueue q = new ActiveMQQueue("Consumer.test"+i+".VirtualTopic.testTopic");
            c.setDestination(q);

            logger.info("=========================:"+c);
            list.add(c);
        }
         return list;
    }
}
