package com.pulin.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by pulin on 2018/6/30.
 */
public class RateLimitService {
    // static final Executor executor  = Executors.newFixedThreadPool(100);

    static Executor executor = new ThreadPoolExecutor(10, 100,
                                      0L,TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(1000));

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public static void main(String[] args) throws Exception{
        tokenBucket();

    }


    public static void tokenBucket() throws Exception{
        // permitsPerSecond表示每秒新增的令牌数，warmupPeriod表示在从冷启动速率过渡到平均速率的时间间隔。
        final RateLimiter limiter = RateLimiter.create(10, 2, TimeUnit.MILLISECONDS);
        //表示桶容量为5且每秒新增5个令牌，即每隔200毫秒新增一个令牌；
        //final RateLimiter limiter = RateLimiter.create(2);
        for(;;){
            executor.execute(new Runnable() {
                public void run() {
                    limiter.acquire(10);
                    System.out.println(sdf.format(new Date()));
                }
            });

        }
    }


    public static void timeWindow() throws Exception{
        LoadingCache<Long, AtomicLong> counter =
                CacheBuilder.newBuilder()
                        .expireAfterWrite(2, TimeUnit.SECONDS)
                        .build(new CacheLoader<Long, AtomicLong>() {
                            @Override
                            public AtomicLong load(Long seconds) throws Exception {
                                return new AtomicLong(0);
                            }
                        });
        long limit = 1000;
        while(true) {
            //得到当前秒
            long currentSeconds = System.currentTimeMillis() / 1000;
            if(counter.get(currentSeconds).incrementAndGet() > limit) {
                System.out.println("限流了:" + currentSeconds);
                continue;
            }
            //业务处理
        }

    }
}
