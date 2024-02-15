package com.stayconnected4.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SearchDataValidator implements Validator{
	@Override
	public boolean supports (Class<?> c) {
		return WebAccount.class.isAssignableFrom(c);
	}
	
	public void validate(Object target, Errors errors) {
		SearchData search = (SearchData)target;
		if (search.getQuery().length() == 0) {
			errors.rejectValue("criteria", "SearchData.criteria.empty");
		}
	}
}
