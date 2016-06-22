package com.cognizant.microservice.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.cognizant.microservice.memcache.IMemcacheWrapper;
import com.cognizant.microservice.memcache.MeetupMemcacheWrapper;
import com.cognizant.microservice.memcache.SpyNetMemcacheWrapper;
import com.meetup.memcached.MemcachedClient;

public class ChatUtil {
	
	/*public static MemcachedClient getMemCacheClient() {
		String memcacheHost=System.getenv(MessagingConstants.MEMCACHE_HOSTNAME);
		String memcachePort=System.getenv(MessagingConstants.MEMCACHE_PORT);
		if(memcacheHost == null || memcacheHost.isEmpty()){
			memcacheHost="127.0.0.1";
			memcachePort="11211";
		}
		//System.out.println("host:"+memcacheHost+":port:"+memcachePort);
		MemcachedClient client=new MemcachedClient("Test1",memcacheHost,memcachePort);
		return client;
	}
	*/
	public static IMemcacheWrapper getMemCacheClient() {
		String memcacheHost=System.getenv(MessagingConstants.MEMCACHE_HOSTNAME);
		String memcachePort=System.getenv(MessagingConstants.MEMCACHE_PORT);
		String memcacheUserName=System.getenv(MessagingConstants.MEMCACHE_USERNAME);
		String memcachePassword=System.getenv(MessagingConstants.MEMCACHE_PASSWORD);
		
		IMemcacheWrapper retValue=null;
		try{
			
		if(memcacheHost == null || memcacheHost.isEmpty()){
			memcacheHost="127.0.0.1";
			memcachePort="11211";
			retValue=new MeetupMemcacheWrapper("Test1",null,null,memcacheHost,memcachePort);
		}else{
			retValue=new SpyNetMemcacheWrapper("Test1",memcacheUserName,memcachePassword,memcacheHost+":"+memcachePort);
		}

		}catch(IOException e){
			System.out.println("EXCEPTION HAPPENED WHILE GETTING CLIENT");
			e.printStackTrace();
		}
		System.out.println("Cache client returned is:"+retValue.getClass().getName());
		return retValue;
	}
	


	public static void sendReplyMessage(String replyMessage) {
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
			long endTime=System.currentTimeMillis();
			System.out.println("Time to send response:"+(endTime-startTime));

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
