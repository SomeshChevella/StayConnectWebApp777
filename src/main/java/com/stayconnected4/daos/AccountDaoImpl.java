package com.stayconnected4.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.stayconnected4.models.Account;

@Repository
public class AccountDaoImpl implements AccountDao {
	@Autowired
	private DataSourceTransactionManager transactionManager;
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private final String NEXT_ID_GET_SQL = "SELECT NEXTVAL(PG_GET_SERIAL_SEQUENCE('user_account', 'id')) AS id;";
	private final String ACCOUNT_GET_QUERY = "SELECT * FROM user_account";
	private final String ACCOUNT_ADD_QUERY = "INSERT INTO user_account(id, email, password, active) VALUES ";
	private final String ACCOUNT_UPDATE_QUERY = "UPDATE user_account SET active = ? WHERE email = ?;";
	private final String AUTHORITY_ADD_QUERY = "INSERT INTO user_authority(email, authority) VALUES ";
	private final String AUTHORITY_GET_QUERY = "SELECT * FROM user_authority WHERE email = ?";
	private final String AUTHORITY_DELETE_QUERY = "DELETE FROM user_authority WHERE email = '%s';";
	private final String AUTHORITIES_GET_QUERY = "SELECT * FROM authority";
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	@Override
	public Account getAccountById(int id) {
		String sql = String.format("%s WHERE id = %d;", ACCOUNT_GET_QUERY, id);
		Account account = jdbcTemplate.query(sql, new AccountMapper()).get(0);
		account.setRoles(jdbcTemplate.query(AUTHORITY_GET_QUERY, new AuthoritySetter(account.getEmail()), new AuthorityMapper()));
		return account;
	}
	
	@Override
	public Account getAccountByEmail(String email) {
		String sql = String.format("%s WHERE email = '%s';", ACCOUNT_GET_QUERY, email);
		Account account = jdbcTemplate.query(sql, new AccountMapper()).get(0);
		account.setRoles(jdbcTemplate.query(AUTHORITY_GET_QUERY, new AuthoritySetter(email), new AuthorityMapper()));
		return account;
	}

	@Override
	public List<Account> getAllAccounts() {
		List<Account> accounts = jdbcTemplate.query(ACCOUNT_GET_QUERY, new AccountMapper());
		for (Account account: accounts) {
			account.setRoles(jdbcTemplate.query(AUTHORITY_GET_QUERY, new AuthoritySetter(account.getEmail()), new AuthorityMapper()));
		}
		return accounts;
	}
	
	@Override
	public void addUserAccount(Account account) {
		int id = jdbcTemplate.query(NEXT_ID_GET_SQL, new IdMapper()).get(0); 
		String accountSql = String.format("%s (?, ?, ?, ?);", ACCOUNT_ADD_QUERY);
		String roleSql = generateRoleSql(account);
		boolean accountActive = account.getRoles().get(0).equals("ROLE_FACULTY");
		
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(definition);
		try {
			jdbcTemplate.update(accountSql, id, account.getEmail(), account.getPassword(), accountActive);
			jdbcTemplate.update(roleSql);
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in creating user account, rolling back...");
			transactionManager.rollback(status);
			throw e;
		}
	}
	
	@Override
	public void updateAccount(Account account) {
		
		List<String> rolesList = jdbcTemplate.query(AUTHORITY_GET_QUERY, new AuthoritySetter(account.getEmail()), new AuthorityMapper());
		boolean roleNeedsUpdate;
		boolean hasAuthority = true;
		String originalRole = "";
		try {
			originalRole = rolesList.get(0);
			roleNeedsUpdate = account.getRoles().size() == 0 || !account.getRoles().get(0).equals(originalRole);
		}
		catch (Exception e) {
			roleNeedsUpdate = true;
			hasAuthority = false;
		}
		String roleAddSql = generateRoleSql(account);
		
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(definition);
		try {
			jdbcTemplate.update(ACCOUNT_UPDATE_QUERY, account.getActive(), account.getEmail());
			
			if (roleNeedsUpdate) {
				if (hasAuthority) {
					String deleteSql = String.format(AUTHORITY_DELETE_QUERY, account.getEmail());
					System.out.println(deleteSql);
					jdbcTemplate.execute(deleteSql);
				}
				jdbcTemplate.update(roleAddSql);
			}
			
			transactionManager.commit(status);
		}
		catch (DataAccessException e) {
			System.out.println("Error in updating account, rolling back...");
			transactionManager.rollback(status);
			throw e;
		}
	}
	
	@Override
	public List<String> getAvailableRoles() {
		return jdbcTemplate.query(AUTHORITIES_GET_QUERY, new AuthorityMapper());
	}
	
	private String generateRoleSql(Account account) {
		String sql = String.format("%s ('%s', '%s')", 
				AUTHORITY_ADD_QUERY, account.getEmail(), account.getRoles().get(0));
		for (int i=1; i<account.getRoles().size(); i++) {
			sql += String.format(", ('%s', '%s')", account.getEmail(), account.getRoles().get(i));
		}
		sql += ";";
		System.out.println(sql);
		return sql;
	}
	
	class AccountMapper implements RowMapper<Account> {
		public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
			Account account = new Account();
			account.setId(rs.getInt("id"));
			account.setEmail(rs.getString("email"));
			account.setActive(rs.getBoolean("active"));
			account.setPassword(rs.getString("password"));
			return account;
		}
	}
	
	class AuthorityMapper implements RowMapper<String> {
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("authority");
		}
	}
	
	class IdMapper implements RowMapper<Integer> {
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt("id");
		}
	}
	
	class AuthoritySetter implements PreparedStatementSetter {
		String email;
		AuthoritySetter(String email) {
			this.email = email;
		}
		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			ps.setString(1, email);
			
		}
		
	}
}
