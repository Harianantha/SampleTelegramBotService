package com.cognizant.microservice.demo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CustomerChatSession implements Serializable {
	
	public static String ORDER_STATUS="ORDER_STATUS";
	public Long chatId;
	public Long messageId;
	public Long sessionKey;
	public String customerId;
	//KEY VALUE PAIRS
	public Map<String,Object> sessionMetrics;
	public CustomerChatSession(){
		
	}
	public CustomerChatSession(Long chatId,Long messageId,Long sessionKey,String customerId){
		
		this.chatId=chatId;
		this.messageId=messageId;
		this.sessionKey=sessionKey;
		this.customerId=customerId;
		
	}
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
	public Long getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(Long sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Map<String, Object> getSessionMetrics() {
		return sessionMetrics;
	}
	public void setSessionMetrics(Map<String, Object> sessionMetrics) {
		this.sessionMetrics = sessionMetrics;
	}
	
	public void addSessionMetrics(String key,Object value){
		if(sessionMetrics==null){
			sessionMetrics=new HashMap<String,Object>();
		}
	}

}
