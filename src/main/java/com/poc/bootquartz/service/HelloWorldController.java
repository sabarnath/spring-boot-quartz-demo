package com.poc.bootquartz.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.CronTrigger;
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
import org.springframework.util.StringUtils;
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
    public @ResponseBody String sayHello() {
        return "Hello All!!!!";
    }

    @RequestMapping(value = "/createJob", method = RequestMethod.GET)
    public String createJob(@RequestParam(value = "jobName", required = false,
            defaultValue = "myFirstJob") String jobName, @RequestParam(value = "triggerName",
            required = false, defaultValue = "myFirstTrigger") String triggerName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        _log.info("createJob ... job name {}", jobName);
        try {
            JobDataMap jobDataMap = new JobDataMap();
            JobDetail job = prepareJobDetailWithDataMap(jobName, triggerName, jobDataMap);

            // Trigger the job to run on the next round minute
            Trigger trigger =
                    newTrigger()
                            .withIdentity(triggerName, _groupName)
                            .startNow()
                            .usingJobData(jobDataMap)
                            .withSchedule(
                                    simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10))
                            .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);

            if (scheduler.checkExists(JobKey.jobKey(jobName, _groupName))) {
                _log.info(
                        "This job has triggered using rest-controller ....Job name : {}.. Trigger name {} & groupname {}",
                        jobName, triggerName, _groupName);
            }

        } catch (SchedulerException sce) {
            _log.error("Error while create the new job ...", sce);
            return sce.getMessage();
        } catch (Exception e) {
            _log.error("Error while create the new job ...", e);
        }
        return "Job successfully added and triggered ...JobName :" + jobName + "....GroupName :"
                + _groupName;
    }

    @RequestMapping(value = "/createCronJob", method = RequestMethod.GET)
    public String createCronJob(@RequestParam(value = "jobName", required = true) String jobName, 
                                @RequestParam(value = "triggerName",required = true) String triggerName, 
                                @RequestParam(value = "seconds", required = false) String seconds,
                                @RequestParam(value = "minutes", required = false) String minutes,
                                @RequestParam(value = "hours", required = false) String hours) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        _log.info("createJob ... job name {}, seconds {}, minutes {}, hours {}", jobName, seconds, minutes, hours);
        try {
            JobDataMap jobDataMap = new JobDataMap();
            JobDetail job = prepareJobDetailWithDataMap(jobName, triggerName, jobDataMap);
            CronTrigger trigger = null;
            String cronExcr = generateCronExpression(seconds, minutes, hours, "*", "*", "?", "*");
            _log.info("Cron Expression {}", cronExcr);
            if(!StringUtils.isEmpty(cronExcr)){
             // cron trigger the job to run on the every 20 sec.
                trigger = newTrigger().withIdentity(triggerName, _groupName)
                                .withSchedule(cronSchedule(cronExcr)).build();
            }

            Date ft = scheduler.scheduleJob(job, trigger);
            _log.info(job.getKey() + " has been scheduled to run at: " + ft
                    + " and repeat based on expression: " + trigger.getCronExpression());

            if (scheduler.checkExists(JobKey.jobKey(jobName, _groupName))) {
                _log.info(
                        "This job has triggered using rest-controller ....Job name : {}.. Trigger name {} & groupname {}",
                        jobName, triggerName, _groupName);
            }

        } catch (SchedulerException sce) {
            _log.error("Error while create the new job ...", sce);
            return sce.getMessage();
        } catch (Exception e) {
            _log.error("Error while create the new job ...", e);
        }
        return "Job successfully added and triggered ...JobName :" + jobName + "....GroupName :"
                + _groupName;
    }

    private JobDetail prepareJobDetailWithDataMap(String jobName, String triggerName,
            JobDataMap jobDataMap) {
        // define the job and tie it to our HelloJob class
        JobDetail job =
                newJob(RestControllerJob.class).storeDurably().withIdentity(jobName, _groupName)
                        .build();
        jobDataMap.put("JobName", jobName);
        jobDataMap.put("groupName", _groupName);
        jobDataMap.put("triggerName", triggerName);
        jobDataMap.put("isDurable", true);
        return job;
    }

    @RequestMapping(value = "/updateJobTrigger", method = RequestMethod.GET)
    public String updateJobTrigger(@RequestParam(value = "jobName", required = false,
            defaultValue = "myFirstJob") String jobName, @RequestParam(value = "triggerName",
            required = false, defaultValue = "myFirstTrigger") String triggerName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        _log.info("updateJob ... job name {}", jobName);
        try {
            if (!scheduler.checkExists(JobKey.jobKey(jobName, _groupName))) {
                _log.error(
                        "JobName not exists....in our group.. pls re-check the name... jobname {}, GroupName{}",
                        jobName, _groupName);
                return "JobName not exists, Please re-check the name : " + jobName
                        + " : group name : " + _groupName;
            }
            if (!scheduler.checkExists(TriggerKey.triggerKey(triggerName, _groupName))) {
                _log.error(
                        "Trigger not exists....in our group.. pls re-check the name... triggername {}, GroupName{}",
                        triggerName, _groupName);
                return "Trigger not exists, Please re-check the name : " + triggerName
                        + " : group name : " + _groupName;
            }
            // retrieve the trigger
            Trigger oldTrigger =
                    scheduler.getTrigger(TriggerKey.triggerKey(triggerName, _groupName));

            // obtain a builder that would produce the trigger
            TriggerBuilder<Trigger> tb = (TriggerBuilder<Trigger>) oldTrigger.getTriggerBuilder();

            // update the schedule associated with the builder, and build the
            // new trigger
            // (other builder methods could be called, to change the trigger in
            // any desired way)
            Trigger newTrigger =
                    tb.withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10))
                            .build();

            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);

        } catch (SchedulerException sce) {
            _log.error("Error while update the new job ...", sce);
            return sce.getMessage();
        } catch (Exception e) {
            _log.error("Error while update the new job ...", e);
            return e.getMessage();
        }

        return "Job successfully updated and rescheduled ...JobName :" + jobName
                + "....GroupName :" + _groupName;
    }

    
    @RequestMapping(value = "/updateJobTriggerWithCronExp", method = RequestMethod.GET)
    public String updateJobTriggerWithCronExp(@RequestParam(value = "jobName", required = true) String jobName, 
            @RequestParam(value = "triggerName",required = true) String triggerName, 
            @RequestParam(value = "seconds", required = false) String seconds,
            @RequestParam(value = "minutes", required = false) String minutes,
            @RequestParam(value = "hours", required = false) String hours) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        _log.info("updateJob ... job name {}", jobName);
        try {
            if (!scheduler.checkExists(JobKey.jobKey(jobName, _groupName))) {
                _log.error(
                        "JobName not exists....in our group.. pls re-check the name... jobname {}, GroupName{}",
                        jobName, _groupName);
                return "JobName not exists, Please re-check the name : " + jobName
                        + " : group name : " + _groupName;
            }
            if (!scheduler.checkExists(TriggerKey.triggerKey(triggerName, _groupName))) {
                _log.error(
                        "Trigger not exists....in our group.. pls re-check the name... triggername {}, GroupName{}",
                        triggerName, _groupName);
                return "Trigger not exists, Please re-check the name : " + triggerName
                        + " : group name : " + _groupName;
            }
            // retrieve the trigger
            Trigger oldTrigger =
                    scheduler.getTrigger(TriggerKey.triggerKey(triggerName, _groupName));

            // obtain a builder that would produce the trigger
            TriggerBuilder<Trigger> tb = (TriggerBuilder<Trigger>) oldTrigger.getTriggerBuilder();

            // update the schedule associated with the builder, and build the
            // new trigger
            // (other builder methods could be called, to change the trigger in
            // any desired way)
            String cronExcr = generateCronExpression(seconds, minutes, hours, "*", "*", "?", "*");
            _log.info("Cron Expression {}", cronExcr);
            Trigger newTrigger =
                    tb.withSchedule(cronSchedule(cronExcr))
                            .build();

            Date ft = scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
            _log.info(jobName + " has been scheduled to run at: " + ft
                    + " and repeat based on expression: " + newTrigger.getNextFireTime());

        } catch (SchedulerException sce) {
            _log.error("Error while update the new job ...", sce);
            return sce.getMessage();
        } catch (Exception e) {
            _log.error("Error while update the new job ...", e);
            return e.getMessage();
        }

        return "Job successfully updated and rescheduled ...JobName :" + jobName
                + "....GroupName :" + _groupName;
    }

    
    @RequestMapping(value = "/deleteJobTrigger", method = RequestMethod.GET)
    public String deleteJobTrigger(@RequestParam(value = "jobName", required = false,
            defaultValue = "myFirstJob") String jobName, @RequestParam(value = "triggerName",
            required = false, defaultValue = "myFirstTrigger") String triggerName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        _log.info("deleteJobTrigger ... job name {}", jobName);
        try {
            if (!scheduler.checkExists(JobKey.jobKey(jobName, _groupName))) {
                _log.error(
                        "JobName not exists....in our group.. pls re-check the name... jobname {}, GroupName{}",
                        jobName, _groupName);
                return "JobName not exists, Please re-check the name : " + jobName
                        + " : group name : " + _groupName;
            }
            scheduler.deleteJob(JobKey.jobKey(jobName, _groupName));

        } catch (SchedulerException sce) {
            _log.error("Error while deleteJobTrigger the new job ...", sce);
            return sce.getMessage();
        } catch (Exception e) {
            _log.error("Error while deleteJobTrigger the new job ...", e);
            return e.getMessage();
        }

        return "Job successfully deleteJobTrigger ...JobName :" + jobName + "....GroupName :"
                + _groupName;
    }
    
    @RequestMapping(value = "/createCronJobWithExcludeHolidays", method = RequestMethod.GET)
    public String createCronJobWithExcludeHolidays(@RequestParam(value = "jobName", required = false,
            defaultValue = "myFirstJob") String jobName, @RequestParam(value = "triggerName",
            required = false, defaultValue = "myFirstTrigger") String triggerName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        _log.info("createJob ... job name {}", jobName);
        try {
            JobDataMap jobDataMap = new JobDataMap();
            JobDetail job = prepareJobDetailWithDataMap(jobName, triggerName, jobDataMap);

            // cron trigger the job to run on the every 20 sec.
            CronTrigger trigger =
                    newTrigger().withIdentity(triggerName, _groupName)
                            .withSchedule(cronSchedule("0/20 * * * * ?")).modifiedByCalendar("myHolidays").build();

            Date ft = scheduler.scheduleJob(job, trigger);
            _log.info(job.getKey() + " has been scheduled to run at: " + ft
                    + " and repeat based on expression: " + trigger.getCronExpression());

            if (scheduler.checkExists(JobKey.jobKey(jobName, _groupName))) {
                _log.info(
                        "This job has triggered using rest-controller ....Job name : {}.. Trigger name {} & groupname {}",
                        jobName, triggerName, _groupName);
            }

        } catch (SchedulerException sce) {
            _log.error("Error while create the new job ...", sce);
            return sce.getMessage();
        } catch (Exception e) {
            _log.error("Error while create the new job ...", e);
        }
        return "Job successfully added and triggered ...JobName :" + jobName + "....GroupName :"
                + _groupName;
    }
    
    /**
     * Generate a CRON expression is a string comprising 6 or 7 fields separated by white space.
     *
     * @param seconds    mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param minutes    mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param hours      mandatory = yes. allowed values = {@code 0-23   * / , -}
     * @param dayOfMonth mandatory = yes. allowed values = {@code 1-31  * / , - ? L W}
     * @param month      mandatory = yes. allowed values = {@code 1-12 or JAN-DEC    * / , -}
     * @param dayOfWeek  mandatory = yes. allowed values = {@code 0-6 or SUN-SAT * / , - ? L #}
     * @param year       mandatory = no. allowed values = {@code 1970–2099    * / , -}
     * @return a CRON Formatted String.
     */
    private static String generateCronExpression(final String seconds, final String minutes, final String hours,
                                                 final String dayOfMonth,
                                                 final String month, final String dayOfWeek, final String year)
    {
      return String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", seconds, minutes, hours, dayOfMonth, month, dayOfWeek, year);
    }

    
}
