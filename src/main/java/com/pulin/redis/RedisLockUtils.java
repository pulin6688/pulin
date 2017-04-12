package com.pulin.redis;

public class RedisLockUtils {
	
//    public  static final String lockKey="gateway-test-dis-lock-key-_lock";
//    
//    public  static final  String tokenKey="gateway-yazuo-token";
    
    public  static final String lockKey ="gateway:crm:yazuo:token:distributed:lock:key";
    
    public  static final  String tokenKey ="gateway:crm:yazuo:token:key";
    
    /**
     * 锁过期时间，防止线程在入锁以后，无限的执行等待   毫秒
     */
    public static int lockExpireTime = 60000;
	
	 /**
     * 锁等待时间，防止线程饥饿
     */
    public static int timeoutTime = 10000;
    
    public static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 500;
    
    /**
     * token过期时间
     */
    public static int tokenExpireTime = 60 * 10;//秒
    
    /**
     * 锁超时时间计算
     */
    public static long getLockExpireTime(){
    	return System.currentTimeMillis() + lockExpireTime + 1;//锁超时时间
    }
}
