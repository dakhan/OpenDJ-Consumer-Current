package org.boces.api.client;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName ("xStudent")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DistrictStudentInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Additional attributes inherited from the parent.
	private String districtName;
	private String districtId;
	
	//Json attributes from RESTful
	@JsonProperty("@refId")
	private String refId;
	
	@JsonProperty("name")
	private Name name;
	
	@JsonProperty("localId")
	private String localId;
	
	@JsonProperty("stateProvinceId")
	private String stateProvinceId;
	
	@JsonProperty("demographics")
	private Demographics demographics;
	
	@JsonProperty("enrollment")
	private Enrollment enrollment;
	/*
	public String getAccessCode() {
		return UUID.fromString(refId).toString();
	}
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
	
	public String getLocalId() {
		if (this.localId != null) {
			return localId;
		}else{
			return "";
		}
	}
	public void setLocalId(String localId) {
		this.localId = localId;
	}
	
	public String getStateProvinceId() {
		if(this.stateProvinceId != null){
			return stateProvinceId;
		}else{
			return "";
		}
	}
	public void setStateProvinceId(String stateProvinceId) {
		this.stateProvinceId = stateProvinceId;
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
				}else{
					return "";
				}
			}
		}
		
	//demographics object
	public String getBirthDate() {
		if(demographics != null){
			return this.demographics.getBirthDate();
		}else{
			return "";
		}
	}
	static class Demographics implements Serializable {
			private static final long serialVersionUID = 1L;

			@JsonProperty("birthDate")
			private String birthDate;
			
			public String getBirthDate() {
				return birthDate;
			}
		}
	
	//enrollment object
	public String getleaRefId() {
		if(this.enrollment != null){
			return this.enrollment.getleaRefId();
		}else{
			return "";
		}
	}
	public String getschoolRefId() {
		return this.enrollment.getschoolRefId();
	}
	static class Enrollment implements Serializable {
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
 *      	"familyName": "redacted",
 *      	"givenName": "redacted",
 *      	"middleName": "redacted"
 *      },
 *      "localId": "redacted",
 *      "stateProvinceId": "redacted",
 *      "demographics": {
 *      	"birthDate": "redacted"
 *      },
 *      "enrollment": {
 *      	"leaRefId": "redacted",
 *      	"schoolRefId": "redacted"
 *      }
 *}
*/
