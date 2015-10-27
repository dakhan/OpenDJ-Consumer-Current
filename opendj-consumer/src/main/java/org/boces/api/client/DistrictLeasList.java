package org.boces.api.client;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistrictLeasList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("xLeas")
	private XLeas xLeas;

	public XLeas getxLeas() {
		return xLeas;
	}
	
	public List<DistrictLeaInfo> getDistrictLeaInfoList() {
		return xLeas.getLeaList();
	}
	
	static class XLeas implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@JsonProperty("xLea")
		private List<DistrictLeaInfo> leaList;

		public List<DistrictLeaInfo> getLeaList() {
			return leaList;
		}

	}
}
