package com.poc.bootquartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SchedulerConfig.class})
public class Application {

	private static Logger _log = LoggerFactory.getLogger(Application.class);
	
    public static void main(String[] args) {
    	
    	_log.debug("Application server getting start from here....");
        SpringApplication.run(Application.class, args);
        _log.debug("Application server started....");
        
    }
}
