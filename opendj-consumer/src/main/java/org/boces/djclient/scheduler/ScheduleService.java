package org.boces.djclient.scheduler;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.boces.api.client.OAuthClient;
import org.boces.api.client.OAuthEndpointInfo;
import org.boces.api.client.OAuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
	
	@Autowired
	private OAuthClient oauthClient;
	
	@Autowired
    ConfigurableApplicationContext context;
	
	public void executeScheduledJob() {
		// Invoke the OAuth2 Login API
		OAuthInfo oauthInfo = oauthClient.retrieveTokensAndEndpoints();
		
		// push the individual endpoint info objects to the district queue.
		for (OAuthEndpointInfo endpoint: oauthInfo.getEndpoint()) {
			// send the endpoint info to a queue.
			sendEndpointToQueue(endpoint);
		}
	}

	private void sendEndpointToQueue(final OAuthEndpointInfo endpoint) {
		// Send a message to the district-destination
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(endpoint);
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        jmsTemplate.setDeliveryPersistent(true);
        log.info("Sending the following object to the district-destination Queue. " + endpoint);
        jmsTemplate.send("district-destination", messageCreator);
	}
	
}
