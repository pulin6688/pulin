package com.pulin.configuration;

import java.util.Collection;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;


@Configuration
public class GsonHttpMessageConvertersConfiguration {
    /*
     * GsonHttpMessageConverter 转换配置
     */
    //@Bean
    public HttpMessageConverters gsonHttpMessageConverters() {
        Collection<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
        // Gson 配置
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        messageConverters.add(gsonHttpMessageConverter);
        return new HttpMessageConverters(true, messageConverters);
    }
    
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        Collection<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
        // fast Json 配置
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        messageConverters.add(fastJsonHttpMessageConverter);
        return new HttpMessageConverters(true, messageConverters);
    }
}
