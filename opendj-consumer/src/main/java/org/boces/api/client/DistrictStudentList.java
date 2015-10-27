package org.boces.api.client;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistrictStudentList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("xStudents")
	private XStudents xStudents;

	XStudents getxStudents() {
		return xStudents;
	}
	
	public List<DistrictStudentInfo> getDistrictStudentInfoList() {
		return xStudents.getStudentList();
	}
	
	static class XStudents implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@JsonProperty("xStudent")
		private List<DistrictStudentInfo> studentList;

		public List<DistrictStudentInfo> getStudentList() {
			return studentList;
		}

		public void setStudentList(List<DistrictStudentInfo> staffList) {
			this.studentList = staffList;
		}
	}
}
