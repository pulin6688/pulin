package com.pulin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;


//@EnableAutoConfiguration//启用自动配置
//@EnableDiscoveryClient
//@EnableEurekaClient
//@ImportResource(value = {"classpath:jms.xml"}) // 导入配置文件
@ImportResource(value = {"classpath:application-boot.xml"})
@ComponentScan("com.pulin")//组件扫描
@SpringBootApplication//配置控制
@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class
		,MongoAutoConfiguration.class
        ,SolrAutoConfiguration.class, 
        //,FeignClientConfiguration.class
        })
public class WebRun {

    final static Logger logger = LoggerFactory.getLogger(WebRun.class);

    public static void main(String[] args) {
        SpringApplication.run(WebRun.class, args);
    }



    @Bean
    public FormHttpMessageConverter addHttpMessageConverter(){
        //WebMvcConfigurationSupport
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        return formHttpMessageConverter;
    }

    @Bean
    public GsonHttpMessageConverter addGsonHttpMessageConverter(){
        //WebMvcConfigurationSupport
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        return gsonHttpMessageConverter;
    }






/*
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
    }*/



}
