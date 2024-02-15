package com.stayconnected4.controllers;

import java.util.Arrays;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.stayconnected4.daos.AccountDao;
import com.stayconnected4.models.Account;
import com.stayconnected4.validation.WebAccount;
import com.stayconnected4.validation.WebAccountValidator;
import com.stayconnected4.validation.WebCode;

@Controller
public class AccountController {
	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private WebAccountValidator webAccountValidator;
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String displayRegister(Model model) {
		model.addAttribute("roles", accountDao.getAvailableRoles());
		model.addAttribute("account", new WebAccount());
		return "Register";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegister(@Valid @ModelAttribute("account")WebAccount account, BindingResult errors, Model model, HttpSession session) {
		webAccountValidator.validate(account, errors);
		if (errors.hasErrors()) {
			model.addAttribute("roles", accountDao.getAvailableRoles());
			return "Register";
		}
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		session.setAttribute("account", account);
		generateCode(session);
		return "redirect:/register/verify";
	}
	
	@RequestMapping(value="/register/verify", method=RequestMethod.GET)
	public String displayRegisterVerify(Model model, HttpSession session) {
		model.addAttribute("code", new WebCode());
		return "RegisterVerify";
	}
	
	@RequestMapping(value="/code/new", method=RequestMethod.POST)
	public String generateCode(HttpSession session) {
		Random random = new Random();
		String code = String.format("%08d", random.nextInt(100000000));
		session.setAttribute("code", code);
		sendEmail(code);
		return "redirect:/register/verify";
	}
	
	@RequestMapping(value="/register/verify", method=RequestMethod.POST)
	public String processRegisterVerify(@Valid @ModelAttribute("code")WebCode inputCode, BindingResult errors, Model model, HttpSession session) {
		String systemCode = (String)session.getAttribute("code");
		if (systemCode == null) {
			return "redirect:/register";
		}
		else if (!systemCode.equals(inputCode.getCode())) {
			errors.rejectValue("code", "System.code.mismatch");
			return "RegisterVerify";
		}
		WebAccount webAccount = (WebAccount)session.getAttribute("account");
		try {
			accountDao.addUserAccount(new Account(webAccount.getEmail(),
					webAccount.getPassword(), true, Arrays.asList(new String[] {webAccount.getRole()})));
		}
		catch(Exception e) {
			System.out.println(e);
			return "redirect:/register";
		}
		session.removeAttribute("code");
		session.removeAttribute("account");
		session.setAttribute("email", webAccount.getEmail());
		return "redirect:/register/confirm";
	}
	
	@RequestMapping(value="/register/confirm", method=RequestMethod.GET)
	public String displayRegisterConfirm(Model model, HttpSession session) {
		String email = (String)session.getAttribute("email");
		if (email == null) {
			return "redirect:/register";
		}
		
		Account account = accountDao.getAccountByEmail(email);
		model.addAttribute("account", account);
		return "RegisterConfirm";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String displayLogin() {
		return "Login";
	}
	
	@RequestMapping(value="/account/list", method=RequestMethod.GET)
	public String displayAccountList(Model model) {
		model.addAttribute("accounts", accountDao.getAllAccounts());
		return "AccountList";
	}
	
	@RequestMapping(value="/account/modify", method=RequestMethod.GET)
	public String displayAccountModify(@RequestParam("id")int id, Model model, HttpSession session) {
		session.setAttribute("id", id);
		model.addAttribute("account", new WebAccount(accountDao.getAccountById(id)));
		model.addAttribute("possibleBools", Arrays.asList(new Boolean[] {true, false}));
		model.addAttribute("possibleRoles", accountDao.getAvailableRoles());
		return "AccountModify";
	}
	
	@RequestMapping(value="/account/modify", method=RequestMethod.POST)
	public String processAccountModify(@Valid @ModelAttribute("account")WebAccount webAccount, BindingResult errors, Model model, HttpSession session) {
		Integer id = (Integer)session.getAttribute("id");
		if (id == null) {
			return "redirect:/account/list";
		}
		Account account = generateAccount(accountDao.getAccountById(id).getEmail(), webAccount);
		accountDao.updateAccount(account);
		return "redirect:/account/modify/confirm";
	}
	
	@RequestMapping(value="/account/modify/confirm", method=RequestMethod.GET)
	public String dislayAccountModifyConfirm(Model model, HttpSession session) {
		Integer id = (Integer)session.getAttribute("id");
		if (id == null) {
			return "redirect:/account/list";
		}
		Account account = accountDao.getAccountById(id);
		model.addAttribute("account", account);
		return "AccountModifyConfirm";
	}
	
	@RequestMapping(value={"/", "/home"}, method=RequestMethod.GET)
	public String displayHome() {
		return "Home";
	}
	
	@RequestMapping(value="/403", method=RequestMethod.GET)
	public String display403() {
		return "403";
	}
	
	@RequestMapping(value="/email", method=RequestMethod.GET)
	public String displayEmail(HttpSession session, Model model) {
		String code = (String)session.getAttribute("code");
		if (code == null) {
			return "redirect:/register";
		}
		model.addAttribute("code", code);
		return "Email";
	}
	
	private void sendEmail(String code) {
		System.out.printf("Your code is: %s\n", code);
		logger.info("Your code is: " + code);
	}
	
	private Account generateAccount(String email, WebAccount webAccount) {
		return new Account(email, webAccount.getActive(), webAccount.getRole());
	}
}
