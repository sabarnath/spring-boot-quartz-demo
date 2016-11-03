package com.kaviddiss.bootquartz;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

import com.kaviddiss.bootquartz.examples.example1.SimpleExample;

@SpringBootApplication
@Import({SchedulerConfig.class})
public class Application {

	@Autowired
	private Scheduler scheduler;
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
    }
}
