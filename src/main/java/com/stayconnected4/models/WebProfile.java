package com.stayconnected4.models;

import java.util.List;
import javax.validation.Valid;

public class WebProfile {

	@Valid
	private String name;
	@Valid
	private String gender;
	@Valid
	private int age;
	@Valid
	private String skillSummary;
	@Valid
	private String phoneNum;
	@Valid
	private String country;
	@Valid
	private String state;
	@Valid
	private String city;
	@Valid
	private String zipCode;
	@Valid
	private String address;
	@Valid
	List<String> skills;

	public WebProfile() {
	}

	public WebProfile(String name, String gender, int age, String skillSummary, String phoneNum,
			String country, String state, String city, String zipCode, String address,
			 List<String> skills) {
		this.name = name;
		this.gender = gender;
		this.age = age;
		this.skillSummary = skillSummary;
		this.phoneNum = phoneNum;
		this.skills = skills;
		this.country = country;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}


}
