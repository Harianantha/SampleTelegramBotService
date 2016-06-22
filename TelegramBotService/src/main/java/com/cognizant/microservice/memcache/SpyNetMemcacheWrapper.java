package com.cognizant.microservice.memcache;

import java.io.IOException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

public class SpyNetMemcacheWrapper implements IMemcacheWrapper {

	private MemcachedClient client=null;
	@Override
	public boolean add(String key, Object value) {
		// TODO Auto-generated method stub
		OperationFuture<Boolean> addResult=client.add(key, 0, value);
		return addResult.isDone();
	}

	@Override
	public boolean set(String key, Object value) {
		
		OperationFuture<Boolean> setResult=client.set(key, 0, value);
		return setResult.isDone();
	}

	@Override
	public Object get(String key) {
		
		return client.get(key);
		//return null;
	}

	@Override
	public boolean replace(String key, Object value) {
		// TODO Auto-generated method stub
		OperationFuture<Boolean> replaceResult=client.replace(key, 0, value);
		return replaceResult.isDone();
	}

	@Override
	public boolean keyExists(String key) {
		// TODO Auto-generated method stub
		Object value=get(key);
		if(value==null){
			return false;
			
		}
		return true;
		
	}
	
	public SpyNetMemcacheWrapper(String poolName,String userName,String password,String serverDetails) throws IOException{
		
		AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" },new PlainCallbackHandler(userName, password));
		client = new MemcachedClient(
                new ConnectionFactoryBuilder()
                    .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                    .setAuthDescriptor(ad).build(),
                AddrUtil.getAddresses(serverDetails));
		
	}

}
