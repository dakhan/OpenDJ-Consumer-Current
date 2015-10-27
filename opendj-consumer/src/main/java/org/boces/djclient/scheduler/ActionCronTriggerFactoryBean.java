package org.boces.djclient.scheduler;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class ActionCronTriggerFactoryBean extends CronTriggerFactoryBean {

   @Autowired
   private ScheduledActionRunnerJobDetailFactory jobDetailFactory;

   @Value("${cron.pattern}")
   private String pattern;

   @Override
   public void afterPropertiesSet() throws ParseException {
       setCronExpression(pattern);
       setJobDetail(jobDetailFactory.getObject());
       super.afterPropertiesSet();
   }

}
