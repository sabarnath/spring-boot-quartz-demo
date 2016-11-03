package com.kaviddiss.bootquartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.kaviddiss.bootquartz.examples.example1.SimpleExample;
import com.kaviddiss.bootquartz.examples.example2.SimpleTriggerExample;
import com.kaviddiss.bootquartz.examples.example3.CronTriggerExample;
import com.kaviddiss.bootquartz.examples.example4.JobStateExample;
import com.kaviddiss.bootquartz.examples.example5.MisfireExample;
import com.kaviddiss.bootquartz.examples.example6.JobExceptionExample;
import com.kaviddiss.bootquartz.examples.example8.CalendarExample;
import com.kaviddiss.bootquartz.examples.example9.ListenerExample;
import com.kaviddiss.bootquartz.job.SampleJob;

@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private Scheduler scheduler;

    @Test
    public void test() throws Exception {

        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
                .storeDurably(true)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .startNow()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

        Thread.sleep(5000);
    }
    
    @Test
    public void test1() throws Exception {
    	SimpleExample.run(scheduler);
        //Thread.sleep(5000);
    }
    
    @Test
    public void test2() throws Exception {
    	SimpleTriggerExample.run(scheduler);
        //Thread.sleep(5000);
    }
    
    @Test
    public void testall() throws Exception {
    	SimpleExample.run(scheduler);
    	scheduler.start();
    	SimpleTriggerExample.run(scheduler);
    	CronTriggerExample.run(scheduler);
    	JobStateExample.run(scheduler);
    	MisfireExample.run(scheduler);
    	JobExceptionExample.run(scheduler);
    	//InterruptExample.run(scheduler);
    	CalendarExample.run(scheduler);
    	ListenerExample.run(scheduler);
    	scheduler.start();
    	Thread.sleep(50000);
    }
    
    @Test
    public void MisfireExample() throws Exception{
    	MisfireExample.run(scheduler);
    }
    
}