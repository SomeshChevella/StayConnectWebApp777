package com.stayconnected4.validation;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

public class WebCode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Pattern(regexp = "\\d{8}")
	private String code;
	
	public WebCode() {}
	public WebCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
