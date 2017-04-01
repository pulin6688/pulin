package com.pulin.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
   
   -XX:MaxPermSize=128m
   -Xms100m
   -Xmx100m
   -Xss325k
   -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled
   -XX:+UseG1GC -XX:+UseStringDeduplication
   
   在用G1 Garbage Collector时，可以开启 -XX:+UseStringDeduplication 
   参数。它通过将重复的字符串移动到同一个 char 数组中来优化堆内存的使用。该选项在Java 8u20时引用进来。
  
      在JVM内存不能调小的前提下，将-Xss设置较小，如：-Xss128k
   
   
   
   
   
 * @author pul
 *
 */
public class JvmUtils {
	
	static Map<String,String> map = new HashMap<String,String>();
	
	static List<String> list = new CopyOnWriteArrayList<>();
	
	static List<HeapOOM> heapOOMList = new ArrayList<HeapOOM>();
	
	static class HeapOOM{
		
	}
	
	static class StackOver{
		public  int stackLength=1;
		public  void stackLeak(){
			try{
				stackLength++;
				stackLeak();
			}catch(Throwable e){
				System.out.println("stackLength:"+stackLength);
				e.printStackTrace();
			}
		}
	}
	
	static class StackOverThread{
		CountDownLatch latch = new CountDownLatch(1);
		public  void run(){
			Runnable r = new Runnable(){
				public void run() {
						try {
							latch.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			};
			new Thread(r).start();
		}
	}
	
	
	
	
	
	public static void oom(){
		for(int i=0;i<100;i++){
			Runnable r = new Runnable(){
				public void run() {
					Long num=0L;
					while(true){
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						num++;
						String s = "abcdefghijklmnopqrstuvwxyz"+num+"";
						list.add(s);
					}
				}
			};
			
			new Thread(r).start();
		}	
	}
	
	
	
	public static void heapOOM(){
		Runnable r = new Runnable(){
			public void run() {
				while(true){
					heapOOMList.add(new HeapOOM());
				}
			}
		};
		Thread t = new Thread(r);
		t.setName("heapOOM_Thread");
		t.start();
	}
	
	
	
	
	public static void stackOverTest(){
		StackOver over = new StackOver();
		Runnable r = new Runnable(){
			public void run() {
				over.stackLeak();
			}
		};
		Thread t = new Thread(r);
		t.setName("stackOver-thread");
		t.start();
	}
	
	
	
	public static void stackOverThreadTest(){
		StackOverThread over = new StackOverThread();
		Runnable r = new Runnable(){
			public void run() {
				while(true){
					over.run();
				}
			}
		};
		Thread t = new Thread(r);
		t.setName("stackOverThread-thread");
		t.start();
	
	}
	
	
	 public static void main(String[] args) {
	    //al引用到常量池，保证不被gc回收。因为现在部分商用的JVM在实现永久区域的时候会full gc了。
	    //常量是存储在堆中的永久区。
//        ArrayList<String> al=new ArrayList<String>();  
//        int i=0;
//        while(true){
//            al.add(String.valueOf(i++).intern());  //intern将字符串迁移到常量池
//        }
        
		 //stackOverTest();
		 stackOverThreadTest();
		 //heapOOM();
	 }
	
	
	
	
	
	

}
