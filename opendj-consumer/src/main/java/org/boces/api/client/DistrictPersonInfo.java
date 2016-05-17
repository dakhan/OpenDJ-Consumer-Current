package org.boces.api.client;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistrictPersonInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String districtName;
	
	private String districtId;
	
	@JsonProperty("@refId")
	private String refId;
	
	@JsonProperty("name")
	private Name name;
	
	@JsonProperty("localId")
	private String localId;
	
	@JsonProperty("stateProvinceId")
	private String stateProvinceId;
	
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
	
	public String getLocalId(){
		return localId;
	}
	public void setLocalId(String localId){
		this.localId = localId;
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
	
	//Name breakout
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
}
