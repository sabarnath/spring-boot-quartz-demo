package com.poc.bootquartz.service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.poc.bootquartz.examples.example1.RestControllerJob;

@RestController
public class HelloWorldController {
	
    private static Logger _log = LoggerFactory.getLogger(HelloWorldController.class);
    
    private static final String _groupName = "REST_CONTROLLER_GROUP_OF_JOBS";

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@RequestMapping("/hello-world")
	public @ResponseBody String sayHello(){
		return "Hello All!!!!";
	}
	
	@RequestMapping(value="/createJob" ,method=RequestMethod.GET)
    public String createJob(@RequestParam(value="jobName", required=false, defaultValue="myFirstJob") String jobName,
    		@RequestParam(value="triggerName", required=false, defaultValue="myFirstTrigger") String triggerName) {
    	Scheduler scheduler = schedulerFactoryBean.getScheduler();

    	_log.info("createJob ... job name {}",jobName);
    	try {
    		// define the job and tie it to our HelloJob class
            JobDetail job = newJob(RestControllerJob.class).storeDurably().withIdentity(jobName, _groupName).build();
            
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("JobName", jobName);
            jobDataMap.put("groupName", _groupName);
            jobDataMap.put("triggerName", triggerName);
            jobDataMap.put("isDurable", true);

            // Trigger the job to run on the next round minute
            Trigger trigger = newTrigger().withIdentity(triggerName, _groupName).startNow().usingJobData(jobDataMap).build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            
            if(scheduler.checkExists(JobKey.jobKey(jobName,_groupName))){
        		_log.info("This job has triggered using rest-controller ....Job name : {}.. Trigger name {} & groupname {}",jobName,triggerName,_groupName);
        	}
            
		}catch(SchedulerException sce){
			_log.error("Error while create the new job ...",sce);
			return sce.getMessage();
		}catch (Exception e) {
			_log.error("Error while create the new job ...",e);
		}
    	
        return "Job successfully added and triggered ...JobName :"+jobName+"....GroupName :"+_groupName;
    }
	
	@RequestMapping(value="/updateJobTrigger" ,method=RequestMethod.GET)
    public String updateJobTrigger(@RequestParam(value="jobName", required=false, defaultValue="myFirstJob") String jobName,
    		@RequestParam(value="triggerName", required=false, defaultValue="myFirstTrigger") String triggerName) {
    	Scheduler scheduler = schedulerFactoryBean.getScheduler();

    	_log.info("updateJob ... job name {}",jobName);
    	try {
    		 if(!scheduler.checkExists(JobKey.jobKey(jobName,_groupName))){
         		_log.error("JobName not exists....in our group.. pls re-check the name... jobname {}, GroupName{}",jobName, _groupName);
         		return "JobName not exists, Please re-check the name : "+jobName+ " : group name : "+_groupName;
         	}
    		 if(!scheduler.checkExists(TriggerKey.triggerKey(triggerName, _groupName))){
          		_log.error("Trigger not exists....in our group.. pls re-check the name... triggername {}, GroupName{}",triggerName, _groupName);
          		return "Trigger not exists, Please re-check the name : "+triggerName+ " : group name : "+_groupName;
          	}
    		// retrieve the trigger
    		 Trigger oldTrigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, _groupName));

    		 // obtain a builder that would produce the trigger
    		 TriggerBuilder<Trigger> tb = (TriggerBuilder<Trigger>) oldTrigger.getTriggerBuilder();

    		 // update the schedule associated with the builder, and build the new trigger
    		 // (other builder methods could be called, to change the trigger in any desired way)
    		 Trigger newTrigger = tb.withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10)).build();

    		 scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
            
            
		}catch(SchedulerException sce){
			_log.error("Error while update the new job ...",sce);
			return sce.getMessage();
		}catch (Exception e) {
			_log.error("Error while update the new job ...",e);
			return e.getMessage();
		}
    	
        return "Job successfully updated and rescheduled ...JobName :"+jobName+"....GroupName :"+_groupName;
    }
	
	@RequestMapping(value="/deleteJobTrigger" ,method=RequestMethod.GET)
    public String deleteJobTrigger(@RequestParam(value="jobName", required=false, defaultValue="myFirstJob") String jobName,
    		@RequestParam(value="triggerName", required=false, defaultValue="myFirstTrigger") String triggerName) {
    	Scheduler scheduler = schedulerFactoryBean.getScheduler();

    	_log.info("deleteJobTrigger ... job name {}",jobName);
    	try {
    		 if(!scheduler.checkExists(JobKey.jobKey(jobName,_groupName))){
         		_log.error("JobName not exists....in our group.. pls re-check the name... jobname {}, GroupName{}",jobName, _groupName);
         		return "JobName not exists, Please re-check the name : "+jobName+ " : group name : "+_groupName;
         		}
    		 scheduler.deleteJob(JobKey.jobKey(jobName,_groupName));
            
		}catch(SchedulerException sce){
			_log.error("Error while deleteJobTrigger the new job ...",sce);
			return sce.getMessage();
		}catch (Exception e) {
			_log.error("Error while deleteJobTrigger the new job ...",e);
			return e.getMessage();
		}
    	
        return "Job successfully deleteJobTrigger ...JobName :"+jobName+"....GroupName :"+_groupName;
    }
	
}