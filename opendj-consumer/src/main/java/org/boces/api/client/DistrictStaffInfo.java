package org.boces.api.client;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName ("xStaff")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DistrictStaffInfo extends DistrictPersonInfo {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("email")
	private Email email;
	
	@JsonProperty("primaryAssignment")
	private PrimaryAssignment primaryAssignment;
	
	
	//email address breakout
	public String getEmailAddress() {
		if(this.email != null){
			return this.email.getEmailAddress();
		}else{
			return "";
		}
	}
	static class Email implements Serializable {
		private static final long serialVersionUID = 1L;

		@JsonProperty("emailAddress")
		private String emailAddress;
		
		public String getEmailAddress() {
			return emailAddress;
		}
	}
	
	//primaryAssignment breakout
	public String getleaRefId() {
		if(this.primaryAssignment != null){
			return this.primaryAssignment.getleaRefId();
		}else{
			return "";
		}
	}
	public String getschoolRefId() {
		return this.primaryAssignment.getschoolRefId();
	}
	static class PrimaryAssignment implements Serializable {
		private static final long serialVersionUID = 1L;

		@JsonProperty("leaRefId")
		private String leaRefId;
		
		@JsonProperty("schoolRefId")
		private String schoolRefId;
		
		public String getleaRefId() {
			return leaRefId;
		}
		
		public String getschoolRefId() {
			return schoolRefId;
		}
	}
	
}

/*
 * API Reference object
 * {
 * 		"@refId": "redacted",
 * 		"name": {
 * 			"familyName": "redacted",
 * 			"givenName": "redacted",
 * 			"middleName": "redacted"
 * 		},
 * 		"email": {
 * 			"emailAddress": "redacted"
 * 		},
 * 		"primaryAssignment": {
 * 			"leaRefId": "redacted",
 * 			"schoolRefId": "redacted"
 * 		}
 * },
*/