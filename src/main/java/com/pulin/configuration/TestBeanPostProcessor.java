package com.pulin.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pulin on 2018/6/30.
 */
@Component
public class TestBeanPostProcessor implements BeanPostProcessor {
    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(Thread.currentThread().getName()+"-->"+sdf.format(new Date())+"--->>>>> "+beanName);
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}