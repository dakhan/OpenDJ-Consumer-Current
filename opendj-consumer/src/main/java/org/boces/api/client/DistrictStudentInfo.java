package org.boces.api.client;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName ("xStudent")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DistrictStudentInfo extends DistrictPersonInfo {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("demographics")
	private Demographics demographics;
	
	@JsonProperty("enrollment")
	private Enrollment enrollment;
		
	//demographics breakout
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
	
	//enrollment breakout
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
