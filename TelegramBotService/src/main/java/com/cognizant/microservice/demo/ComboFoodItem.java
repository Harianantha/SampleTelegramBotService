package com.cognizant.microservice.demo;

import java.util.ArrayList;
import java.util.List;

public class ComboFoodItem extends FoodItem {
	
	private List<FoodItem> constituentItemList;

	public List<FoodItem> getConstituentItemList() {
		return constituentItemList;
	}

	public void setConstituentItemList(List<FoodItem> constituentItemList) {
		this.constituentItemList = constituentItemList;
	}
	
	public void addConstituentItemList(FoodItem constituentItem) {
		
		if(constituentItemList==null){
			constituentItemList=new ArrayList<FoodItem>();
		}
		constituentItemList.add(constituentItem);
	}

}
