package com.poc.bootquartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJobListener implements JobListener {

	private static Logger _log = LoggerFactory.getLogger(MyJobListener.class);
	
	public void jobToBeExecuted(JobExecutionContext context) {
		_log.debug("Job to be exected: " + context.getFireInstanceId() + ", job listener: " + getName());
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		_log.debug("Job has been Vetoed ...."+context.getFireInstanceId());
	}

	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		_log.debug("Job was exected: " + context.getFireInstanceId() + ", job listener: " + getName());
	}

	public String getName() {
		return "MyJobListener";
	}

}
