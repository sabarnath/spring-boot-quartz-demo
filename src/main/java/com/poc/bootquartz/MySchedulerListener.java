package com.poc.bootquartz;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySchedulerListener implements SchedulerListener {

	private static Logger _log = LoggerFactory.getLogger(MySchedulerListener.class);

	
	@Override
	public void jobScheduled(Trigger trigger) {
		_log.debug("jobScheduled ..."+trigger.getDescription());
	}

	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		_log.debug("jobUnscheduled ..."+triggerKey.getName());
		
	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		_log.debug("triggerFinalized ..."+trigger.getDescription());		
	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		_log.debug("triggerPaused ..."+triggerKey.getName());
	}

	@Override
	public void triggersPaused(String triggerGroup) {
		_log.debug("triggersPaused ..."+triggerGroup);
		
	}

	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		_log.debug("triggerResumed ..."+triggerKey.getName());
		
	}

	@Override
	public void triggersResumed(String triggerGroup) {
		_log.debug("triggersResumed ..."+triggerGroup);
		
	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		_log.debug("jobAdded ..."+jobDetail.getKey());
		
	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		_log.debug("jobDeleted ..."+jobKey.getName());
		
	}

	@Override
	public void jobPaused(JobKey jobKey) {
		_log.debug("jobPaused ..."+jobKey.getName());
		
	}

	@Override
	public void jobsPaused(String jobGroup) {
		_log.debug("jobsPaused ..."+jobGroup);
		
	}

	@Override
	public void jobResumed(JobKey jobKey) {
		_log.debug("jobResumed job name..."+jobKey.getName());
		
	}

	@Override
	public void jobsResumed(String jobGroup) {
		_log.debug("jobsResumed group name..."+jobGroup);
		
	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		_log.debug("schedulerError ..."+msg,cause);
		
	}

	@Override
	public void schedulerInStandbyMode() {
		_log.debug("schedulerInStandbyMode");
		
	}

	@Override
	public void schedulerStarted() {
		_log.debug("schedulerStarted");		
	}

	@Override
	public void schedulerStarting() {
		_log.debug("schedulerStarting");		
	}

	@Override
	public void schedulerShutdown() {
		_log.debug("schedulerShutdown");		
	}

	@Override
	public void schedulerShuttingdown() {
		_log.debug("schedulerShuttingdown");		
	}

	@Override
	public void schedulingDataCleared() {
		_log.debug("schedulingDataCleared");		
	}

	
}
