package com.pulin;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class VelocityTest {
	
	 public static String exportFixedVelocity() {  
	        // 创建引擎  
	        VelocityEngine ve = new VelocityEngine();  
	        // 设置模板加载路径，这里设置的是class下  
	        ve.setProperty(Velocity.RESOURCE_LOADER, "class");  
	        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");  
	        // 进行初始化操作  
	        ve.init();  
            // 加载模板，设定模板编码  
            Template t = ve.getTemplate("templete.vm", "UTF-8"); 
	       
            
           try {  
	            // 设置初始化数据  
	            VelocityContext context = new VelocityContext();  
	            context.put("name", "张三");  
	            String[] hobbyArray={"吃饭","喝水","洗澡"};  
	            context.put("hobby", "爱好");  
	            context.put("hobbyArray", hobbyArray);  
	            // 设置输出  
	            StringWriter writer = new StringWriter();  
	            // 将环境数据转化输出  
	            t.merge(context, writer);  
	            return writer.toString();  
	  
	        } catch (Exception e) {  
	            throw new RuntimeException("模版转化错误!");  
	        }
            
            
	    }  
	      
	    public static void main(String[] args) {  
	        System.out.println(exportFixedVelocity());
	    }  
}
