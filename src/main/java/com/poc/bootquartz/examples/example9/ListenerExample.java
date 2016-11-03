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
 
package com.poc.bootquartz.examples.example9;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates the behavior of <code>JobListener</code>s. In particular, this example will use a job listener to
 * trigger another job after one job succesfully executes.
 */
public class ListenerExample {

  public static void run(Scheduler sched) throws Exception {
    Logger log = LoggerFactory.getLogger(ListenerExample.class);

    JobDetail job = newJob(SimpleJob1.class).withIdentity("job1111").build();

    Trigger trigger = newTrigger().withIdentity("trigger111111").startNow().build();

    // Set up the listener
    JobListener listener = new Job1Listener();
    Matcher<JobKey> matcher = KeyMatcher.keyEquals(job.getKey());
    sched.getListenerManager().addJobListener(listener, matcher);

    // schedule the job to run
    sched.scheduleJob(job, trigger);

    SchedulerMetaData metaData = sched.getMetaData();
    log.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");

  }

 
}
