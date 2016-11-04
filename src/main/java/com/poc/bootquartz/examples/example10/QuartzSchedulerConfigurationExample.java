package com.poc.bootquartz.examples.example10;

import java.io.Serializable;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;


@Service
public class QuartzSchedulerConfigurationExample implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7662016530887783798L;
	private int repeatCount = 1;
	public static final String jobdatamapKey = "latch";
	public static final String jobdetailmapKey = "example";

	public <T extends Job> void fireJob(Scheduler scheduler, Class<T> jobClass)
			throws SchedulerException, InterruptedException {

		// define the job and tie it to our HelloJob class
		JobBuilder jobBuilder = JobBuilder.newJob(jobClass);
		JobDataMap data = new JobDataMap();
		data.put(jobdatamapKey, this);

		JobDetail jobDetail = jobBuilder
				.usingJobData(jobdetailmapKey,
						"com.poc.bootquartz.examples.example10.QuartzSchedulerConfigurationExample")
				/*.usingJobData(data)*/.storeDurably(true).build();

		// Trigger the job to run now, and then every 40 seconds
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.startNow()
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule()
								.withRepeatCount(repeatCount)
								.withIntervalInSeconds(2))
				.withDescription("MyTrigger").build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(jobDetail, trigger);
	}

}
