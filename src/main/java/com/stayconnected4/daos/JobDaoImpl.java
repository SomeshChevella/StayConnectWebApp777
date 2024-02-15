package com.stayconnected4.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.stayconnected4.daos.ProfileDaoImpl.SkillsMapper;
import com.stayconnected4.models.Account;
import com.stayconnected4.models.Employer;
import com.stayconnected4.models.JobPost;
import com.stayconnected4.models.Location;
import com.stayconnected4.models.Profile;
import com.stayconnected4.models.Skill;


@Repository
public class JobDaoImpl implements JobDao{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	 @Autowired
	 private DataSourceTransactionManager transactionManager;
	 private DataSource dataSource;
	 private JdbcTemplate jdbcTemplate;
	 @Autowired
	 public void setDataSource(DataSource dataSource) {
	 this.dataSource = dataSource;
	 this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	 }
	 
	 
	 @Override
	 public JobPost addJobPost(JobPost jobPost) {
		 DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		 def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		 TransactionStatus status = transactionManager.getTransaction(def);
	 
		 Profile profile = new Profile(); 
	 
		 try {
			 // insert into job post table
			 String SQL = "insert into job_post (account_email, job_num, employer_name, job_type, job_title) " + " values (?, ?, ?, ?, ?)";
			 jdbcTemplate.update(SQL, profile.getEmail() ,jobPost.getJobNum() ,jobPost.getEmployer() ,jobPost.getJobType() ,jobPost.getJobTitle());
	 
			 logger.info("Created jobPost with account_email = " + jobPost.getAccount().getEmail() + " and job_num = " + jobPost.getJobNum());
	 
			 // commit the transaction
			 transactionManager.commit(status);
		 } catch (DataAccessException e) {
			 System.out.println("Error in creating customer record, rolling back");
			 transactionManager.rollback(status);
			 throw e;
	 
		 }
	 
	 return jobPost;
	 }




	@Override
	public JobPost getJobByJobNum(String email, int jobNum) {
		// TODO Auto-generated method stub
		return null;
	}

	private final String LOCATION_SQL = "SELECT id, country, city, state, zip_code, address FROM location, job_post WHERE location_id = %d AND id = location_id;";
	private final String SKILLS_SQL = "SELECT skill FROM skill_job_post WHERE account_email = '%s' AND job_num = %d;";
	private final String JOB_SEARCH_SQL = "SELECT account_email, job_num, job_title, job_type, location_id, employer_name FROM job_post WHERE ";
	private final String JOB_SEARCH_LOC_SQL = "SELECT account_email, job_num, job_title, job_type, location_id, employer_name FROM job_post WHERE location_id IN (SELECT id FROM location WHERE UPPER(city) SIMILAR TO %s OR UPPER(country) SIMILAR TO %s OR UPPER(state) SIMILAR TO %s OR UPPER(address) SIMILAR TO %s);";
	private final String JOB_SEARCH_SKILL_SQL = "SELECT account_email, job_num, job_title, job_type, location_id, employer_name FROM job_post WHERE (account_email, job_num) IN (SELECT account_email, job_num FROM skill_job_post WHERE UPPER(skill) SIMILAR TO %s);";
	private final String JOB_SEARCH_EMPLOYER_SQL = "SELECT * FROM job_post WHERE employer_name IN (SELECT name FROM employer WHERE UPPER(name) SIMILAR TO UPPER(%s));";
	
	
	@Override
	public List<JobPost> getJobsByCriteria(List<String> keywords, String criteria) {
		String sql;
		if (criteria.equals("location")) {
			sql = generateLocationSql(keywords);
		}
		else if (criteria.equals("skills")) {
			sql = generateSkillsSql(keywords);
		}
		else if (criteria.equals("employer")) {
			sql = generateEmployerSql(keywords);
		}
		else {
			sql = generateJobSql(keywords, criteria);
		}
		
		List<JobPost> searchResult = jdbcTemplate.query(sql, new JobPostMapper());
		for (JobPost job : searchResult) {
			sql = String.format(LOCATION_SQL, job.getLocation().getId());
			job.setLocation(jdbcTemplate.query(sql, new LocationMapper()).get(0));
			if (job.getLocation() == null) {
				job.setLocation(new Location());
			}
			sql = String.format(SKILLS_SQL, job.getAccount().getEmail(), job.getJobNum());
			job.setSkills(jdbcTemplate.query(sql, new SkillsMapper()));			
			if (job.getSkills() == null) {
				job.setSkills(new ArrayList<>());
			}
			
		}
		return searchResult;
	}
	
	private String generateLocationSql(List<String> keywords) {
		String sqlCompareStr = "UPPER('%" + keywords.get(0) +"%";
		for (int i=1; i<keywords.size(); i++) {
			sqlCompareStr += "|%" + keywords.get(i) + "%";
		}
		sqlCompareStr += "')";
		String sql = String.format(JOB_SEARCH_LOC_SQL, sqlCompareStr, sqlCompareStr, sqlCompareStr, sqlCompareStr);
		
		return sql;
	}
	private String generateSkillsSql(List<String> keywords) {
		String sqlCompareStr = "UPPER('%" + keywords.get(0) +"%";
		for (int i=1; i<keywords.size(); i++) {
			sqlCompareStr += "|%" + keywords.get(i) + "%";
		}
		sqlCompareStr += "')";
		String sql = String.format(JOB_SEARCH_SKILL_SQL, sqlCompareStr);
		
		return sql;
	}
	
	private String generateEmployerSql(List<String> keywords) {
		String sqlCompareStr = "'%" + keywords.get(0) +"%";
		for (int i=1; i<keywords.size(); i++) {
			sqlCompareStr += "|%" + keywords.get(i) + "%";
		}
		sqlCompareStr += "'";
		String sql = String.format(JOB_SEARCH_EMPLOYER_SQL, sqlCompareStr);
		
		return sql;
	}

	private String generateJobSql(List<String> keywords, String criteria) {
		String sql = String.format("%sUPPER(%s) SIMILAR TO UPPER('%%%s%%", JOB_SEARCH_SQL, criteria, keywords.get(0));
		for (int i=1; i<keywords.size(); i++) {
			sql += "|%" + keywords.get(i) + "%";
		}
		sql += "')";
		return sql;
	}
	
	class JobPostMapper implements RowMapper<JobPost> {

		@Override
		public JobPost mapRow(ResultSet rs, int rowNum) throws SQLException {
			JobPost jobPost = new JobPost();
			jobPost.setAccount(new Account(rs.getString("account_email")));
			jobPost.setJobNum(rs.getInt("job_num"));
			jobPost.setLocation(new Location(rs.getInt("location_id")));
			jobPost.setEmployer(new Employer(rs.getString("employer_name")));
			jobPost.setJobTitle(rs.getString("job_title"));
			jobPost.setJobType(rs.getString("job_type"));
			return jobPost;
		}
		
	}
	
	class LocationMapper implements RowMapper<Location> {
		
		public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
			Location location = new Location();
			location.setId(rs.getInt("id"));
			location.setCountry(rs.getString("country"));
			location.setCity(rs.getString("city"));
			location.setState(rs.getString("state"));
			location.setZipCode(rs.getString("zip_code"));
			location.setAddress(rs.getString("address"));
			return location;
		}
	}
	
	class SkillsMapper implements RowMapper<String> {
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("skill");
		}
	}
}
