package com.stayconnected4.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class WebAccountValidator implements Validator {
	
	@Override
	public boolean supports (Class<?> c) {
		return WebAccount.class.isAssignableFrom(c);
	}
	
	public void validate(Object target, Errors errors) {
		WebAccount account = (WebAccount)target;
		
		validatePassword(account, errors);
		validateRoles(account, errors);
	}
	
	private void validatePassword(WebAccount account, Errors errors) {
		String password = account.getPassword();
		String passwordConf = account.getPasswordConf();
		
		if (!password.equals(passwordConf)) {
			errors.rejectValue("password", "Account.password.mismatch");
			errors.rejectValue("passwordConf", "Account.password.mismatch");
		}
	}
	
	private void validateRoles(WebAccount customer, Errors errors) {
		if (!customer.hasValidRole()) {
			errors.rejectValue("roles", "Account.roles.empty");
		}
	}
}
