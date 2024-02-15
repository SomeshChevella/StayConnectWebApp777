package com.stayconnected4.models;

import java.util.List;
import javax.validation.Valid;
public class WebJobPost {
	
	@Valid
	private int jobNum;
	@Valid
	private String jobType;
	@Valid
	private String jobTitle;
	@Valid
    private Location location;
	@Valid
    private Employer employer;
	@Valid
    private List<String> skills;
	

	
	public WebJobPost() {}
	
	public WebJobPost(int jobNum, String jobType, String jobTitle, Location location,
			List<String> skills, Employer employer) {
		
		this.jobNum = jobNum;
		this.jobType = jobType;
		this.jobTitle = jobTitle;
		this.location = location;
		this.skills = skills;
		this.employer=employer;
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