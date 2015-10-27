package org.boces.djclient.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class ScheduledActionRunnerJobDetailFactory extends JobDetailFactoryBean {

    @Autowired
    private ScheduleService scheduleService;
    
    public ScheduledActionRunnerJobDetailFactory() {
    	// set durability to true.
		setDurability(true);
	}

    @Override
    public void afterPropertiesSet() {
       setJobClass(ScheduledActionRunner.class);
       Map<String, Object> data = new HashMap<String, Object>();
       data.put("scheduleService", scheduleService);
       setJobDataAsMap(data);
       super.afterPropertiesSet();
   }
}