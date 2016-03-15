package org.boces.api.client;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName ("xStaff")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DistrictStaffInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Additional attributes inherited from the parent objects.
	private String districtName;
	private String districtId;
	
	//Json Attributes from REStful
	
	//CODE: For future use with usernames/passwords from API
	/*
	 * 
	 * @JsonProperty("@UserName")
	 * Private String userName;
	 * 
	 * @JsonProperty("@password")
	 * Private String password;
	 * 
	 */
	
	@JsonProperty("@refId")
	private String refId;
	
	@JsonProperty("name")
	private Name name;
	
	@JsonProperty("localId")
	private String localId;
	
	@JsonProperty("stateProvinceId")
	private String stateProvinceId;
	
	@JsonProperty("email")
	private Email email;
	
	@JsonProperty("primaryAssignment")
	private PrimaryAssignment primaryAssignment;
	
	/*
	public String getAccessCode() {
		return UUID.fromString(refId).toString();
	}
	*/
	
	//CODE: For future use with usernames/passwords from API
	/*
	 * public String getUsername(){
	 * 	return userName
	 * public String getPassword(){
	 * 	return password
	 * 
	 */
	
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	public String getStateProvinceId(){
		if(this.stateProvinceId != null){
			return stateProvinceId;
		}else{
			return "";
		}
	}
	public void setStateProvinceId(String stateProvinceId){
		this.stateProvinceId = stateProvinceId;
	}
	
	public String getLocalId(){
		if (this.localId != null) {
			return localId;
		}else{
			return "";
		}
	}
	public void setLocalId(String localId){
		this.localId = localId;
	}
	
	//name object
	public String getFamilyName() {
		return this.name.getFamilyName();
	}
	public String getGivenName() {
		return this.name.getGivenName();
	}
	public String getMiddleName() {
		return this.name.getMiddleName();
	}
	static class Name implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@JsonProperty("familyName")
		private String familyName;
		
		@JsonProperty("givenName")
		private String givenName;
		
		@JsonProperty("middleName")
		private String middleName;
		
		public String getFamilyName() {
			return familyName;
		}
		public String getGivenName() {
			return givenName;
		}
		public String getMiddleName() {
			if(middleName != null){
				return middleName;
			} else {
				return "";
			} 
		}
		
	}
	
	//email address object
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
	
	//primaryAssignment object
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