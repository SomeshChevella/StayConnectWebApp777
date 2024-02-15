package com.stayconnected4.daos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.stayconnected4.models.Account;

@Component
public interface AccountDao {
	public Account getAccountById(int id);
	public Account getAccountByEmail(String email);
	public List<Account> getAllAccounts();
	public void addUserAccount(Account account);
	public void updateAccount(Account account);
	public List<String> getAvailableRoles();
}
