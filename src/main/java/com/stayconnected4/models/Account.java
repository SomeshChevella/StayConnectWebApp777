package com.stayconnected4.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String email;
	private String password;
	private boolean active;
	private List<String> roles;
	
	public Account() {
	}
	
	public Account(String email) {
		this.email = email;
	}
	
	public Account(String email, boolean active, String role) {
		this.email = email;
		this.active = active;
		this.roles = Arrays.asList(new String[] {role});
	}
	
	public Account(String email, String password, boolean active, List<String> roles) {
		this.email = email;
		this.password = password;
		this.active = active;
		this.roles = roles;
	}
	
	public Account(int id, String email, String password, boolean active, List<String> roles) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.active = active;
		this.roles = roles;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
