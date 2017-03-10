package com.pulin.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBJDBC {
	
	public static void main( String args[] ){
		MongoDatabase mongoDatabase = conn2();
		MongoCollection<Document> collection = mongoDatabase.getCollection("order");
		System.out.println(collection.count());
	}
	
	
	
	  public static void conn(){
		  String address="dds-bp1de18c814359f41.mongodb.rds.aliyuncs.com";
		  Integer port = 3717;
	      try{   
	       // 连接到 mongodb 服务
	       MongoClient mongoClient = new MongoClient( address , port );
	       // 连接到数据库
	       MongoDatabase mongoDatabase = mongoClient.getDatabase("calm_gateway_online");  
	       System.out.println("Connect to database successfully");
	        
	      }catch(Exception e){
	        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	     }
	   }
	  
	  public static MongoDatabase conn2(){
		  MongoDatabase mongoDatabase = null;
	        try {  
	            //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址  
	            //ServerAddress()两个参数分别为 服务器地址 和 端口  
	        	String address="dds-bp1de18c814359f41.mongodb.rds.aliyuncs.com";
	        	String address2="dds-bp1de18c814359f42.mongodb.rds.aliyuncs.com";
	  		    Integer port = 3717;
	            ServerAddress serverAddress = new ServerAddress(address,port);  
	            ServerAddress serverAddress2 = new ServerAddress(address2,port);  
	            List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
	            addrs.add(serverAddress); 
	            addrs.add(serverAddress2);
	              
	            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码  
	            String name = "mg_prd_calm_gateway";
	            String db   = "calm_gateway_online";
	            String pwd  = "2NlswHw5advoFqcOpr1P";
	            MongoCredential credential = MongoCredential.createScramSha1Credential(name, db, pwd.toCharArray());  
	            List<MongoCredential> credentials = new ArrayList<MongoCredential>();  
	            credentials.add(credential);
	              
	            //通过连接认证获取MongoDB连接 
	            MongoClient mongoClient = new MongoClient(addrs,credentials);
	              
	            //连接到数据库  
	            mongoDatabase = mongoClient.getDatabase(db);
	            System.out.println("Connect to database successfully");
	            return mongoDatabase;
	        } catch (Exception e) {  
	            System.err.println( e.getClass().getName() + ": " + e.getMessage() );  
	        } 
	        return mongoDatabase;
	    } 
}
