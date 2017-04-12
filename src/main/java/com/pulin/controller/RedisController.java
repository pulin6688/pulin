package com.pulin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.redis.RedisLockUtils;
import com.pulin.redis.RedisLock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Controller
@RequestMapping("/redis")
public class RedisController {
	
	static Logger logger = LoggerFactory.getLogger(RedisController.class);
	
	@Autowired
	private JedisPool jedisPool;
	
    
    @RequestMapping(value="/get")
    @ResponseBody
    public String get() {
        return exec();
    }
    
   // SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH-mm:ss:ms"); 
    SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	private  String exec() {
		String r_token = null;
		Jedis jedis = jedisPool.getResource();
		RedisLock lock = new RedisLock(jedis);
		//RedisLock2 lock = new RedisLock2(jedis);
		try {
	    	r_token = jedis.get(RedisLockUtils.tokenKey);//获取token token未失效的情况不需要加锁
	    	if(r_token != null){
	    		return r_token;
	    	}
	    	
			if (lock.lock()) {//加锁成功 重新刷新token
				Long lockExpireTime = RedisLockUtils.getLockExpireTime();//锁超时时间
				jedis.set(RedisLockUtils.lockKey, lockExpireTime.toString());//key 锁  value 锁超时时间
				//jedis.expire(Constants.lockKey, 10);
				//jedis.setex(Constants.lockKey, 10, time.toString());
				
				//获取 yazuo token
				String token = UUID.randomUUID().toString();
				//jedis.set(Constants.tokenKey,token);//token
				//jedis.expire(Constants.tokenKey, 20);
				jedis.setex(RedisLockUtils.tokenKey, RedisLockUtils.tokenExpireTime, token);
				
				r_token = jedis.get(RedisLockUtils.tokenKey);
				
				System.out.println(Thread.currentThread().getName()+"|"+format.format(new Date())+"|====================="+"|getlock_exec|"+"|set_token:"+token);
				
				// 为了让分布式锁的算法更稳键些，持有锁的客户端在解锁之前应该再检查一次自己的锁是否已经超时，再去做DEL操作，因为可能客户端因为某个耗时的操作而挂起，
				// 操作完的时候锁因为超时已经被别人获得，这时就不必解锁了。 ————这里没有做
				//lock.unlock();
			}else{
				//Long time  = System.currentTimeMillis();
				//System.out.println(time+"|"+Thread.currentThread().getName()+"-get_lock:"+jedis.get(Constants.lockKey));
				//System.out.println(time+"|"+Thread.currentThread().getName()+"-get_token:"+jedis.get(Constants.tokenKey));
				r_token = jedis.get(RedisLockUtils.tokenKey);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
			jedisPool.returnResource(jedis);
		}
		return r_token;
	}
  
   
}
