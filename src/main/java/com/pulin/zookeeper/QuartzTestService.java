package com.pulin.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mac on 2017/11/7.
 */
@Service
public class QuartzTestService {

    private static final Logger logger = LoggerFactory.getLogger(QuartzTestService.class);

    //@Scheduled(cron="0/10 * * * * ?")
    public void test(){
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
