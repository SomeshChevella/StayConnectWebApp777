package com.stayconnected4.daos;

import java.lang.System.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import com.stayconnected4.daos.JobDaoImpl.JobPostMapper;
import com.stayconnected4.daos.JobDaoImpl.LocationMapper;
import com.stayconnected4.daos.JobDaoImpl.SkillsMapper;
import com.stayconnected4.models.JobPost;
import com.stayconnected4.models.Location;
import com.stayconnected4.models.Name;
import com.stayconnected4.models.Profile;
import com.stayconnected4.models.Skill;
import com.stayconnected4.models.Profile;

@Repository
public class ProfileDaoImpl implements ProfileDao {
	
	@Autowired
	private DataSourceTransactionManager transactionManager;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	public boolean doesEmailExist(String email) {
		boolean result=true;
		String SQL= "select * from profile where account_email=?";
		Profile profile = jdbcTemplate.queryForObject(SQL, new Object[] {email }, new ProfileMapper());
		
		if(profile.getEmail()!=null) {
			result= false;
		}
		return result;
		
	}
	
	@Override
	public void addProfile(Profile profile,Name name) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		Location loc = profile.getLocation();

		try {
			String locIdSQL = "query from before";
			
			
			//gets next available id for location
			String SQL1= "SELECT NEXTVAL(PG_GET_SERIAL_SEQUENCE('location', 'id')) AS id;";
			int id = jdbcTemplate.query(SQL1, new IdMapper()).get(0);
			
			String SQL2 = "INSERT INTO location(id, country, state, city, zip_code, address) VALUES (?, ?, ?, ?, ?, ?)";
			jdbcTemplate.update(SQL2, id, loc.getCountry(),
					loc.getState(),loc.getCity(),loc.getZipCode(),loc.getAddress());

			
			String SQL3 = "insert into profile (account_email,gender,age,skills_summary,"
					+ "phone_num,location_id)" + "values(?,?,?,?,?,?)";
			jdbcTemplate.update(SQL3, profile.getEmail(),profile.getGender(),profile.getAge(),
					profile.getSkillSummary(),profile.getPhoneNum(),id);
			
			String SQL4 = "insert into name (profile_email,name)" + "values(?,?)";	
			jdbcTemplate.update(SQL4,profile.getEmail(),name.getName());
		
			transactionManager.commit(status);
		} catch (DataAccessException e) {
			System.out.println("Error in creating customer record, rolling back");
			transactionManager.rollback(status);
			throw e;
		}

	}
	
	@Override
	public Profile getProfileByEmail(String email) {
		
		String SQL = "select * from profile where account_email =?";
		Profile profile = jdbcTemplate.queryForObject(SQL, new Object[] {email }, new ProfileMapper());
		
		return profile;	
	}
	
	@Override
	public Name getNameByEmail(String profileEmail) {
		String SQL2 = "select * from name where profile_email =?";
		Name nameInfo = jdbcTemplate.queryForObject(SQL2, new Object[] { profileEmail }, new NameInfoMapper());
		return nameInfo;
		
	}
	
	@Override
	public Location getLocationById(int id) {
		String SQL="select * from location where id= ?";
		Location location = jdbcTemplate.queryForObject(SQL,  new Object[] {id}, new LocationMapper());
		return location;
	}
	
	@Override
	public void updateProfile(Profile profile,Name name) {
		
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		Location loc= getLocationById(profile.getLocId());
		try {
			String SQL ="update profile(account_email,gender,age,skills_summary, phone_num)" + "values(?,?,?,?,?)";
			jdbcTemplate.update(SQL, profile.getEmail(),profile.getGender(),profile.getAge(),
					profile.getSkillSummary(),profile.getPhoneNum());
			
			String SQL2 = "update location(country, state, city, zip_code, address) VALUES (?, ?, ?, ?, ?)";
			jdbcTemplate.update(SQL2,loc.getCountry(),
					loc.getState(),loc.getCity(),loc.getZipCode(),loc.getAddress());
			
			String SQL4 = "update name (profile_email,name)" + "values(?,?)";	
			jdbcTemplate.update(SQL4,profile.getEmail(),name);
			
			transactionManager.commit(status);
		} catch (DataAccessException e) {
			System.out.println("Error in creating customer record, rolling back");
			transactionManager.rollback(status);
			throw e;
		}
	}
		

	private final String PROFILE_SELECT_SQL = "SELECT * FROM profile";
	private final String PROFILE_SEARCH_SQL = PROFILE_SELECT_SQL + " WHERE UPPER(%s) SIMILAR TO %s;";
	private final String PROFILE_SEARCH_LOC_SQL = PROFILE_SELECT_SQL + " WHERE location_id IN (SELECT id FROM location WHERE UPPER(city) SIMILAR TO %s OR UPPER(country) SIMILAR TO %s OR UPPER(state) SIMILAR TO %s OR UPPER(address) SIMILAR TO %s);";
	private final String PROFILE_SEARCH_SKILL_SQL = PROFILE_SELECT_SQL + " WHERE account_email IN (SELECT account_email, skill, proficiency FROM skill_job_post WHERE UPPER(skill) SIMILAR TO %s);";
	private final String PROFILE_SEARCH_NAME_SQL = PROFILE_SELECT_SQL + ", name WHERE profile_email = account_email AND UPPER(name) SIMILAR TO %s;";
	
	private final String LOCATION_SQL = "SELECT * FROM location, profile WHERE location_id = %d AND id = location_id;";
	private final String SKILLS_SQL = "SELECT * FROM skill_profile WHERE profile_email = '%s';";
	private final String NAME_SQL = "SELECT * FROM name WHERE profile_email = '%s';";
	@Override
	public List<Profile> getProfileByCriteria(List<String> keywords, String criteria) {
		String sql;
		if (criteria.equals("location")) {
			sql = generateLocationSql(keywords);
		}
		else if (criteria.equals("skills")) {
			sql = generateSkillsSql(keywords);
		}
		else if (criteria.equals("name")) {
			sql = generateNameSql(keywords);
		}
		else {
			sql = generateProfileSql(keywords, criteria);
		}
		System.out.println(sql);
		List<Profile> searchResult = jdbcTemplate.query(sql, new ProfileMapper());
		for (Profile profile : searchResult) {
			if (profile.getLocId() != 0) {
				sql = String.format(LOCATION_SQL, profile.getLocId());
				profile.setLocation(jdbcTemplate.query(sql, new LocationMapper()).get(0));
			}
			else {
				profile.setLocation(new Location());
			}
			sql = String.format(SKILLS_SQL, profile.getEmail());
			profile.setSkillProficiencies(jdbcTemplate.query(sql, new SkillsMapper()));
			if (profile.getSkillProficiencies().size() == 0 || profile.getSkillProficiencies().get(0) == null) {
				profile.setSkillProficiencies(new ArrayList<>());
			}
			sql = String.format(NAME_SQL, profile.getEmail());
			List<Name> names = jdbcTemplate.query(sql, new NameMapper());
			if (names.size() == 0 || names.get(0) == null) {
				names.remove(0);
				names.add(new Name(profile.getEmail(), "Unknown"));
			}
			profile.setNameInfo(names.get(0));
		}
		return searchResult;
	}
	
	
	@Override
	public List<Profile> getAllProfiles() {
		String sql = "SELECT * FROM profile;";
		List<Profile> profiles = jdbcTemplate.query(sql, new ProfileMapper());
		for (Profile profile: profiles) {
			if (profile.getLocId() != 0) {
				sql = String.format(LOCATION_SQL, profile.getLocId());
				profile.setLocation(jdbcTemplate.query(sql, new LocationMapper()).get(0));
			}
			else {
				profile.setLocation(new Location());
			}
			sql = String.format(SKILLS_SQL, profile.getEmail());
			profile.setSkillProficiencies(jdbcTemplate.query(sql, new SkillsMapper()));
			if (profile.getSkillProficiencies() == null || profile.getSkillProficiencies().get(0) == null) {
				profile.setSkillProficiencies(new ArrayList<>());
			}
			sql = String.format(NAME_SQL, profile.getEmail());
			List<Name> names = jdbcTemplate.query(sql, new NameMapper());
			if (names.size() == 0 || names.get(0) == null) {
				names.remove(0);
				names.add(new Name(profile.getEmail(), "Unknown"));
			}
			profile.setNameInfo(names.get(0));
		}
		return profiles;
	}
	
	private String generateLocationSql(List<String> keywords) {
		String sqlCompareStr = "UPPER('%" + keywords.get(0) +"%";
		for (int i=1; i<keywords.size(); i++) {
			sqlCompareStr += "|%" + keywords.get(i) + "%";
		}
		sqlCompareStr += "')";
		String sql = String.format(PROFILE_SEARCH_LOC_SQL, sqlCompareStr, sqlCompareStr, sqlCompareStr, sqlCompareStr);
		return sql;
	}
	private String generateSkillsSql(List<String> keywords) {
		String sqlCompareStr = "UPPER('%" + keywords.get(0) +"%";
		for (int i=1; i<keywords.size(); i++) {
			sqlCompareStr += "|%" + keywords.get(i) + "%";
		}
		sqlCompareStr += "')";
		String sql = String.format(PROFILE_SEARCH_SKILL_SQL, sqlCompareStr);
		
		return sql;
	}
	private String generateNameSql(List<String> keywords) {
		String sqlCompareStr = "UPPER('%" + keywords.get(0) +"%";
		for (int i=1; i<keywords.size(); i++) {
			sqlCompareStr += "|%" + keywords.get(i) + "%";
		}
		sqlCompareStr += "')";
		String sql = String.format(PROFILE_SEARCH_NAME_SQL, sqlCompareStr);
		
		return sql;
	}
	private String generateProfileSql(List<String> keywords, String criteria) {
		String sqlCompareStr = "UPPER('%" + keywords.get(0) +"%";
		for (int i=1; i<keywords.size(); i++) {
			sqlCompareStr += "|%" + keywords.get(i) + "%";
		}
		sqlCompareStr += "')";
		String sql = String.format(PROFILE_SEARCH_SQL, criteria, sqlCompareStr);
		
		return sql;
	}
	
	class ProfileMapper implements RowMapper<Profile> {
		public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {
			Profile profile = new Profile();
			
			profile.setEmail(rs.getString("account_email"));
			profile.setGender(rs.getString("gender"));
			profile.setAge(rs.getInt("age"));
			profile.setSkillSummary(rs.getString("skills_summary"));
			profile.setPhoneNum(rs.getString("phone_num"));
			profile.setLocId(rs.getInt("location_id"));
			
			return profile;
		}
	}
	
	
	class NameInfoMapper implements RowMapper<Name> {
		public Name mapRow(ResultSet rs, int rowNum) throws SQLException {
			Name name = new Name();
			name.setProfileEmail(rs.getString("profile_email"));
			name.setName(rs.getString("name"));
			name.setStartTime(rs.getTimestamp("start_time"));
			name.setEndTime(rs.getTimestamp("end_time"));
	
			return name;
		}
	}
	
	class IdMapper implements RowMapper<Integer> {
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt("id");
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
	
	class SkillsMapper implements RowMapper<Skill> {
		public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Skill(rs.getString("skill"), rs.getInt("proficiency"));
		}
	}
	
	class NameMapper implements RowMapper<Name> {
		public Name mapRow(ResultSet rs, int rowNum) throws SQLException {
			Name name = new Name();
			name.setProfileEmail(rs.getString("profile_email"));
			name.setName(rs.getString("name"));
			name.setStartTime(rs.getTimestamp("start_time"));
			name.setEndTime(rs.getTimestamp("end_time"));
			return name;
		}
	}
	
	public List<String> getAllGenders() {
		List<String> data = jdbcTemplate.queryForList("select *  from gender", String.class);
		return data;
	}
	
	
}

