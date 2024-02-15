package com.stayconnected4.daos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.stayconnected4.models.Location;
import com.stayconnected4.models.Name;
import com.stayconnected4.models.Profile;
import com.stayconnected4.models.WebProfile;

@Component
public interface ProfileDao {
	public void addProfile(Profile profile,Name name);
	public Profile getProfileByEmail(String accountEmail);
	public List<Profile> getProfileByCriteria(List<String> keywords, String criteria);
	public Name getNameByEmail(String profileEmail);
	public List<Profile> getAllProfiles();
	public List<String> getAllGenders();
	public Location getLocationById(int id);
	public void updateProfile(Profile profile,Name name);
	public boolean doesEmailExist(String email);
}