package com.pulin.bean;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

/**
 * 请求参数封装
 */
public class Request<T> implements Serializable {

	private static final long serialVersionUID = 1990559659670684491L;

	public Request() {
		super();
	}

	protected Integer vender;

	protected String version; // 合作方版本号

	protected Long timestamp;// 时间戳

	protected String sign; // 加密签名

	private String token;
	
	@SerializedName("content")
	@JSONField(name="content")
	protected T requestBody;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getVender() {
		return vender;
	}

	public void setVender(Integer vender) {
		this.vender = vender;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setRequestBody(T requestBody) {
		this.requestBody = requestBody;
	}

	public T getRequestBody() {
		return requestBody;
	}
}
