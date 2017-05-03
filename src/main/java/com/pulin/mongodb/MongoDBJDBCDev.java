package com.pulin.mongodb;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoDBJDBCDev {
	
	
	
	public static void main( String args[] ){
		MongoDatabase mongoDatabase = conn3();
		MongoCollection<Document> order = mongoDatabase.getCollection("order");
		Long count = order.count();
		System.out.println("count:"+count);
		FindIterable<Document> document =  order.find();
		while(document.iterator().hasNext()){
			System.out.println(document.iterator().next());
		}
	}
	
	








	

	  
	  public static MongoDatabase conn3(){
		  MongoDatabase mongoDatabase = null;
	        try {
				//dds-bp1c359e21a920442.mongodb.rds.aliyuncs.com:3717
				//dds-bp1c359e21a920441.mongodb.rds.aliyuncs.com:3717
	        	String address="dds-bp1c359e21a920442.mongodb.rds.aliyuncs.com";
	        	String address2="dds-bp1c359e21a920441.mongodb.rds.aliyuncs.com";
	  		    Integer port = 3717;
	            ServerAddress serverAddress = new ServerAddress(address,port);  
	            ServerAddress serverAddress2 = new ServerAddress(address2,port);
	            List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
	            addrs.add(serverAddress); 
	            addrs.add(serverAddress2);
	              
	            String name = "mg_dev_calm_gateway";
	            String db   = "calm_gateway_dev";
	            String pwd  = "FE7ywjaY3V2jFicNnONW";
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
