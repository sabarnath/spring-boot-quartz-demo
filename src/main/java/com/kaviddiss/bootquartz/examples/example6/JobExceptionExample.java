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
 
package com.kaviddiss.bootquartz.examples.example6;

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
 * This job demonstrates how Quartz can handle JobExecutionExceptions that are thrown by jobs.
 * 
 * @author Bill Kratzer
 */
public class JobExceptionExample {

  public static void run(Scheduler sched) throws Exception {
    Logger log = LoggerFactory.getLogger(JobExceptionExample.class);

    // get a "nice round" time a few seconds in the future...
    Date startTime = nextGivenSecondDate(null, 15);

    // badJob1 will run every 10 seconds
    // this job will throw an exception and refire
    // immediately
    JobDetail job = newJob(BadJob1.class).withIdentity("badJob1", "group4").usingJobData("denominator", "0").build();

    SimpleTrigger trigger = newTrigger().withIdentity("trigger1", "group4").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();

    Date ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // badJob2 will run every five seconds
    // this job will throw an exception and never
    // refire
    job = newJob(BadJob2.class).withIdentity("badJob2", "group4").build();

    trigger = newTrigger().withIdentity("trigger2", "group4").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(5).repeatForever()).build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    SchedulerMetaData metaData = sched.getMetaData();
    log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
  }



}
