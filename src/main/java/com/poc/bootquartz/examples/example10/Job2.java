package com.poc.bootquartz.examples.example10;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Job2 implements Job {
	
	Logger log = LoggerFactory.getLogger(Job2.class);
	
	private static int count;

	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		log.debug("--------------------------------------------------------------------");
		log.debug("Job2 start: " + jobContext.getFireTime());
		count++;
		log.debug("Job count " + count);		
		log.debug("Job2 next scheduled time: " + jobContext.getNextFireTime());
		log.debug("Job's thread name is: " + Thread.currentThread().getName());
		log.debug("log the jobdatamapKey : "+jobContext.getMergedJobDataMap().getString(QuartzSchedulerConfigurationExample.jobdatamapKey));
		log.debug("log the jobdatamapKey : "+jobContext.getMergedJobDataMap().containsKey(QuartzSchedulerConfigurationExample.jobdetailmapKey));
		log.debug("Job end");
		log.debug("--------------------------------------------------------------------");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
