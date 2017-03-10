package com.pulin.test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.pulin.jms.JMSProducer;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class JmsTest {

    @Autowired(required=false)
    private JMSProducer jmsProducer;

    @Test
    public void test(){
        for(int i=1;i<=10;i++){
            jmsProducer.sendMessage(i+"", i+"");
        }
    }
}
