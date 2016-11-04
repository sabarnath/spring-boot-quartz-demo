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
 
package com.poc.bootquartz.examples.example5;

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
 * Demonstrates the behavior of <code>StatefulJob</code>s, as well as how misfire instructions affect the firings of
 * triggers of <code>StatefulJob</code> s - when the jobs take longer to execute that the frequency of the trigger's
 * repitition.
 * <p>
 * While the example is running, you should note that there are two triggers with identical schedules, firing identical
 * jobs. The triggers "want" to fire every 3 seconds, but the jobs take 10 seconds to execute. Therefore, by the time
 * the jobs complete their execution, the triggers have already "misfired" (unless the scheduler's "misfire threshold"
 * has been set to more than 7 seconds). You should see that one of the jobs has its misfire instruction set to
 * <code>SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT</code>, which causes it to fire
 * immediately, when the misfire is detected. The other trigger uses the default "smart policy" misfire instruction,
 * which causes the trigger to advance to its next fire time (skipping those that it has missed) - so that it does not
 * refire immediately, but rather at the next scheduled time.
 * </p>
 * 
 * @author 
 */
public class MisfireExample {

  public static void run(Scheduler sched) throws Exception {
    Logger log = LoggerFactory.getLogger(MisfireExample.class);

    // get a "nice round" time a few seconds in the future...
    Date startTime = nextGivenSecondDate(null, 15);

    // statefulJob1 will run every three seconds
    // (but it will delay for ten seconds)
    JobDetail job = newJob(StatefulDumbJob.class).withIdentity("statefulJob1", "group3").usingJobData(StatefulDumbJob.EXECUTION_DELAY, 10000L).build();

    SimpleTrigger trigger = newTrigger().withIdentity("trigger1", "group3").startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(3).repeatForever()).build();

    Date ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    // statefulJob2 will run every three seconds
    // (but it will delay for ten seconds - and therefore purposely misfire after a few iterations)
    job = newJob(StatefulDumbJob.class).withIdentity("statefulJob2", "group3")
        .usingJobData(StatefulDumbJob.EXECUTION_DELAY, 10000L).build();

    trigger = newTrigger()
        .withIdentity("trigger2", "group3")
        .startAt(startTime)
        .withSchedule(simpleSchedule().withIntervalInSeconds(3).repeatForever()
                          .withMisfireHandlingInstructionNowWithExistingCount()) // set misfire instructions
        .build();

    ft = sched.scheduleJob(job, trigger);
    log.info(job.getKey() + " will run at: " + ft + " and repeat: " + trigger.getRepeatCount() + " times, every "
             + trigger.getRepeatInterval() / 1000 + " seconds");

    log.info("------- Starting Scheduler ----------------");

    SchedulerMetaData metaData = sched.getMetaData();
    log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
  }


}
