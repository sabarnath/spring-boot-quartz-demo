
package com.poc.bootquartz.examples.example1;

import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This is just a simple job that says "Hello" to the world.
 * </p>
 * 
 * @author 
 */
public class RestControllerJob implements Job {

    private static Logger _log = LoggerFactory.getLogger(RestControllerJob.class);

    /**
     * <p>
     * Empty constructor for job initilization
     * </p>
     * <p>
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     * </p>
     */
    public RestControllerJob() {
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a
     * <code>{@link org.quartz.Trigger}</code> fires that is associated with
     * the <code>Job</code>.
     * </p>
     * 
     * @throws JobExecutionException
     *             if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        _log.info("RestControllerJob triggered - " + new Date());
        _log.info("----------------------------------------------------------------");
        _log.info("getFireTime....{}",context.getFireTime());
        _log.info("getJobDetail....{}",context.getJobDetail().getKey());
        _log.info("getJobInstance....{}",context.getJobInstance());
        _log.info("getJobRunTime......{}",context.getJobRunTime());
        _log.info("getNextFireTime......{}",context.getNextFireTime());
        _log.info("getPreviousFireTime......{}",context.getPreviousFireTime());
       // _log.info("getRecoveringTriggerKey......{}",context.getRecoveringTriggerKey().getName());
        _log.info("getScheduledFireTime......{}",context.getScheduledFireTime());
      
        JobDataMap jobdatamap= context.getMergedJobDataMap();
        if(jobdatamap != null){
            _log.info("All the data map details : {}",jobdatamap.toString());
            for (Entry<String, Object> entry : jobdatamap.entrySet()) {
            	_log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }
        _log.info("----------------------------------------------------------------");
        _log.info("Gonna trigger the batch job");
        //HttpCall
        CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpGet httpGet = new HttpGet("http://localhost:8080/runUserImport");
        
        try {
            HttpResponse httpResp = httpClient.execute(httpGet);
            _log.info("Successfully sent the Job request from scheduler trigger.....{}",httpResp.getStatusLine());
        } catch (ClientProtocolException e) {
            _log.error("Error while call the httpclient ",e);
        } catch (IOException e) {
            _log.error("Error while call the httpclient ",e);
        }
    }
}
