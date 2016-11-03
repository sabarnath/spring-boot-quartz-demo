/* 
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */
 
package com.kaviddiss.bootquartz.examples.example4;

import static org.quartz.DateBuilder.nextGivenSecondDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Example will demonstrate how job parameters can be passed into jobs and how state can be maintained
 * 
 * @author Bill Kratzer
 */
public class JobStateExample {

  public static void run(Scheduler sched) throws Exception {
    Logger log = LoggerFactory.getLogger(JobStateExample.class);

    // get a "nice round" time a few seconds in the future....
    Date startTime = nextGivenSecondDate(null, 10);

    // job1 will only run 5 times (at start time, plus 4 repeats), every 10 seconds
    JobDetail job1 = newJob(ColorJob.class).withIdentity("job1", "group9").build();

    SimpleTrigger trigger1 = newTrigger().withIdentity("trigger1", "group9").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(4)).build();

    // pass initialization parameters into the job
    job1.getJobDataMap().put(ColorJob.FAVORITE_COLOR, "Green");
    job1.getJobDataMap().put(ColorJob.EXECUTION_COUNT, 1);

    // schedule the job to run
    Date scheduleTime1 = sched.scheduleJob(job1, trigger1);
    log.info(job1.getKey() + " will run at: " + scheduleTime1 + " and repeat: " + trigger1.getRepeatCount()
             + " times, every " + trigger1.getRepeatInterval() / 1000 + " seconds");

    // job2 will also run 5 times, every 10 seconds
    JobDetail job2 = newJob(ColorJob.class).withIdentity("job2", "group9").build();

    SimpleTrigger trigger2 = newTrigger().withIdentity("trigger2", "group9").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(4)).build();

    // pass initialization parameters into the job
    // this job has a different favorite color!
    job2.getJobDataMap().put(ColorJob.FAVORITE_COLOR, "Red");
    job2.getJobDataMap().put(ColorJob.EXECUTION_COUNT, 1);

    // schedule the job to run
    Date scheduleTime2 = sched.scheduleJob(job2, trigger2);
    log.info(job2.getKey().toString() + " will run at: " + scheduleTime2 + " and repeat: " + trigger2.getRepeatCount()
             + " times, every " + trigger2.getRepeatInterval() / 1000 + " seconds");

    SchedulerMetaData metaData = sched.getMetaData();
    log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");

  }

 
}
