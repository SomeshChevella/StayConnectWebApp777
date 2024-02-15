package com.stayconnected4.models;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private Name nameInfo;
	private String gender;
	private int age;
	private String skillSummary;
	private String phoneNum;
	private int locId;
	private Location location;
	private List<String> skills;
	private List<Skill> skillProficiencies;
	private List<Name> nameList;
	
	public Profile(){}
	
	public Profile(String email,String gender,int age, String skillSummary,String phoneNum,
			Location location,List<String> skills){
		this.email = email;
		this.gender = gender;
		this.age = age;
		this.skillSummary = skillSummary;
		this.phoneNum = phoneNum;
		this.location = location;
		this.skills = skills;	
	}
	
	public Profile(String email,String gender,int age, String skillSummary,String phoneNum,
	Location location) {
		this.email = email;
		this.gender = gender;
		this.age = age;
		this.skillSummary = skillSummary;
		this.phoneNum = phoneNum;
		this.location = location;
	}

	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getSkillSummary() {
		return skillSummary;
	}
	public void setSkillSummary(String skillSummary) {
		this.skillSummary = skillSummary;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
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
	public List<Skill> getSkillProficiencies() {
		return skillProficiencies;
	}
	public void setSkillProficiencies(List<Skill> skillProficiencies) {
		this.skillProficiencies = skillProficiencies;
	}
	public Name getNameInfo() {
		return nameInfo;
	}
	public void setNameInfo(Name nameInfo) {
		this.nameInfo = nameInfo;
	}

	public int getLocId() {
		return locId;
	}

	public void setLocId(int locId) {
		this.locId = locId;
	}

	public List<Name> getNameList() {
		return nameList;
	}

	public void setNameList(List<Name> nameList) {
		this.nameList = nameList;
	}


	
}
