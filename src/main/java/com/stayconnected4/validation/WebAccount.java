package com.stayconnected4.validation;


import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.stayconnected4.models.Account;

public class WebAccount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Email
	private String email;
	
	@NotNull
	@NotBlank
	private String password;
	
	@NotNull
	@NotBlank
	private String passwordConf;
	
	private boolean active;
	
	private String role;
	
	public WebAccount() {	}

	public WebAccount(@NotBlank @Email String email, @NotNull @NotBlank String password,
			@NotNull @NotBlank String passwordConf, List<String> roles) {
		super();
		this.email = email;
		this.password = password;
		this.passwordConf = passwordConf;
		if (roles.size() > 0) {
			this.role = roles.get(0);
		}
	}
	
	public WebAccount(Account account) {
		this.email = account.getEmail();
		this.password = account.getPassword();
		this.passwordConf = account.getPassword();
		this.active = account.getActive();
		if (account.getRoles().size() > 0) {
			this.role = account.getRoles().get(0);
		}
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

	public String getPasswordConf() {
		return passwordConf;
	}

	public void setPasswordConf(String passwordConf) {
		this.passwordConf = passwordConf;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean hasValidRole() {
		return role != null && !role.equals("");
	}
	
}
