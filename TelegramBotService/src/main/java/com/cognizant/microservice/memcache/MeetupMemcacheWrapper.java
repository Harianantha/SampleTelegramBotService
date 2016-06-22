package com.cognizant.microservice.memcache;

import java.io.IOException;

import com.meetup.memcached.MemcachedClient;





public class MeetupMemcacheWrapper implements IMemcacheWrapper {

	@Override
	public boolean replace(String key, Object value) {
		return client.replace(key, value);
	}

	private MemcachedClient client=null;
	@Override
	public boolean add(String key, Object value) {
		
		return client.add(key,  value);
	}

	@Override
	public boolean set(String key, Object value) {
		
		
		return client.set(key, value);
	}

	@Override
	public Object get(String key) {
		
		return client.get(key);
		//return null;
	}

	@Override
	public boolean keyExists(String key) {
		
		return client.keyExists(key);
		
		
	}
	
	public MeetupMemcacheWrapper(String poolName,String userName,String password,String memcacheHost,String memcachePort) throws IOException{
		
		 client=new MemcachedClient("Test1",memcacheHost,memcachePort);
		
	}

}
