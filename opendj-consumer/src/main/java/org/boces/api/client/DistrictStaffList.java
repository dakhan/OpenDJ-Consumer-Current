package org.boces.api.client;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistrictStaffList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("xStaffs")
	private XStaffs xStaffs;

	public XStaffs getxStaffs() {
		return xStaffs;
	}
	
	public List<DistrictStaffInfo> getDistrictStaffInfoList() {
		return xStaffs.getStaffList();
	}
	
	static class XStaffs implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@JsonProperty("xStaff")
		private List<DistrictStaffInfo> staffList;

		public List<DistrictStaffInfo> getStaffList() {
			return staffList;
		}

		public void setStaffList(List<DistrictStaffInfo> staffList) {
			this.staffList = staffList;
		}
	}
}
