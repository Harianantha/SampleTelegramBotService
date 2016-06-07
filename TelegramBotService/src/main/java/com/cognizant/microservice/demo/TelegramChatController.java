package com.cognizant.microservice.demo;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
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



@RestController
@RequestMapping("/chat")
public class TelegramChatController {

	private final static Logger LOGGER = Logger.getLogger(TelegramChatController.class.getName()); 
	private static String REPLY_URL="https://api.telegram.org/bot162646339:AAFLvRJOZNbnYsw_7fiECo-UEXZ-JWoGhNE/sendMessage?";
	private static String CHAT_ID="chat_id";
	private static String REPLY_TEXT="text";
	private static String REPLY_TO_MESSAGE="reply_to_message_id";
	private static String EQUALS="=";
	private static String AMBERSON="&";
	private static String ACKNOWLEDGEMENT_MESSAGE="Thanks. Your vehicle will be ready for you";
    private final static String CLASSNAME="HotelValetController";

    
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
    	
    /*
    	JsonArray result=structure.getJsonArray("result");
    	if(result!=null){
    		JsonObject parentMessageObject=result.getJsonObject(0);
    		//System.out.println("result object:"+result.toString());
    		if(parentMessageObject!=null){
    			//System.out.println("parentMessageObject object:"+parentMessageObject.toString());
    			
    			JsonObject messageObject=parentMessageObject.getJsonObject("message");
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
        		
    	}else{
    		System.out.println("Result array is null");
    	}
    	
    	*/

    	
    		//System.out.println("result object:"+result.toString());
    		if(structure!=null ){
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
    	
    	
    	URL url=null;
		try {
			url = new URL(replyMessage);
			long startTime=System.currentTimeMillis();
			/*HttpHost proxy = new HttpHost("proxy.cognizant.com");
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.cognizant.com", 6050));*/
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			/*conn.setRequestProperty("Accept", "application/json");
			conn.addRequestProperty("http.proxyHost", "proxy.cognizant.com");
			conn.addRequestProperty("http.proxyPort", "6050");*/
			//conn.addRequestProperty("java.net.useSystemProxies", "true");
			
			conn.connect();
			//conn.getC
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    	
       
    }
   
    private String getConstructedReplyURL(String inputText,int chatId,int messageId){
    	
    	StringBuffer reply=new StringBuffer(REPLY_URL);
    	reply.append(CHAT_ID);
    	reply.append(EQUALS);
    	reply.append(chatId);
    	reply.append(AMBERSON);
    	reply.append(REPLY_TEXT);
    	reply.append(EQUALS);
    	JsonObjectBuilder keyBoardBuilder=Json.createObjectBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderOuter=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine1=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine2=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine3=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLine4=Json.createArrayBuilder();
    	JsonArrayBuilder keyBoardArrayBuilderLastLine=Json.createArrayBuilder();
    	JsonObjectBuilder samosaObjectBuilder=Json.createObjectBuilder();
    	samosaObjectBuilder.add("text", "Samosa@Rs75");
    	samosaObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	samosaObjectBuilder.add("callback_data", "Samosa");
    	
    	
    	JsonObjectBuilder sandwichObjectBuilder=Json.createObjectBuilder();
    	sandwichObjectBuilder.add("text", "Veg Sandwich@Rs100");
    	sandwichObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	sandwichObjectBuilder.add("callback_data", "Sandwich");
    	
    	JsonObjectBuilder combo1ObjectBuilder=Json.createObjectBuilder();
    	combo1ObjectBuilder.add("text", "Coke+Small Popcron@Rs150");
    	combo1ObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	combo1ObjectBuilder.add("callback_data", "Combo1");
    	
    	JsonObjectBuilder combo2ObjectBuilder=Json.createObjectBuilder();
    	combo2ObjectBuilder.add("text", "Coke+Large Popcron@Rs200");
    	combo2ObjectBuilder.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	combo2ObjectBuilder.add("callback_data", "Combo2");
    	
    	JsonObjectBuilder confirmEndOfSelection=Json.createObjectBuilder();
    	confirmEndOfSelection.add("text", "Done with Selection.Enter QUantities");
    	confirmEndOfSelection.add("url", "https://telegrambotservice.cfapps.io/chat/processcallback");
    	confirmEndOfSelection.add("callback_data", "Done with Selection.Enter QUantities");
    	
    	
    	keyBoardArrayBuilderLine1.add(samosaObjectBuilder);
    	keyBoardArrayBuilderLine2.add(sandwichObjectBuilder);
    	keyBoardArrayBuilderLine3.add(combo1ObjectBuilder);
    	keyBoardArrayBuilderLine4.add(combo2ObjectBuilder);
    	keyBoardArrayBuilderLastLine.add(confirmEndOfSelection);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine1);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine2);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine3);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLine4);
    	keyBoardArrayBuilderOuter.add(keyBoardArrayBuilderLastLine);
    	keyBoardBuilder.add("inline_keyboard", keyBoardArrayBuilderOuter);
    	String options=keyBoardBuilder.build().toString();
    	
    	
    	reply.append("Choose from Below&reply_to_message_id="+messageId+"&reply_markup="+options);
    	reply.append(AMBERSON);
    	reply.append(REPLY_TO_MESSAGE);
    	reply.append(EQUALS);
    	reply.append(messageId);
    	String replyUrl=reply.toString();
    	//System.out.println("replyUrl:"+replyUrl);
    	return replyUrl;
    }
   
    @RequestMapping(value="/processcallback",method=RequestMethod.POST)
    public void processCallBack(@RequestBody String payload) {
    	String methodName="processCallBack";
    	LOGGER.entering(CLASSNAME, methodName);
    	LOGGER.log(Level.FINE, "PayLoad is:"+payload);
    }
    
}
