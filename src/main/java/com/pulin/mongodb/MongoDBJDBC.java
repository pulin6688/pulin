package com.pulin.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDBJDBC {
	
	
	
	public static void main( String args[] ){
		aggregate();
	}
	
	
	/**
	 * 
	 */
	public static void aggregate(){
		 
		 //{ "$match"   :  { "createTime":{"$gte":new Date("2017-03-07T00:00:00")},"source":18 }  },
		 Document match = new Document("$match",new Document("source",18));
		 System.out.println(JSON.toJSON(match));
		 
		 //{"$group" : { "_id":{"shopId":"$shopId"}, "count":{"$sum":1} }             }
		 Document group = new Document("$group",new Document("_id",new Document("shopId","$shopId").append("count", new Document("$sum",1))  ));
		 group = new Document("$group",new Document("_id",new Document("shopId","$shopId")).append("count", new Document("$sum",1)));
		 System.out.println(JSON.toJSON(group));
		
		 
		 
		 Document match2 = new Document("$match",new Document("count",new Document("$gte",300)));
		 System.out.println(JSON.toJSON(match2));
		 
		 Document limit = new Document("$limit",10);
		 System.out.println(JSON.toJSON(limit));
		 
		 Document sort = new Document("$sort",new Document("count",-1));//-1 倒序   1 升序
		 System.out.println(JSON.toJSON(sort));
		
		 
		 
		 //{ "$project" :  { "_id":0, "shopId":"$_id.shopId", "count":1 }                 },
		 Document project = new Document("$project",new Document("_id",0).append("shopId", "$_id.shopId").append("count", 1));
		 System.out.println(JSON.toJSON(project));
		 
		 
		 List<Bson> pipeline = new ArrayList<Bson>();
		 pipeline.add(match);
		 pipeline.add(group);
		 pipeline.add(match2);
		 pipeline.add(project);
		 pipeline.add(limit);
		 pipeline.add(sort);
		 
		 System.out.println(JSON.toJSON(pipeline));
		 
		 MongoDatabase mongoDatabase = conn3();
		 MongoCollection<Document> collection = mongoDatabase.getCollection("order");
		 AggregateIterable<Document> agg =  collection.aggregate(pipeline);
		 MongoCursor<Document> iter =  agg.iterator();
		 int index = 0;
		 while(iter.hasNext()){
			 System.out.println(++index+":"+JSON.toJSON(iter.next()));
		 }
	}






	public static void find(){
		/*	// create our pipeline operations, first with the $match
		DBObject match = new BasicDBObject("$match", new BasicDBObject("type", "airfare") );
		// build the $projection operation
		DBObject fields = new BasicDBObject("department", 1); fields.put("amount", 1);
		fields.put("_id", 0);
		DBObject project = new BasicDBObject("$project", fields );
		// Now the $group operation
		DBObject groupFields = new BasicDBObject( "_id", "$department"); groupFields.put("average", new BasicDBObject( "$avg", "$amount")); DBObject group = new BasicDBObject("$group", groupFields);

*/

		MongoDatabase mongoDatabase = conn3();
		MongoCollection<Document> collection = mongoDatabase.getCollection("order");
		// run aggregation
		//collection.aggregate();
		//AggregationOutput output = collection.aggregate(match,project,group);
		
		 Document countD = new Document("shopId",247900001);
		 //System.out.println(collection.count(countD));
		 
		 List<Bson> pipeline = new ArrayList<Bson>();
		 
		 Document group = new Document("$group",new Document("_id",new Document("shopId","$shopId").append("count", new Document("$sum",1))  ));
		 System.out.println(JSON.toJSON(group));
		 
		 Document match = new Document("$match",new Document("count",new Document("$gte",300)));
		 System.out.println(JSON.toJSON(match));
		
		 //{ "$project" :  { "_id":0, "shopId":"$_id.shopId", "count":1 }                 },
		 Document project = new Document("$project",new Document("_id",0).append("shopId", "$_id.shopId").append("count", 1));
		 System.out.println(JSON.toJSON(project));
		 
		 pipeline.add(group);
		 pipeline.add(match);
		 pipeline.add(project);
		 
		 System.out.println(JSON.toJSON(pipeline));
		 
//		 AggregateIterable<Document> agg =  collection.aggregate(pipeline);
//		 MongoCursor<Document> iter =  agg.iterator();
//		 while(iter.hasNext()){
//			 System.out.println(iter.next());
//		 }
		
		
		/*FindIterable<Document> ff = collection.find(d);
		while(ff.iterator().hasNext()){
			System.out.println(ff.iterator().next());
		}*/
	}
	
	
	
	  public static void conn2(){
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
	  
	  public static MongoDatabase conn3(){
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
