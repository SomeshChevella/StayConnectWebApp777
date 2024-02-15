package com.stayconnected4.models;

import java.io.Serializable;
import java.util.List;

public class JobPost implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Account account;
	private int jobNum;
	private String jobType;
	private String jobTitle;
	private Location location;
	private Employer employer;
	private List<String> skills;
	
	public JobPost() {}
	public JobPost(Account account, int jobNum, String jobType, String jobTitle, Location location,
			List<String> skills) {
		super();
		this.account = account;
		this.jobNum = jobNum;
		this.jobType = jobType;
		this.jobTitle = jobTitle;
		this.location = location;
		this.skills = skills;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public int getJobNum() {
		return jobNum;
	}
	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
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
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public List<String> getSkills() {
		return skills;
	}
	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
	public Employer getEmployer() {
		return employer;
	}
	public void setEmployer(Employer employer) {
		this.employer = employer;
	}
	
	
}