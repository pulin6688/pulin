package com.pulin.service;


import com.netflix.hystrix.*;

/**
 * Created by pulin on 2018/6/30.
 */
public class MyHystrixCommand extends HystrixCommand<Object> {

    private Object service;
    private Object request;

    public MyHystrixCommand(Object service, Object request){
        super( HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ServiceGroup") )
                .andCommandKey(HystrixCommandKey.Factory.asKey("servcie1query") )
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("service1ThreadPool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(20))//服务线程池数量
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerErrorThresholdPercentage(60)//熔断器关闭到打开阈值
                        .withCircuitBreakerSleepWindowInMilliseconds(3000)//熔断器打开到关闭的时间窗长度
                )
        );
        this.service = service;
        this.request = request;
    
    }


    @Override
    protected Object run(){
        return null;
    }

    @Override
    protected Object getFallback(){
        return null;
    }
}
