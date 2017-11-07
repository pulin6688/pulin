package com.pulin.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mac on 2017/11/7.
 */
@Service
public class AsyncTestService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTestService.class);


    public String test(){
        String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        async();
        return s;
    }


    @Async(value = "pulin")
    public void async(){
        try {
            Thread.sleep(1000*10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logger.info(s);
    }
}
