package com.cognizant.microservice.demo;

import java.io.StringBufferInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.microservice.memcache.IMemcacheWrapper;
import com.meetup.memcached.MemcachedClient;



@RestController
@RequestMapping("/chat")
public class TelegramChatController {

	private final static Logger LOGGER = Logger.getLogger(TelegramChatController.class.getName()); 

//	private static String ACKNOWLEDGEMENT_MESSAGE="Thanks. Your vehicle will be ready for you";
    private final static String CLASSNAME="TelegramChatController";
    
    
    public static String ORDER_STATUS="ORDER_STATUS";
    public static String PENDING="PENDING";
    
    @RequestMapping(method=RequestMethod.POST)
    public void process(@RequestBody String payload) {
    	String methodName="process";
    	LOGGER.entering(CLASSNAME, methodName);
    	System.out.println("Request body is:"+payload);
    	JsonReader reader=Json.createReader(new StringBufferInputStream(payload));
    	//JsonStructure structure=reader.read();
    	JsonObject structure=reader.readObject();
    	//JsonString stringValue=(JsonString)structure.get("text");

 
    	int chatId=0;
    	int messageId=0;
    	String messagePostedByCustomer=null;
    	
    	
    		//System.out.println("result object:"+result.toString());
    	if(structure!=null && structure.containsKey("callback_query")){
    		System.out.println("Request is a callBack Request");
    		//MemcachedClient client = ChatUtil.getMemCacheClient();
    		IMemcacheWrapper client=ChatUtil.getMemCacheClient();
    	    		
    		
    		JsonObject callBackObject=structure.getJsonObject("callback_query");
    		JsonObject messageObject=callBackObject.getJsonObject("message");
    		chatId=messageObject.getJsonObject("chat").getInt("id");
    		messageId=messageObject.getInt("message_id");
    		int timestamp=messageObject.getInt("date");
    		String callBackData=callBackObject.getString("data");
    		//User clickd chosen all. Give option to get confirmation
    		if(MessagingConstants.FINAL_OPTION.equalsIgnoreCase(callBackData)){
    			String confirmationMessage=getMoreOptionConfirmation( chatId, messageId);
    			System.out.println("Message to be sent:"+confirmationMessage);
    			ChatUtil.sendReplyMessage(confirmationMessage);
    			
    			
    		}
    		//Enter quantities or if user has entered quantit y for an item, update it
    		else if(callBackData.contains(MessagingConstants.GOTO_ENTERQUANTTIES) || callBackData.contains(":qty")){
    			//Ask confirmation about items chosen and ask if anything else need to be chose
    			//If not, ask quantity for each
    			//Once all items are confirmed, give total amount and link to pay
    			//MemcachedClient memcachedClient=ChatUtil.getMemCacheClient();
    			IMemcacheWrapper memcachedClient=ChatUtil.getMemCacheClient();
    			int firstOccurance=callBackData.indexOf(":");
    			String[]values=callBackData.split(":");
    			messageId=Integer.valueOf(values[0]);
    			String sessionObjectKey=chatId+"_"+messageId;
    			CustomerChatSession createOrUpdateChatSession=createOrUpdateChatSession(chatId, messageId, memcachedClient, 0, sessionObjectKey);
    			String orderKey=chatId+"_"+messageId+"_"+createOrUpdateChatSession.getSessionKey();
    			System.out.println("ORDER KEY TO RETRIEVE:"+orderKey);
    			Order clientOrder=(Order)memcachedClient.get(orderKey);
    			Map<String, Integer> orderItemQuantityMap=clientOrder.getItemQuantityMap();
    			//If the control comes here by user clicking on quantitys for an item, update the quuantities and show message to
    			//enter quantities for next available item
    			if(callBackData.contains(":qty")){
    				
    				String itemName=values[1];
    				System.out.println("ITEM NAME FOR WHICH QUANTITY ENTERED:"+itemName);
    				orderItemQuantityMap.put(itemName, Integer.valueOf(values[3]));
    				clientOrder.setItemQuantityMap(orderItemQuantityMap);
    				memcachedClient.replace(orderKey, clientOrder);
    				
    			}
    			Set<String> keys=orderItemQuantityMap.keySet();
    			Iterator<String> keysIteartor=keys.iterator();
    			String key=null;
    			//Integer quantity=0;
    			boolean proceedToCheckout=true;
    			while(keysIteartor.hasNext()){
    				key=keysIteartor.next();
    				if(orderItemQuantityMap.get(key).intValue()==0){
    					proceedToCheckout=false;
    					String replyMessage=getQuantitiesForItem(chatId, messageId,key);
    					System.out.println("REPLY MESSAGE FOR ENTERING QUANTITIES:"+replyMessage);
    					ChatUtil.sendReplyMessage(replyMessage);
    					break;
    				}
    				
    			}
    			if(proceedToCheckout){
    				//TODO ADD LOGIC FOR ORDERNUMBER
    				
    				Set<String> keys2=orderItemQuantityMap.keySet();
        			Iterator<String> keysIteartor2=keys.iterator();
        		
        			Double totalValue=0.0;
        			String keyInMap=null;
        			Double itemValue=0.0;
        			Double itemMasterValue=0.0;
        			int itemQuantity=0;
        			String itemName=null;
        			Map<String,FoodItem> masterItemMap=(Map<String,FoodItem>)memcachedClient.get(MessagingConstants.MASTER_ITEM_MAP);
        			JsonObjectBuilder keyBoardBuilder=Json.createObjectBuilder();
        			JsonArrayBuilder keyBoardArrayBuilderOuter=Json.createArrayBuilder();
        			JsonArrayBuilder headerarrayBuilder=Json.createArrayBuilder();
        			JsonObjectBuilder headerObject=Json.createObjectBuilder();
        			StringBuilder reply=new StringBuilder( "ITEMNAME--QUANTITY--ITEMVALUE");
        			//reply.append("\n");
        			headerObject.add(MessagingConstants.TEXT, "ITEMNAME			QUANTITY			ITEMVALUE");
        			headerarrayBuilder.add(headerObject);
        			keyBoardArrayBuilderOuter.add(headerarrayBuilder);
        			
        			JsonArrayBuilder itemRowArraybuilder=null;
        			JsonObjectBuilder itemObjectBuilder=null;
        			while(keysIteartor2.hasNext()){
        				keyInMap=keysIteartor2.next();
        				itemName=masterItemMap.get(keyInMap).getItemDisplayName();
        				itemQuantity=orderItemQuantityMap.get(keyInMap).intValue();
        				itemMasterValue=masterItemMap.get(keyInMap).getPrice();
        				itemValue=itemQuantity*itemMasterValue;
        				itemName=masterItemMap.get(keyInMap).getItemDisplayName();
        				itemObjectBuilder=Json.createObjectBuilder();
        				
        				itemObjectBuilder.add(MessagingConstants.TEXT, itemName+"			"+itemQuantity+"			"+itemValue);
        				itemRowArraybuilder=Json.createArrayBuilder();
        				itemRowArraybuilder.add(itemObjectBuilder);
        				keyBoardArrayBuilderOuter.add(itemRowArraybuilder);
        				reply.append(itemName+"--"+itemQuantity+"--"+itemValue);
        				totalValue=totalValue+itemValue;
        				//reply.append("\n");
        			}
        			
        			JsonArrayBuilder totalValueArray=Json.createArrayBuilder();
        			JsonObjectBuilder totalValueObject=Json.createObjectBuilder();
        			totalValueObject.add(MessagingConstants.TEXT, "TOTAL VALUE			"+totalValue);
        			reply.append( "TOTAL VALUE--"+totalValue);
        			totalValueArray.add(totalValueObject);
        			keyBoardArrayBuilderOuter.add(totalValueArray);
        			keyBoardBuilder.add("inline_keyboard", keyBoardArrayBuilderOuter);
        			
        			//TODO ADD LINK TO MAKE PAYMENT
        			String replyText=getCartOverViewMessage(chatId,messageId,reply.toString());
        			System.out.println("Cart Page to be shown:"+replyText);
        			ChatUtil.sendReplyMessage(replyText);
    			}
    			
    			
    			
    		}
    		//Proceed to checkout
    		else if(MessagingConstants.GOTO_CHECKOUT.equalsIgnoreCase(callBackData)){
    			//Get items and quantities from session.
    			//Get order total.
    			//Construct reply message.
    			
    		}
    		//Populate cache with items selected
    		else{
    			populateItemSelectionCallBack(chatId, messageId, client, timestamp, callBackData);	
    		}
    		
    		return;
    	}
    		if(structure!=null){
    			//System.out.println("parentMessageObject object:"+parentMessageObject.toString());
    			
    			JsonObject messageObject=structure.getJsonObject("message");
    			if(messageObject!=null){
    				
    				messagePostedByCustomer=messageObject.getString("text");
    				JsonObject chatObject=messageObject.getJsonObject("chat");
        			chatId=chatObject.getInt("id");
        			messageId=messageObject.getInt("message_id");
    					
    			}else{
    				System.out.println("messageObject is null");
    			}
    			
            		
    		}else{
    			System.out.println("parentMessageObject is null");
    		}
        		
    	
    	String replyMessage=getConstructedReplyURL(messagePostedByCustomer,chatId,messageId);
    	System.out.println("Reply URL to be posted:"+replyMessage);
    	
    	
    	ChatUtil.sendReplyMessage(replyMessage);
		    	
       
    }



