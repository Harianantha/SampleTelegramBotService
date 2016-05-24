package com.cognizant.microservice.demo;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/browse")
public class CatalogBrowseController {

	private final static Logger LOGGER = Logger.getLogger(CatalogBrowseController.class.getName()); 
    private final AtomicLong counter = new AtomicLong();
    private final static String CLASSNAME="CategoriesController";

    
    @RequestMapping(value="/process",method=RequestMethod.POST)
    public void process(@RequestBody String payload) {
    	String methodName="process";
    	
    	LOGGER.entering(CLASSNAME, methodName);
    	System.out.println("Request body is:"+payload);
        
       // return null;
        
        
        
    }
   
    
}
