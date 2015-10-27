package org.boces.api.client;

import java.io.Serializable;

public class DistrictInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private DistrictLeasList districtLeasList;
	private DistrictStaffList staffList;
	private DistrictStudentList studentList;

	public DistrictLeasList getDistrictLeasList(){
		return districtLeasList;
	}
	public void setDistrictLeasList(DistrictLeasList districtLeasList){
		this.districtLeasList = districtLeasList;
	}
	public DistrictStaffList getStaffList() {
		return staffList;
	}
	public void setStaffList(DistrictStaffList staffList) {
		this.staffList = staffList;
	}
	
	public DistrictStudentList getStudentList() {
		return studentList;
	}
	public void setStudentList(DistrictStudentList studentList) {
		this.studentList = studentList;
	}
}
