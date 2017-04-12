package com.pulin.redis.bak;

import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pulin.redis.RedisLockUtils;

import redis.clients.jedis.Jedis;

public class RedisLock2 {
	
	private static Logger logger = LoggerFactory.getLogger(RedisLock2.class);

	private Jedis jedis;

	private  volatile boolean locked = false;

	//private Lock lock = new ReentrantLock();

	private static Semaphore semaphore = new Semaphore(1); // 机器数目
    
    
    public RedisLock2(Jedis jedis) {
        this.jedis = jedis;
    }



    


    /**
     * jedis get
     * @param key
     * @return
     */
    private String redisGet(final String key) {
        Object obj = null;
        try {
           obj =  jedis.get(key);
        } catch (Exception e) {
            logger.error("get redis error, key : {}", key);
        }
        return obj != null ? obj.toString() : null;
    }

    
    /**
     * jedis setnx
     * @param key
     * @param value
     * @return
     */
    private boolean redisSetNx(final String key, final String value) {
        try {
            if(jedis.setnx(key, value) == 1){
            	return true;
            }
           
        } catch (Exception e) {
            logger.error("setNX redis error, key : {}", key);
        }
        return false;
    }

    
    /**
     * jedis getset
     * @param key
     * @param value
     * @return
     */
    private String redisGetSet(final String key, final String value) {
        Object obj = null;
        try {
            obj = jedis.getSet(key, value);
        } catch (Exception e) {
            logger.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (String) obj : null;
    }
    
    

    /**
     * 获得 lock.
     * 实现思路: 主要是使用了redis 的setnx命令,缓存了锁.
     * reids缓存的key是锁的key,所有的共享, value是锁的到期时间(注意:这里把过期时间放在value了,没有时间上设置其超时时间)
     * 执行过程:
     * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
     * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
     * @return true if lock is acquired, false acquire timeouted
     * @throws InterruptedException in case of thread interruption
     */
    public  boolean lock() throws InterruptedException {
    	//已经加锁
    	if(locked){
			 return false;
		}
    	//不用加锁
    	String token = jedis.get(RedisLockUtils.tokenKey);//获取token token未失效的情况不需要加锁
    	if(token != null){
    		return false;
    	}
    	
    	try{
			//lock.lock();
			semaphore.acquire();
			//System.out.println(Thread.currentThread().getName()+"|token Failure........");
			token = jedis.get(RedisLockUtils.tokenKey);// 获取token
			if (token != null) {
				return false;
			}
			
			int timeout = RedisLockUtils.timeoutTime;// 超时时间
			while (timeout >= 0) {
				//long expires = System.currentTimeMillis() + Constants.expireTime + 1;//锁超时时间
				long expires = RedisLockUtils.getLockExpireTime();
				String expiresStr = String.valueOf(expires); // 锁到期时间
				if (redisSetNx(RedisLockUtils.lockKey, expiresStr)) {// 获取到锁
					//lock acquired
					//System.out.println(Thread.currentThread().getName()+"|this.setNX is true. expires |"+expires);
					locked = true;
					return true;
				}
				
				// 如果没有获取到锁，需要进行超时处理
				String oldValueStr = redisGet(RedisLockUtils.lockKey); // 获取redis里的时间
				//System.out.println(Thread.currentThread().getName()+"|oldValueStr:"+oldValueStr);
				
				if ( oldValueStr != null && Long.parseLong(oldValueStr) < System.currentTimeMillis() ) {
					// 判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
					// lock is expired
					String currentValueStr = redisGetSet(RedisLockUtils.lockKey, expiresStr);
					//System.out.println(Thread.currentThread().getName()+"|expiresStr:"+expiresStr);
					//System.out.println(Thread.currentThread().getName()+"|currentValueStr:"+currentValueStr);
					// 获取上一个锁到期时间，并设置现在的锁到期时间,
					// 只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
					if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
						// System.out.println(Thread.currentThread().getName()+"|oldValueStr:"+oldValueStr+"|currentValueStr:"+currentValueStr);
						// 防止误删（覆盖，因为key是相同的）了他人的锁——这里达不到效果，这里值会被覆盖，但是因为什么相差了很少的时间，所以可以接受
						// [分布式的情况下]:如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
						// 获取到锁
						locked = true;
						return true;
					}
				}
				
				
				Integer d = (int) (500+Math.random()*100);
				timeout = timeout - d;
				
				/*
				 * 延迟100 毫秒, 这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即,当同时到达多个进程,
				 * 只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进行,也以同样的频率申请锁,
				 * 这将可能导致前面来的锁得不到满足. 使用随机的等待时间可以一定程度上保证公平性
				 */
				
				Thread.sleep(Long.parseLong(d.toString()));

			} // while end
    		 
    	}catch(Exception e){
    		
    	}finally{
    		//lock.unlock(); 
    		semaphore.release();
    	}
    	
        return false;
    }

    /**
     * 释放lock
     * Acqurired lock release.
     */
    public synchronized void unlock() {
        if (locked) {
        	jedis.del(RedisLockUtils.lockKey);
            locked = false;
        }
    }
    
	
	
    
    
    
    
}
