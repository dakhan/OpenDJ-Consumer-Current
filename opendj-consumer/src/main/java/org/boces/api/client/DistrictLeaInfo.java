package org.boces.api.client;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName ("xLea")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DistrictLeaInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	// Additional attributes inherited from the parent objects.
	private String districtName;
	private String districtId;
	
	//Json attributes from RESTful
	@JsonProperty("@refId")
	private String refId;
	
	@JsonProperty("localId")
	private String localId;
	
	@JsonProperty("stateProvinceId")
	private String stateProvinceId;
	
	@JsonProperty("leaName")
	private String leaName;
	
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
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}
	
	public String getStateProvinceId() {
		return stateProvinceId;
	}

	public void setStateProvinceId(String stateProvinceId) {
		this.stateProvinceId = stateProvinceId;
	}

	public String getLeaName() {
		return leaName;
	}

	public void setLeaName(String leaName) {
		this.leaName = leaName;
	}
	
}
/*Reference API Object
 * [
 * {
 * 		"@refId": "redacted",
 * 		"localId": "redacted",
 * 		"stateProvinceId": "redacted",
 * 		"leaName": "redacted"
 * },
 */