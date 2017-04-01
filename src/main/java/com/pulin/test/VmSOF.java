package com.pulin.test;
/*
 * stack outof erro
 * 
 * 方法无限递归可以测试出
 * 每个main方法启动一个栈，然后不断的递归方法进入栈(栈信息保存部分方法的返回点)，直到stack满发生sof异常
 * 
 * Exception in thread "main" java.lang.StackOverflowError
 */
public class VmSOF {
	public static void main(String[] args) {
		new VmSOF().testSOF();
	}

	private void testSOF() {
		testSOF();

	}
}
