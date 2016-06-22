package com.cognizant.microservice.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cognizant.microservice.memcache.IMemcacheWrapper;
import com.meetup.memcached.MemcachedClient;

@SpringBootApplication


public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        populateProductDetails();
    }
    
    private static void populateProductDetails(){
    	
    	//MemcachedClient client=ChatUtil.getMemCacheClient();
    	IMemcacheWrapper client=ChatUtil.getMemCacheClient();
    	FoodItem samosa=new FoodItem("Samosa",Boolean.TRUE,Boolean.TRUE,new Double("75.00"),"Samosa@Rs75","Samosa",new Long(1),Boolean.FALSE);
    	FoodItem sandwich=new FoodItem("VegSandwich",Boolean.TRUE,Boolean.TRUE,new Double("100.00"),"Veg Sandwich@Rs100","Sandwich",new Long(2),Boolean.FALSE);
    	FoodItem cspc1=new FoodItem("CokeSmallPC",Boolean.TRUE,Boolean.TRUE,new Double("150.00"),"Coke+Small Popcron@Rs150","Combo1",new Long(3),Boolean.TRUE);
    	FoodItem clpc1=new FoodItem("CokeLargePC",Boolean.TRUE,Boolean.TRUE,new Double("200.00"),"Coke+Large Popcron@Rs200","Combo2",new Long(4),Boolean.TRUE);
    	
    	List<FoodItem> masterItemList=new ArrayList<FoodItem>(4);
    	masterItemList.add(samosa);
    	masterItemList.add(sandwich);
    	masterItemList.add(cspc1);
    	masterItemList.add(clpc1);
    	
    	client.add(MessagingConstants.MASTER_ITEM_LIST, masterItemList);
    	Map<String,FoodItem> masterItemMap=new HashMap<String,FoodItem>();
    	for(FoodItem item:masterItemList){
    		masterItemMap.put(item.getItemCallBackText(), item);
    	}
    	client.add(MessagingConstants.MASTER_ITEM_MAP , masterItemMap);
    	
    	//FoodItem samosa=new FoodItem("Samosa",Boolean.TRUE,Boolean.TRUE,new Double("75.0"),"Samosa@Rs75","Samosa",new Long(1),Boolean.FALSE);
    	
    }
    
}
