package com.stayconnected4.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Name implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String profileEmail;
	private String name;
	private Timestamp startTime;
	private Timestamp endTime;
	
	public Name() {}

	public Name(String profileEmail, String name) {
		this.profileEmail = profileEmail;
		this.name = name;
	}
	public Name(String profileEmail,String name, Timestamp startTime, Timestamp endTime) {
		super();
		this.profileEmail = profileEmail;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getProfileEmail() {
		return profileEmail;
	}

	public void setProfileEmail(String profileEmail) {
		this.profileEmail = profileEmail;
	}
	
	
}
