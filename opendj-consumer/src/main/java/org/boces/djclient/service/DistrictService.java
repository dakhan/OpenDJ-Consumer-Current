package org.boces.djclient.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.boces.api.client.DistrictInfo;
import org.boces.api.client.DistrictLeasList;
import org.boces.api.client.OAuthEndpointInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class DistrictService {
	
	private static final Logger log = LoggerFactory.getLogger(DistrictService.class);
	
	@Autowired
	private StaffService staffService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private LeaService leaService;
	
	@Autowired
    ConfigurableApplicationContext context;
	
	@Value("${district.alias.url}")
	String districtAliasUrl;

	//Builds a DistrictInfo Object using a specified endpoint and LeaRefId
	public DistrictInfo getDistrictInfo(OAuthEndpointInfo endpoint, DistrictLeasList leaList) {
		DistrictInfo districtInfo = new DistrictInfo();
		
		//Set DistrictName
		districtInfo.setDistrictLeasList(leaList);
		
		//Set Student List
		districtInfo.setStudentList(studentService.retrieveStudentList(endpoint, leaList));

		//Set Staff List
		districtInfo.setStaffList(staffService.retrieveStaffList(endpoint, leaList));
		
		return districtInfo;
	}
	
	@JmsListener(destination = "district-destination", containerFactory = "djConsumerJmsContainerFactory")
    public void receiveMessage(Object message) throws JMSException {
		OAuthEndpointInfo endpoint = (OAuthEndpointInfo) ((ActiveMQObjectMessage)message).getObject();
		log.info("Sending endpoint --- " + endpoint + " --- to be processed");
		DistrictLeasList leaList = leaService.retrieveLeaList(endpoint);
		DistrictInfo districtInfo = getDistrictInfo(endpoint,  leaList);
		sendDistrictInfoToQueue (districtInfo);
	}
	
	private void sendDistrictInfoToQueue(final DistrictInfo districtInfo) {
		// Send a message to the district-destination
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(districtInfo);
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        jmsTemplate.setDeliveryPersistent(true);
        log.info("Sending districts to the opendj-destination Queue.");
        jmsTemplate.send("opendj-destination", messageCreator);
	}
}
