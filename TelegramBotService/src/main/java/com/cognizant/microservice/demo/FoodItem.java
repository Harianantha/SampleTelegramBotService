package com.cognizant.microservice.demo;

import java.io.Serializable;

public class FoodItem implements Serializable {
	
	private Boolean isCombo;
	public Boolean getIsCombo() {
		return isCombo;
	}
	public void setIsCombo(Boolean isCombo) {
		this.isCombo = isCombo;
	}
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private String itemName;
	private Boolean isSellable;
	private Boolean isAvailable;
	public Boolean getIsSellable() {
		return isSellable;
	}
	public void setIsSellable(Boolean isSellable) {
		this.isSellable = isSellable;
	}
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getItemDisplayName() {
		return itemDisplayName;
	}
	public void setItemDisplayName(String itemDisplayName) {
		this.itemDisplayName = itemDisplayName;
	}
	public String getItemCallBackText() {
		return itemCallBackText;
	}
	public void setItemCallBackText(String itemCallBackText) {
		this.itemCallBackText = itemCallBackText;
	}
	private Double price;
	private String itemDisplayName;
	private String itemCallBackText;
	
	public FoodItem(){
		
	}
	
	public FoodItem(String itemName,Boolean isSellable,Boolean isAvailable,Double price,String itemDisplayName,String itemCallBackText,Long id,Boolean isCombo){
		this.id=id;
		this.isAvailable=isAvailable;
		this.isSellable=isSellable;
		this.itemCallBackText=itemCallBackText;
		this.itemDisplayName=itemDisplayName;
		this.itemName=itemName;
		this.price=price;
		this.isCombo=isCombo;
		
	}
	

}
