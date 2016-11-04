package com.poc.bootquartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.poc.bootquartz.Application;
import com.poc.bootquartz.examples.example1.SimpleExample;
import com.poc.bootquartz.examples.example10.Job1;
import com.poc.bootquartz.examples.example10.Job2;
import com.poc.bootquartz.examples.example10.Job3;
import com.poc.bootquartz.examples.example10.QuartzSchedulerConfigurationExample;
import com.poc.bootquartz.examples.example2.SimpleTriggerExample;
import com.poc.bootquartz.examples.example3.CronTriggerExample;
import com.poc.bootquartz.examples.example4.JobStateExample;
import com.poc.bootquartz.examples.example5.MisfireExample;
import com.poc.bootquartz.examples.example6.JobExceptionExample;
import com.poc.bootquartz.examples.example8.CalendarExample;
import com.poc.bootquartz.examples.example9.ListenerExample;
import com.poc.bootquartz.job.SampleJob;

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
        
        JobDetail jobDetailq = JobBuilder.newJob(SampleJob.class).storeDurably().withIdentity("testcronJob").withDescription("This is cron to purpose only with cluster mode...")
                .storeDurably(true)
                .build();
        
        CronTrigger cronTrigger = newTrigger().withIdentity("testCronTrigger", "testgroup").withSchedule(cronSchedule("0/20 * * * * ?"))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.scheduleJob(jobDetailq, cronTrigger);
        scheduler.start();

        Thread.sleep(50000);
    }
    
    @Test
    public void test1() throws Exception {
    	//scheduler.shutdown();
    	SimpleExample.run(scheduler);
        //Thread.sleep(5000);
    }
    
    @Test
    public void test2() throws Exception {
    	SimpleTriggerExample.run(scheduler);
        //Thread.sleep(5000);
    }
    
    @Test
    public void test3() throws Exception {
    	CronTriggerExample.run(scheduler);
    	scheduler.start();
        Thread.sleep(500000);
    }
    
    @Test
    public void testall() throws Exception {
    	SimpleExample.run(scheduler);
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
    
    @Test
    public void multiJobTriggerWithClusterTest() throws Exception{
    	QuartzSchedulerConfigurationExample quartzSchedulerExample = new QuartzSchedulerConfigurationExample();
			quartzSchedulerExample.fireJob(scheduler, Job1.class);
			quartzSchedulerExample.fireJob(scheduler, Job2.class);
			quartzSchedulerExample.fireJob(scheduler, Job3.class);
			scheduler.start();
			//scheduler.shutdown();
    }
}