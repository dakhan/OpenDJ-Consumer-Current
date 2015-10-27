/*
 * TODO: Logging in sendDistrict--- methods has been disabled to preseve scroll real-estate
*/
package org.boces.djclient.service;

import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.boces.api.client.DistrictInfo;
import org.boces.api.client.DistrictLeaInfo;
import org.boces.api.client.DistrictLeasList;
import org.boces.api.client.DistrictStaffInfo;
import org.boces.api.client.DistrictStudentInfo;
import org.boces.djclient.ldap.LdapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class LdapService {

	private static final Logger log = LoggerFactory.getLogger(LdapService.class);
	
	@Autowired
    ConfigurableApplicationContext context;
	
	@Autowired
	LdapUtil ldapUtil;
	
	/*
	 * Receives districtInfo object from districtService.java
	 * sends the districtName attribute to the LdapUtility to be processed into an OU
	 * sends the staff list to the pre-ldap processing
	 * sends the student list to pre-ldap processing
	 */
	@JmsListener(destination = "opendj-destination", containerFactory = "djConsumerJmsContainerFactory")
    public void receiveMessage(Object message) throws JMSException {
		log.info("Retrieved district info object from the opendj destination " + message);
		DistrictInfo districtInfo = (DistrictInfo) ((ActiveMQObjectMessage)message).getObject();
		
		/*
		 * Deserializes lea list and calls createLeaEntry method form the LdapUtility class
		 */
		DistrictLeasList districtLeasList = districtInfo.getDistrictLeasList();
		List<DistrictLeaInfo> districtLeaInfo = districtLeasList.getDistrictLeaInfoList();
		Iterator<DistrictLeaInfo>districtLeaInfoIter = districtLeaInfo.iterator();
		while(districtLeaInfoIter.hasNext()){
			DistrictLeaInfo districtLeaInfoObj = districtLeaInfoIter.next();
			districtLeaInfoObj.setDistrictName(districtLeaInfoObj.getLeaName());
			log.info("Sending " + districtLeaInfoObj.getDistrictName() + "to the LDAP consumer");
			ldapUtil.createLeaEntry(districtLeaInfoObj.getDistrictName());
		}
		
		//Send the list of staff to be processed
		if (districtInfo.getStaffList() != null)
			log.info("Sending " + districtInfo.getStaffList().getDistrictStaffInfoList().size() + " staff accounts to opendj-destination queue");
			sendDistrictStaffInfoToQueue(districtInfo.getStaffList().getDistrictStaffInfoList());
		
		//Semd the list of students to be processed
		if (districtInfo.getStudentList() != null)
			log.info("Sending " + districtInfo.getStudentList().getDistrictStudentInfoList().size() + " student accounts to opendj-destination queue");
			sendDistrictStudentInfoToQueue(districtInfo.getStudentList().getDistrictStudentInfoList());
	}
	
	/*
	 * Deserializes student list and sends each student object to LDAP processing
	 */
	private void sendDistrictStudentInfoToQueue(List<DistrictStudentInfo> districtStudentInfoList) {

		if (districtStudentInfoList == null || districtStudentInfoList.isEmpty()) return;
		
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		
        Iterator<DistrictStudentInfo> iter = districtStudentInfoList.iterator();
        
        while (iter.hasNext()) {
        	DistrictStudentInfo student = iter.next();
        	log.debug("Sending the following Student object to the onefed-destination Queue. " + student);
        	jmsTemplate.setDeliveryPersistent(true);
	        jmsTemplate.send("student-destination", getMessageCreatorForStudent(student));
        }
	}
	
	//JMS message handling for student objects
	private MessageCreator getMessageCreatorForStudent(final DistrictStudentInfo student) {
		// Send a message to the district-destination
		MessageCreator messageCreator = new MessageCreator() {
		    @Override
		    public Message createMessage(Session session) throws JMSException {
		        return session.createObjectMessage(student);
		    }
		};
		return messageCreator;
	}
	
	/*
	 * Deserializes staff list and sends each staff object to LDAP Processing
	 */
	private void sendDistrictStaffInfoToQueue(List<DistrictStaffInfo> districtStaffInfoList) {
		// Return if the list is empty or null.
		if (districtStaffInfoList == null || districtStaffInfoList.isEmpty()) return;
		
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		
        Iterator<DistrictStaffInfo> iter = districtStaffInfoList.iterator();
        
        while (iter.hasNext()) {
        	DistrictStaffInfo staff = iter.next();
        	log.debug("Sending the following Student object to the onefed-destination Queue. " + staff);
        	jmsTemplate.setDeliveryPersistent(true);	
	        jmsTemplate.send("staff-destination", getMessageCreatorForStaff(staff));
        }
	}
	
	//JMS message handling for staff objects
	private MessageCreator getMessageCreatorForStaff(final DistrictStaffInfo staff) {
		// Send a message to the district-destination
		MessageCreator messageCreator = new MessageCreator() {
		    @Override
		    public Message createMessage(Session session) throws JMSException {
		        return session.createObjectMessage(staff);
		    }
		};
		return messageCreator;
	}
}
