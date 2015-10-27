package org.boces.djclient.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduledActionRunner implements Job {
	
	private ScheduleService scheduleService;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// Retrieve the Schedule Service from the Job Data Map.
		scheduleService = (ScheduleService) arg0.getMergedJobDataMap().get("scheduleService");
		
		// Invoke the Scheduled Service specific API..
		scheduleService.executeScheduledJob();
	}

}
