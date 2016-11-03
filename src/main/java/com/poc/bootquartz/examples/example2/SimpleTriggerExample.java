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
 
package com.poc.bootquartz.examples.example2;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Example will demonstrate all of the basics of scheduling capabilities of Quartz using Simple Triggers.
 * 
 * @author 
 */
public class SimpleTriggerExample {

  public static void run(Scheduler sched) throws Exception {
    Logger log = LoggerFactory.getLogger(SimpleTriggerExample.class);

    // get a "nice round" time a few seconds in the future...
    Date startTime = DateBuilder.nextGivenSecondDate(null, 15);

    // job1 will only fire once at date/time "ts"
    JobDetail job = newJob(SimpleJob.class).withIdentity("job1", "group2").build();

    SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group2").startAt(startTime).build();

    // schedule it to run!
    Date ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // job2 will only fire once at date/time "ts"
    job = newJob(SimpleJob.class).withIdentity("job2", "group2").build();

    trigger = (SimpleTrigger) newTrigger().withIdentity("trigger2", "group2").startAt(startTime).build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // job3 will run 11 times (run once and repeat 10 more times)
    // job3 will repeat every 10 seconds
    job = newJob(SimpleJob.class).withIdentity("job3", "group2").build();

    trigger = newTrigger().withIdentity("trigger3", "group2").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10)).build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // the same job (job3) will be scheduled by a another trigger
    // this time will only repeat twice at a 70 second interval

    trigger = newTrigger().withIdentity("trigger33", "group2").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(2)).forJob(job).build();

    ft = sched.scheduleJob(trigger);
    log.info(job.getKey() + " will [also] run at: " + ft + " and repeat: " + trigger.getRepeatCount()
             + " times, every " + trigger.getRepeatInterval() / 1000 + " seconds");

    // job4 will run 6 times (run once and repeat 5 more times)
    // job4 will repeat every 10 seconds
    job = newJob(SimpleJob.class).withIdentity("job4", "group2").build();

    trigger = newTrigger().withIdentity("trigger4", "group2").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(5)).build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // job5 will run once, five minutes in the future
    job = newJob(SimpleJob.class).withIdentity("job5", "group2").build();

    trigger = (SimpleTrigger) newTrigger().withIdentity("trigger5", "group2")
        .startAt(futureDate(5, IntervalUnit.MINUTE)).build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // job6 will run indefinitely, every 40 seconds
    job = newJob(SimpleJob.class).withIdentity("job6", "group2").build();

    trigger = newTrigger().withIdentity("trigger6", "group2").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(40).repeatForever()).build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    log.info("------- Starting Scheduler ----------------");

    // All of the jobs have been added to the scheduler, but none of the jobs
    // will run until the scheduler has been started
    //sched.start();

    log.info("------- Started Scheduler -----------------");

    // jobs can also be scheduled after start() has been called...
    // job7 will repeat 20 times, repeat every five minutes
    job = newJob(SimpleJob.class).withIdentity("job7", "group2").build();

    trigger = newTrigger().withIdentity("trigger7", "group2").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInMinutes(5).withRepeatCount(20)).build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // jobs can be fired directly... (rather than waiting for a trigger)
    job = newJob(SimpleJob.class).withIdentity("job8", "group2").storeDurably().build();

    sched.addJob(job, true);

    log.info("'Manually' triggering job8...");
    sched.triggerJob(jobKey("job8", "group2"));

    // jobs can be re-scheduled...
    // job 7 will run immediately and repeat 10 times for every second
    log.info("------- Rescheduling... --------------------");
    trigger = newTrigger().withIdentity("trigger7", "group2").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInMinutes(5).withRepeatCount(20)).build();

    ft = sched.rescheduleJob(trigger.getKey(), trigger);
    log.info("job7 rescheduled to run at: " + ft);

    // display some stats about the schedule that just ran
    SchedulerMetaData metaData = sched.getMetaData();
    log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");

  }
}
