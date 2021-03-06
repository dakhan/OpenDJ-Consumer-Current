package org.boces.djclient.service;

import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.boces.api.client.DistrictLeaInfo;
import org.boces.api.client.DistrictLeasList;
import org.boces.api.client.DistrictStudentInfo;
import org.boces.api.client.DistrictStudentList;
import org.boces.api.client.OAuthEndpointInfo;
import org.boces.djclient.ldap.LdapUtil;
import org.forgerock.opendj.ldap.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StudentService {

	private static final Logger log = LoggerFactory.getLogger(StudentService.class);

	@Autowired
	private LdapUtil ldapUtil;
	
	@Autowired
	private LeaService leaService;
	
	@Value("${student.url}")
	String studentDataUrl;
	
	//Creates a student list API URL eg... "https://sandbox.ricone.org/api/requests/xStudents?"
	private String getStudentDataUrl(OAuthEndpointInfo endpoint) {
		String studentUrl = endpoint.getHref() + studentDataUrl;
		log.info("Student URL " + studentUrl);
		return studentUrl;
	}
	
	//capture invidual page of student data from API
	public DistrictStudentList retrieveStudentListPage(OAuthEndpointInfo endpoint, Integer navigationPage, Integer navigationPageSize ) {
		
		String paging = "navigationPage={navigationPage}&navigationPageSize{navigationPageSize}";
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		//use headers to send authorization token
		try{
			headers.set("Authorization", "Bearer" + endpoint.getToken());
			HttpEntity<?> entity = new HttpEntity<DistrictStudentList>(headers);
			ResponseEntity<DistrictStudentList> response = rt.exchange(getStudentDataUrl(endpoint) + paging, HttpMethod.GET, entity, DistrictStudentList.class, navigationPage, navigationPageSize);
			return response.getBody();
		}
		catch(Exception e){
			return null;
		}
	}
	
	//capture a full list of student data from API
	public DistrictStudentList retrieveStudentList(OAuthEndpointInfo endpoint, DistrictLeasList leaList, Integer navigationPageSize ) {
		
		DistrictStudentList studentList = new DistrictStudentList();
		Integer x = 1;
		
		//Get individual pages of the student list from the API and reassemble those pages into a single student list
		while(x != 0){
			DistrictStudentList studentListPage = retrieveStudentListPage(endpoint, x, navigationPageSize);
			if((studentListPage != null) && (studentListPage.getDistrictStudentInfoList() != null) && !(studentListPage.getDistrictStudentInfoList().isEmpty())){
				if(studentList.getxStudents() == null){
					studentList = studentListPage;
				}
				else
				{
					studentList.merge(studentListPage);
				}
				x++;
			}
			else
			{
				x=0;
			}
		}
		if ((studentList != null) && (studentList.getDistrictStudentInfoList() != null) && !(studentList.getDistrictStudentInfoList().isEmpty())) {
			log.info("District Student List Size = " + (studentList.getDistrictStudentInfoList().size()));
			List<DistrictStudentInfo> studentInfo = studentList.getDistrictStudentInfoList();
			Iterator<DistrictStudentInfo> studentInfoIter = studentInfo.iterator();
			/*
			 * For each student object in the list set districtId attribute to match given Lea refId
			 * If a student object returns a null value when attempting to pull the leaRefId, the object is removed the the list
			*/
			while(studentInfoIter.hasNext()){
				DistrictStudentInfo studentInfoObj = studentInfoIter.next();
				if(studentInfoObj.getleaRefId() != ""){
					//deserializes the leaList and returns the leaInfoObj for the given students leaRefId
					DistrictLeaInfo districtLeaObj = leaService.getDistrictObjectByRefId(leaList, studentInfoObj.getleaRefId());
					studentInfoObj.setDistrictName(districtLeaObj.getLeaName());
					studentInfoObj.setDistrictId(districtLeaObj.getLocalId());
				}else{
					log.debug("Removing Student: "+ studentInfoObj.getRefId() + " from list");
					studentInfoIter.remove();
				}
			}
		}
		return studentList;
	}
	
	/*
	 * Determines if object exists in openDJ
	 * If it does exists in openDJ then the object is updated
	 * If it does not exist in openDJ then object is sent to be created
	 */
	@JmsListener(destination = "student-destination", containerFactory = "djConsumerJmsContainerFactory")
    public void receiveMessage(Object message) throws JMSException {
		DistrictStudentInfo studentInfo = (DistrictStudentInfo) ((ActiveMQObjectMessage)message).getObject();
		// Create or update the existing student entry.
		Entry studentEntry = ldapUtil.searchStudentEntry(studentInfo);
		if (studentEntry != null) {
			log.info("UPDATING the student entry for RefID "+ studentInfo.getRefId() + " within the LDAP server.");
			ldapUtil.updateStudentEntry(studentEntry, studentInfo, 0);
		} else  {
			log.info("CREATING the student entry for RefID "+ studentInfo.getRefId() + " within the LDAP server.");
			ldapUtil.createStudentEntry(studentInfo, 0);
		}
	}
}