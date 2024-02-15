package com.stayconnected4.models;

public class Employment {
	private int jobNum;
	private String employer;
	private String jobType;
	private String jobTitle;
	
	public Employment () {}
	public Employment(int jobNum, String employer, String jobType, String jobTitle) {
		super();
		this.jobNum = jobNum;
		this.employer = employer;
		this.jobType = jobType;
		this.jobTitle = jobTitle;
	}
	
	public int getJobNum() {
		return jobNum;
	}
	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}
	public String getEmployer() {
		return employer;
	}
	public void setEmployer(String employer) {
		this.employer = employer;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
}