	private void populateItemSelectionCallBack(int chatId, int messageId, IMemcacheWrapper client, int timestamp,
			String callBackData) {
		String sessionObjectKey=chatId+"_"+messageId;
		System.out.println("Before calling createOrUpdateChatSession");
		CustomerChatSession chatSession = createOrUpdateChatSession(chatId, messageId, client, timestamp,
				sessionObjectKey);
		System.out.println("Before calling createOrUpdateSessionOrder");
		createOrUpdateSessionOrder(chatId, messageId, client, callBackData, chatSession);
	}

	private void createOrUpdateSessionOrder(int chatId, int messageId, IMemcacheWrapper client, String callBackData,
			CustomerChatSession chatSession) {
		String orderKey=chatId+"_"+messageId+"_"+chatSession.getSessionKey();
		Order clientOrder=null;
		System.out.println("Order key:"+orderKey);
		if(!client.keyExists(orderKey)){
			System.out.println("Client order does not exist in Memacache.Creating new");
		 clientOrder=new Order();
		 //client.set(orderKey, clientOrder);
		 client.add(orderKey, clientOrder);
		}else{
			System.out.println("Client order exist in Memacache.");
			clientOrder=(Order)client.get(orderKey);
		}
		if(callBackData.equalsIgnoreCase("Done with Selection.Enter QUantities")){
			System.out.println("Done with selection");
		}else{
			
			System.out.println("Still more to select");
			clientOrder.addItemQuantityMap(callBackData, new Integer(0));
			//client.set(orderKey, clientOrder);
			client.replace(orderKey, clientOrder);
		}
		
	}

