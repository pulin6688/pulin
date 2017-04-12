package com.pulin.redis.bak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisLock {
	
	private static Logger logger = LoggerFactory.getLogger(JedisLock.class);
	
	
	 private Jedis jedis;
	
    /**
     * 锁超时时间，防止线程在入锁以后，无限的执行等待
     */
    private int expireMsecs = 60 * 1000;
	
	 /**
     * 锁等待时间，防止线程饥饿
     */
    private int timeoutMsecs = 10 * 1000;

    private volatile boolean locked = false;
    
    /**
     * Lock key path.
     */
    private String lockKey;
    
    
    public JedisLock(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = lockKey + "_lock";
    }

    /**
     * Detailed constructor with default lock expiration of 60000 msecs.
     * @param redisTemplate
     * @param lockKey
     * @param timeoutMsecs
     */
    public JedisLock(Jedis jedis, String lockKey, int timeoutMsecs) {
        this(jedis, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    /**
	   * Detailed constructor.
	   * @param redisTemplate
	   * @param lockKey
	   * @param timeoutMsecs
	   * @param expireMsecs
	   */
    public JedisLock(Jedis jedis, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(jedis, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }
	
	
	 /**
     * Acquire lock.
     * @param jedis
     * @return true if lock is acquired, false acquire timeouted
     * @throws InterruptedException in case of thread interruption
     */
    public synchronized boolean acquire() throws InterruptedException {
        int timeout = timeoutMsecs;
        while (timeout >= 0) {
            long expires = System.currentTimeMillis() + expireMsecs + 1;
            String expiresStr = String.valueOf(expires); //锁到期时间

            if (jedis.setnx(lockKey, expiresStr) == 1) {
                // lock acquired
                locked = true;
                return true;
            }

            String currentValueStr = jedis.get(lockKey); //redis里的时间
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                // lock is expired

                String oldValueStr = jedis.getSet(lockKey, expiresStr);
                //获取上一个锁到期时间，并设置现在的锁到期时间，
                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    // lock acquired
                    locked = true;
                    return true;
                }
            }
            timeout -= 100;
            Thread.sleep(100);
        }
        return false;
    }
    
    public synchronized boolean release() throws InterruptedException {
    	   return true;
    }
    
    
    public  void exec() {
    	JedisPool pool = null;
    	Jedis jedis = pool.getResource();
        JedisLock jedisLock = new JedisLock(jedis, lockKey);
        
        jedis.setnx("", "");
        
        try {
            if (jedisLock.acquire()) { // 启用锁
                //执行业务逻辑
            } else {
                logger.info("The time wait for lock more than [{}] ms ", timeoutMsecs);
            }
        } catch (Throwable t) {
            // 分布式锁异常
            logger.warn(t.getMessage(), t);
        } finally {
            if (jedisLock != null) {
                try {
                    jedisLock.release();// 则解锁
                } catch (Exception e) {
                }
            }
            if (jedis != null) {
                try {
                    pool.returnResource(jedis);// 还到连接池里
                } catch (Exception e) {
                }
            }
        }
	}
}
