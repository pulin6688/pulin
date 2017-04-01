package com.pulin.test;

import java.util.ArrayList;
import java.util.List;
/**
 * 堆oom
 * 类的实例放到heap中，所以不断创建类就可以测试出heap的oom了
 * jconsole可以监控资源
 * Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
 * 
 * 
-Xms100m
-Xmx100m
-XX:+UseConcMarkSweepGC

 */
public class HeapOOM {
	static class TestHeepObject {

	}
	static List<TestHeepObject> heapList = new ArrayList<TestHeepObject>();
	public static void main(String[] args) {
		
		while (true) {
			heapList.add(new TestHeepObject());
		}

	}
}
