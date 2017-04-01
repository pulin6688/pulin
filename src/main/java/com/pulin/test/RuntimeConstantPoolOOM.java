package com.pulin.test;

import java.util.ArrayList;
/*
 * 
 * 不断增加常量到常量池出现OOM异常
 * Exception in thread "main" java.lang.OutOfMemoryError: PermGen space
 */
public class RuntimeConstantPoolOOM {
	public static void main(String[] args) {
	     
        /*al引用到常量池，保证不被gc回收。因为现在部分商用的JVM在实现永久区域的时候会full gc了。
         * 常量是存储在堆中的永久区。
         */
        ArrayList<String> al=new ArrayList<String>();
        int i=0;
        while(true){
            al.add(String.valueOf(i++).intern());  //intern将字符串迁移到常量池
        }
    }
}
