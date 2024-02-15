package com.stayconnected4.models;

import java.io.Serializable;

public class Location implements Serializable {
	private int id; 
	private String state = "";
	private String city = "";
	private String address = "";
	private String zipCode = "";
	private String country = "";

	public Location(String country,String state,String city,String zipCode,String address) {
		this.country = country;
		this.state=state;
		this.city=city;
		this.zipCode=zipCode;
		this.address=address;
		
	}
	
	public Location() {
	
	}
	
	public Location(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
}
