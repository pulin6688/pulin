package com.pulin.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class RedisLock {

	private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

	private Jedis jedis;

	private volatile boolean locked = false;

	// private Lock lock = new ReentrantLock();

	// private static Semaphore semaphore = new Semaphore(1); // 机器数目

	public RedisLock(Jedis jedis) {
		this.jedis = jedis;
	}

	/**
	 * jedis get
	 * 
	 * @param key
	 * @return
	 */
	private String redisGet(final String key) {
		Object obj = null;
		try {
			obj = jedis.get(key);
		} catch (Exception e) {
			logger.error("get redis error, key : {}", key);
		}
		return obj != null ? obj.toString() : null;
	}

	/**
	 * jedis setnx
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean redisSetNx(final String key, final String value) {
		try {
			if (jedis.setnx(key, value) == 1) {
				return true;
			}

		} catch (Exception e) {
			logger.error("setNX redis error, key : {}", key);
		}
		return false;
	}

	/**
	 * jedis getset
	 * 
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

	public boolean lock() throws InterruptedException {
		// 已经加锁
		if (locked) {
			return false;
		}
		// 不用加锁
		String token = jedis.get(RedisLockUtils.tokenKey);
		if (token != null) {
			return false;
		}

		synchronized (RedisLock.class) {
			try {
				token = jedis.get(RedisLockUtils.tokenKey);// 获取token
				if (token != null) {
					return false;
				}
				int timeout = RedisLockUtils.timeoutTime;// 超时时间
				while (timeout >= 0) {
					long expires = RedisLockUtils.getLockExpireTime();
					String expiresStr = String.valueOf(expires); // 锁到期时间
					if (redisSetNx(RedisLockUtils.lockKey, expiresStr)) {// 获取到锁
						locked = true;
						return true;
					}
					// 如果没有获取到锁，需要进行超时处理
					String oldValueStr = redisGet(RedisLockUtils.lockKey); // 获取redis里的时间
					if (oldValueStr != null && Long.parseLong(oldValueStr) < System.currentTimeMillis()) {
						String currentValueStr = redisGetSet(RedisLockUtils.lockKey, expiresStr);
						if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
							locked = true;
							return true;
						}
					}
					Integer randomNumber = (int) (400 + Math.random() * 100);
					timeout = timeout - randomNumber;
					Thread.sleep(Long.parseLong(randomNumber.toString()));
				} // while end

			} catch (Exception e) {
				e.printStackTrace();
			}
		} // synchronized end
		return false;
	}

}
