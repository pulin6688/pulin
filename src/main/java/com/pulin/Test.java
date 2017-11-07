package com.pulin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

public class Test {
	public static void main(String[] args) throws Exception {

		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(1509109390*1000L)));


		List<String> list = FileUtils.readLines(new File("d://bbb.txt"));
		List<AA> list2 = new ArrayList<AA>();
		for(String s:list){
			String[] ss = s.split("\\s");
			
			AA aa = new AA();
			if(!(ss[0].equals("810018145")||ss[0].equals("810010629"))){
				aa.setEnableFlag("|NumberInt(1)"+"|");
				aa.setStatusFlag("|NumberInt(1)"+"|");
			}else{
				aa.setEnableFlag("|NumberInt(0)"+"|");
				aa.setStatusFlag("|NumberInt(2)"+"|");
			}
			aa.setBizType("|NumberInt(3)"+"|");
			aa.setBrandIdenty("|NumberLong("+ss[1]+")"+"|");
			aa.setShopIdenty("|NumberLong("+ss[0]+")"+"|");
			aa.setServerCreateTime("|new Date()"+"|");
			aa.setServerUpdateTime("|new Date()"+"|");
			aa.setSourceName("达达配送");
			aa.setSource("|NumberInt(5)"+"|");
			list2.add(aa);
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("db.getCollection('partnerShopBiz').insert(").append(new Gson().toJson(list2)).append(")");
		System.out.println(sb.toString().replaceAll("\"\\|", "").replaceAll("\\|\"", ""));
	}
	
	static class AA{
		private String   brandIdenty;
		private String   shopIdenty;
		private String   bizType;
		private String   source;
		private String   sourceName;
		private String   enableFlag;
		private String   statusFlag ;
		private String   serverCreateTime ;
		private String   serverUpdateTime;
		
		public String getBrandIdenty() {
			return brandIdenty;
		}
		public void setBrandIdenty(String brandIdenty) {
			this.brandIdenty = brandIdenty;
		}
		public String getShopIdenty() {
			return shopIdenty;
		}
		public void setShopIdenty(String shopIdenty) {
			this.shopIdenty = shopIdenty;
		}
		public String getBizType() {
			return bizType;
		}
		public void setBizType(String bizType) {
			this.bizType = bizType;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public String getSourceName() {
			return sourceName;
		}
		public void setSourceName(String sourceName) {
			this.sourceName = sourceName;
		}
		public String getEnableFlag() {
			return enableFlag;
		}
		public void setEnableFlag(String enableFlag) {
			this.enableFlag = enableFlag;
		}
		public String getStatusFlag() {
			return statusFlag;
		}
		public void setStatusFlag(String statusFlag) {
			this.statusFlag = statusFlag;
		}
		public String getServerCreateTime() {
			return serverCreateTime;
		}
		public void setServerCreateTime(String serverCreateTime) {
			this.serverCreateTime = serverCreateTime;
		}
		public String getServerUpdateTime() {
			return serverUpdateTime;
		}
		public void setServerUpdateTime(String serverUpdateTime) {
			this.serverUpdateTime = serverUpdateTime;
		}
	}
}
