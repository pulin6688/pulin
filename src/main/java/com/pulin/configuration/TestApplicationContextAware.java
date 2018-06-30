package com.pulin.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pulin on 2018/6/30.
 */
@Component
public class TestApplicationContextAware implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");
        System.out.println(sdf.format(new Date())+"IOC 容器初始化完成.......................................");


    }
}
