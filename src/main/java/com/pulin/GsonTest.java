package com.pulin;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class GsonTest {
	

	private static Logger logger = LoggerFactory.getLogger(GsonTest.class);
	


	public  static void main(String[] args){

		try {
			String json = FileUtils.readFileToString(new File("D://dish.txt"),"gbk");
			System.out.println(json);
			Gson gson = new Gson();
			java.lang.reflect.Type type = new TypeToken<Mind>(){}.getType();
			Mind to = gson.fromJson(json, type);
			//System.out.println(gson.toJson(to));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	static class Mind{
		private Integer code;
		private List<Object> data;
		private Boolean success;
		private Boolean isHasNext;

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public List<Object> getData() {
			return data;
		}

		public void setData(List<Object> data) {
			this.data = data;
		}

		public Boolean getSuccess() {
			return success;
		}

		public void setSuccess(Boolean success) {
			this.success = success;
		}

		public Boolean getHasNext() {
			return isHasNext;
		}

		public void setHasNext(Boolean hasNext) {
			isHasNext = hasNext;
		}
	}
}
