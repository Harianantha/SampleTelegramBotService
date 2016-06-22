package com.cognizant.microservice.memcache;

public interface IMemcacheWrapper {
	
	public boolean add( String key, Object value );
	public boolean set( String key, Object value );
	public Object get( String key );
	public boolean keyExists( String key );
	public boolean replace( String key, Object value );

}
