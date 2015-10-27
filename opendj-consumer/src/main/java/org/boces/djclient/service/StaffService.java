package org.boces.djclient.service;

import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.boces.api.client.DistrictLeaInfo;
import org.boces.api.client.DistrictLeasList;
import org.boces.api.client.DistrictStaffInfo;
import org.boces.api.client.DistrictStaffList;
import org.boces.api.client.OAuthEndpointInfo;
import org.boces.djclient.ldap.LdapUtil;
import org.forgerock.opendj.ldap.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StaffService {
	
	private static final Logger log = LoggerFactory.getLogger(StaffService.class);
	
	@Autowired
	private LdapUtil ldapUtil;
	
	@Autowired
	private LeaService leaService;
	
	@Value("${staff.url}")
	String staffDataUrl;
	
	//Creates a staff list API URL based on a given leaRefId
	private String getStaffDataUrl(OAuthEndpointInfo endpoint) {
		String url = endpoint.getHref() + staffDataUrl + ".json?accessToken=" + endpoint.getToken();
		log.info("District Staff URL: " + url);
		return url;
	}
	
	/*
	 * Calls API and returns a DistrictStaff List based on a leaRefId
	 * Also cycles through staff objects and assigns leaRefId as districtId
	 * TODO: check necessity of assigning districtId
	*/
	public DistrictStaffList retrieveStaffList(OAuthEndpointInfo endpoint, DistrictLeasList leaList) {
		RestTemplate rt = new RestTemplate();
		DistrictStaffList staffList = rt.getForObject(getStaffDataUrl(endpoint), DistrictStaffList.class);
		if ((staffList != null) && (staffList.getDistrictStaffInfoList() != null) && !(staffList.getDistrictStaffInfoList().isEmpty())) {
			log.info("District Staff List Size = " + (staffList.getDistrictStaffInfoList().size()));
			List<DistrictStaffInfo> staffInfo = staffList.getDistrictStaffInfoList();
			Iterator<DistrictStaffInfo> staffInfoIter = staffInfo.iterator();
			/*
			 * For each staff object in the list set DistrictName attribute to match given Lea Name
			 * If a staff object returns a null value when attempting to pull the leaRefId, the object is removed from the list
			*/
			while(staffInfoIter.hasNext()){
				DistrictStaffInfo staffInfoObj = staffInfoIter.next();
				if(staffInfoObj.getleaRefId() != ""){
					//deserializes the leaList and returns the leaInfoObj for the given staffs leaRefId
					DistrictLeaInfo districtLeaObj = leaService.getDistrictObjectByRefId(leaList, staffInfoObj.getleaRefId());
					staffInfoObj.setDistrictName(districtLeaObj.getLeaName());
					staffInfoObj.setDistrictId(districtLeaObj.getLocalId());
				}else{
					log.debug("Removing Staff: "+ staffInfoObj.getRefId() + " from list" );
					staffInfoIter.remove();
				}
			}
		}
		return staffList;
	}

	/*
	 * Determines if object exists in openDJ
	 * If it does exists in openDJ then the object is updated
	 * If it does not exist in openDJ then object is sent to be created
	 */
	@JmsListener(destination = "staff-destination", containerFactory = "djConsumerJmsContainerFactory")
    public void receiveMessage(Object message) throws JMSException {
		DistrictStaffInfo staffInfo = (DistrictStaffInfo) ((ActiveMQObjectMessage)message).getObject();
		Entry staffEntry = ldapUtil.searchStaffEntry(staffInfo);
		if (staffEntry != null) {
			log.info("UPDATING the staff entry for RefID "+ staffInfo.getRefId() + " within the LDAP server.");
			ldapUtil.updateStaffEntry(staffEntry, staffInfo, 0);

		} else {
			log.info("CREATING the staff entry for RefID "+ staffInfo.getRefId() + " within the LDAP server.");
			ldapUtil.createStaffEntry(staffInfo, 0);
		}
	}
}