package com.cognizant.microservice.demo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {
	
	public String orderNumber;
	public Long chatId;
	public Long getChatId() {
		return chatId;
	}
	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}
	public Long getMessageId() {
		return messageId;
	}
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	public Long messageId;
	public List<FoodItem> orderList;
	public Map<String,Integer>  itemQuantityMap=null;
	public Map<String, Integer> getItemQuantityMap() {
		return itemQuantityMap;
	}
	public void setItemQuantityMap(Map<String, Integer> itemQuantityMap) {
		this.itemQuantityMap = itemQuantityMap;
	}
	public void addItemQuantityMap(String itemName,Integer quantity) {
		if(this.itemQuantityMap==null){
			itemQuantityMap=new HashMap<String, Integer>();
		}	
		itemQuantityMap.put(itemName, quantity);
	}
	public String customerId;
	public String location;
	public String promoCode;
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public List<FoodItem> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<FoodItem> orderList) {
		this.orderList = orderList;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPromoCode() {
		return promoCode;
	}
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	public Double getTotalOrderAmount() {
		return totalOrderAmount;
	}
	public void setTotalOrderAmount(Double totalOrderAmount) {
		this.totalOrderAmount = totalOrderAmount;
	}
	public Double totalOrderAmount;
	
	
	

}
