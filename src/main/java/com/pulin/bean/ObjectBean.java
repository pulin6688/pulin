package com.pulin.bean;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public class ObjectBean implements Serializable{
	
	private static final long serialVersionUID = -1201676583612462995L;

	@JSONField(name="shop_id")
	@SerializedName("shop_id")
	private long shopId;
	
	@JSONField(name="delivery_platform")
	@SerializedName("delivery_platform")
	private int deliveryPlatform;
	
	@JSONField(name="order_id")
	@SerializedName("order_id")
	private long orderId;
	
	@JSONField(name="order_no")
	@SerializedName("order_no")
	private String orderNo;

	public long getShopId() {
		return shopId;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public int getDeliveryPlatform() {
		return deliveryPlatform;
	}

	public void setDeliveryPlatform(int deliveryPlatform) {
		this.deliveryPlatform = deliveryPlatform;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
}
