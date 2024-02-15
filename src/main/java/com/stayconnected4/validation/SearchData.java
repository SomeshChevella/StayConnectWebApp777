package com.stayconnected4.validation;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SearchData {
	@NotNull
	@NotEmpty
	private String query;
	
	@NotNull
	private String criteria;
	
	public final static String[] POSSIBLE_PROFILE_CRITERIA = new String[]{"email", "name", "gender", "age", "location", "skills"};
	public final static String[] POSSIBLE_JOB_CRITERIA = new String[]{"type", "title", "location", "skills", "employer"};

	
	public SearchData() {
		
	}
	
	public SearchData(String query, String criteria) {
		this.query = query;
		this.criteria = criteria;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
}