	private CustomerChatSession createOrUpdateChatSession(int chatId, int messageId, IMemcacheWrapper client,
			int timestamp, String sessionObjectKey) {
		CustomerChatSession chatSession=null;
		if(!client.keyExists(sessionObjectKey)){
			 chatSession=new CustomerChatSession(Long.valueOf(chatId),Long.valueOf(messageId),Long.valueOf(timestamp),null);
		//	 chatSession.setSessionKey(Long.valueOf(timestamp));
			//client.set(sessionObjectKey, chatSession);
			 chatSession.addSessionMetrics(ORDER_STATUS, PENDING);
			client.add(sessionObjectKey, chatSession);
			
			System.out.println("Client Session Doesnot exist.Creating new");
			
		}else{
			System.out.println("Client Session  exist");
			chatSession=(CustomerChatSession)client.get(sessionObjectKey);
		}
		return chatSession;
	}
   
    private String getConstructedReplyURL(String inputText,int chatId,int messageId){
    	
    	StringBuffer reply=new StringBuffer(MessagingConstants.REPLY_URL);
    	reply.append(MessagingConstants.CHAT_ID);
    	reply.append(MessagingConstants.EQUALS);
    	reply.append(chatId);
    	reply.append(MessagingConstants.AMBERSON);
    	reply.append(MessagingConstants.REPLY_TEXT);
    	reply.append(MessagingConstants.EQUALS);
    	JsonObjectBuilder keyBoardBuilder=Json.createObjectBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderOuter=Json.createArrayBuilder();
    	
    	
    	//MemcachedClient client=ChatUtil.getMemCacheClient();
    	IMemcacheWrapper client=ChatUtil.getMemCacheClient();
    	JsonArrayBuilder arrayBuilder=null;
    	JsonObjectBuilder jsonObjectBuilder=null;
    	List<FoodItem> masterItemList=(List<FoodItem>)client.get(MessagingConstants.MASTER_ITEM_LIST);
    	for(FoodItem item:masterItemList){
    		if(item.getIsAvailable().booleanValue()){
    			arrayBuilder=Json.createArrayBuilder();
    			jsonObjectBuilder=Json.createObjectBuilder();
    			jsonObjectBuilder.add(MessagingConstants.TEXT, item.getItemDisplayName());
    			jsonObjectBuilder.add(MessagingConstants.CALLBACK_DATA, item.getItemCallBackText());
    			arrayBuilder.add(jsonObjectBuilder);
    			keyBoardArrayBuilderOuter.add(arrayBuilder);
    		}
    	}
    	
    	
    	JsonArrayBuilder keyBoardArrayBuilderLastLine=Json.createArrayBuilder();
    	/*JsonArrayBuilder keyBoardArrayBuilderLine1=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine2=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine3=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine4=Json.createArrayBuilder();
    	
    	JsonObjectBuilder samosaObjectBuilder=Json.createObjectBuilder();
    	samosaObjectBuilder.add(MessagingConstants.TEXT, "Samosa@Rs75");
    //	samosaObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	samosaObjectBuilder.add(MessagingConstants.CALLBACK_DATA, "Samosa");
    	
    	
    	JsonObjectBuilder sandwichObjectBuilder=Json.createObjectBuilder();
    	sandwichObjectBuilder.add(MessagingConstants.TEXT, "Veg Sandwich@Rs100");
   // 	sandwichObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	sandwichObjectBuilder.add(MessagingConstants.CALLBACK_DATA, "Sandwich");
    	
    	JsonObjectBuilder combo1ObjectBuilder=Json.createObjectBuilder();
    	combo1ObjectBuilder.add(MessagingConstants.TEXT, "Coke+Small Popcron@Rs150");
  //  	combo1ObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	combo1ObjectBuilder.add(MessagingConstants.CALLBACK_DATA, "Combo1");
    	
    	JsonObjectBuilder combo2ObjectBuilder=Json.createObjectBuilder();
    	combo2ObjectBuilder.add(MessagingConstants.TEXT, "Coke+Large Popcron@Rs200");
  //  	combo2ObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	combo2ObjectBuilder.add(MessagingConstants.CALLBACK_DATA, "Combo2");*/
    	
    	JsonObjectBuilder confirmEndOfSelection=Json.createObjectBuilder();
    	confirmEndOfSelection.add(MessagingConstants.TEXT, "Done with Selection.Enter QUantities");
  //  	confirmEndOfSelection.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	confirmEndOfSelection.add(MessagingConstants.CALLBACK_DATA, MessagingConstants.FINAL_OPTION);
    	keyBoardArrayBuilderLastLine.add(confirmEndOfSelection);
    	
    	/*keyBoardArrayBuilderLine1.add(samosaObjectBuilder);
    	keyBoardArrayBuilderLine2.add(sandwichObjectBuilder);
    	keyBoardArrayBuilderLine3.add(combo1ObjectBuilder);
    	keyBoardArrayBuilderLine4.add(combo2ObjectBuilder);
    	keyBoardArrayBuilderLastLine.add(confirmEndOfSelection);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine1);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine2);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine3);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine4);*/
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLastLine);
    	keyBoardBuilder.add("inline_keyboard", keyBoardArrayBuilderOuter);
    	String options=keyBoardBuilder.build().toString();
    	
    	
    	reply.append("Click on items you wish to purchase&reply_to_message_id="+messageId+"&reply_markup="+options);
    	reply.append(MessagingConstants.AMBERSON);
    	reply.append(MessagingConstants.REPLY_TO_MESSAGE);
    	reply.append(MessagingConstants.EQUALS);
    	reply.append(messageId);
    	String replyUrl=reply.toString();
    	//System.out.println("replyUrl:"+replyUrl);
    	return replyUrl;
    }
    
 private String getMoreOptionConfirmation(int chatId,int messageId){
    	
    	StringBuffer reply=new StringBuffer(MessagingConstants.REPLY_URL);
    	reply.append(MessagingConstants.CHAT_ID);
    	reply.append(MessagingConstants.EQUALS);
    	reply.append(chatId);
    	reply.append(MessagingConstants.AMBERSON);
    	reply.append(MessagingConstants.REPLY_TEXT);
    	reply.append(MessagingConstants.EQUALS);
    	JsonObjectBuilder keyBoardBuilder=Json.createObjectBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderOuter=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine1=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine2=Json.createArrayBuilder();
    	
    	
    	JsonObjectBuilder yesObjectBuilder=Json.createObjectBuilder();
    	yesObjectBuilder.add(MessagingConstants.TEXT, MessagingConstants.YES);
    //	samosaObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	yesObjectBuilder.add(MessagingConstants.CALLBACK_DATA, messageId+":"+MessagingConstants.GOTO_ENTERQUANTTIES);
    	
    	
    	JsonObjectBuilder noObjectBuilder=Json.createObjectBuilder();
    	noObjectBuilder.add(MessagingConstants.TEXT, MessagingConstants.NO);
   // 	sandwichObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	noObjectBuilder.add(MessagingConstants.CALLBACK_DATA, messageId+":"+MessagingConstants.GOTO_OPTIONS_AGAIN);
    	
    
    	
    	keyBoardArrayBuilderLine1.add(yesObjectBuilder);
    	keyBoardArrayBuilderLine2.add(noObjectBuilder);

    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine1);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine2);

    	keyBoardBuilder.add("inline_keyboard", keyBoardArrayBuilderOuter);
    	String options=keyBoardBuilder.build().toString();
    	
    	
    	reply.append("Ready to choose Quantities for Food&reply_markup="+options);
    /*	reply.append(AMBERSON);
    	reply.append(REPLY_TO_MESSAGE);
    	reply.append(EQUALS);
    	reply.append(messageId);*/
    	String replyUrl=reply.toString();
    	//System.out.println("replyUrl:"+replyUrl);
    	return replyUrl;
    }
 
 private String getQuantitiesForItem(int chatId,int messageId,String item){
 	
 	StringBuffer reply=new StringBuffer(MessagingConstants.REPLY_URL);
 	reply.append(MessagingConstants.CHAT_ID);
 	reply.append(MessagingConstants.EQUALS);
 	reply.append(chatId);
 	reply.append(MessagingConstants.AMBERSON);
 	reply.append(MessagingConstants.REPLY_TEXT);
 	reply.append(MessagingConstants.EQUALS);
 	JsonObjectBuilder keyBoardBuilder=Json.createObjectBuilder();
 	JsonArrayBuilder keyBoardArrayBuilderOuter=Json.createArrayBuilder();
 	
 	//TODO take both of this from constant
 	int maxQuantityToOrder=9;
 	int maxItemPerRow=3;
 	int totalNumberOfRows=maxQuantityToOrder/maxItemPerRow;
 	JsonArrayBuilder keyBoardArrayBuilderLine=null;
 	int index=1;
 	JsonObjectBuilder entryJsonObject=null;
 	while(index<=maxQuantityToOrder){
 		
 		for(int rowindex=1;rowindex<=totalNumberOfRows;rowindex++){
 			keyBoardArrayBuilderLine=Json.createArrayBuilder(); 
 			
 			for(int colIndex=1;colIndex<=maxItemPerRow;colIndex++){
 				if(! (index > maxQuantityToOrder)){
 					entryJsonObject=Json.createObjectBuilder();
 	 				entryJsonObject.add(MessagingConstants.TEXT, String.valueOf(index));
 	 				entryJsonObject.add(MessagingConstants.CALLBACK_DATA, messageId+":"+item+":qty:"+index);
 	 				keyBoardArrayBuilderLine.add(entryJsonObject);
 	 				index++;
 				}
 				
 			}
 			keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine);
 		}
 		
 	}
 	

  	keyBoardBuilder.add("inline_keyboard", keyBoardArrayBuilderOuter);
 	String options=keyBoardBuilder.build().toString(); 	
 	
 	reply.append("Press number of quantities for "+item+"&reply_markup="+options);

 	String replyUrl=reply.toString();
 	
 	return replyUrl;
 }
 
 private String getCartOverViewMessage(int chatId,int messageId,JsonObjectBuilder jsonObject){
 	
 	StringBuffer reply=new StringBuffer(MessagingConstants.REPLY_URL);
 	reply.append(MessagingConstants.CHAT_ID);
 	reply.append(MessagingConstants.EQUALS);
 	reply.append(chatId);
 	reply.append(MessagingConstants.AMBERSON);
 	reply.append(MessagingConstants.REPLY_TEXT);
 	reply.append(MessagingConstants.EQUALS);
 	String options=jsonObject.build().toString();
 	
 	
 	reply.append("Preview Your Order&reply_markup="+options);
 /*	reply.append(AMBERSON);
 	reply.append(REPLY_TO_MESSAGE);
 	reply.append(EQUALS);
 	reply.append(messageId);*/
 	String replyUrl=reply.toString();
 	//System.out.println("replyUrl:"+replyUrl);
 	return replyUrl;
 }
 
 private String getCartOverViewMessage(int chatId,int messageId,String jsonObject){
	 	
	 	StringBuffer reply=new StringBuffer(MessagingConstants.REPLY_URL);
	 	reply.append(MessagingConstants.CHAT_ID);
	 	reply.append(MessagingConstants.EQUALS);
	 	reply.append(chatId);
	 	reply.append(MessagingConstants.AMBERSON);
	 	reply.append(MessagingConstants.REPLY_TEXT);
	 	reply.append(MessagingConstants.EQUALS);
	 	
	 	 	
	 	reply.append("Preview Your Order"+jsonObject);
	 	JsonObjectBuilder payButton=Json.createObjectBuilder();
	 	double orderNumber=Math.random()*10000;
	 	double finalOrderNum=Math.abs(orderNumber);
	 	//payButton.add(MessagingConstants.TEXT,"orderNumber:"+orderNumber+"Pay Now");
	 	payButton.add(MessagingConstants.TEXT,"Pay Now");
	 	payButton.add(MessagingConstants.CALLBACK_DATA, "MakePayment:OrderNumber:"+finalOrderNum);
	 	payButton.add(MessagingConstants.CALLBACK_PROCESS_URL, "www.google.com");
	 	reply.append("&reply_markup=");
	 	
	 	
	 	JsonArrayBuilder outerArrayBuilder=Json.createArrayBuilder();
	 	JsonArrayBuilder innerArrayBuilder=Json.createArrayBuilder();
	 	innerArrayBuilder.add(payButton);
	 	outerArrayBuilder.add(innerArrayBuilder);
	 	JsonObjectBuilder keyBoardBuilder=Json.createObjectBuilder();
	 	keyBoardBuilder.add("inline_keyboard", outerArrayBuilder);
	 	reply.append(keyBoardBuilder.build().toString());
	 	String replyUrl=reply.toString();
	 	//System.out.println("replyUrl:"+replyUrl);
	 	return replyUrl;
	 }
     
}
