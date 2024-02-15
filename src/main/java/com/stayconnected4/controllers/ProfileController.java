package com.stayconnected4.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.stayconnected4.daos.ProfileDao;
import com.stayconnected4.models.Employment;
import com.stayconnected4.models.JobPost;
import com.stayconnected4.models.Location;
import com.stayconnected4.models.Name;
import com.stayconnected4.models.Profile;
import com.stayconnected4.models.WebProfile;
import com.stayconnected4.validation.SearchData;
import com.stayconnected4.validation.SearchDataValidator;
import com.stayconnected4.validation.WebAccount;

@Controller
public class ProfileController {

	@Autowired
	private ProfileDao profileDao;

	@Autowired
	private SearchDataValidator searchValidator;

	@ModelAttribute("allGender")
	public List<String> getAllGenders() {
		return profileDao.getAllGenders();
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String displayProfile(WebProfile webProfile,Principal principal, Model model) {
		String email=principal.getName();
		if (profileDao.doesEmailExist(email)) {
			return "redirect:/denied";
		}
		model.addAttribute(webProfile);

		return "Profile";
	}

	@RequestMapping(value = "/profileInfo", method = RequestMethod.GET)
	public String displayProfileInfo(Model model) {
		model.addAttribute("webProfile", new WebProfile());
		return "ProfileNew";
	}

	@RequestMapping(value = "/profileInfo", method = RequestMethod.POST)
	public String processProfileInfo(@Valid @ModelAttribute("webProfile") WebProfile webProfile, BindingResult errors,
			Principal principal, Model model) {
		if (errors.hasErrors()) {
			return "ProfileNew";
		}

		Location location = new Location(webProfile.getCountry(), webProfile.getState(), webProfile.getCity(),
				webProfile.getZipCode(), webProfile.getAddress());

		Profile profile = new Profile(principal.getName(), webProfile.getGender(), webProfile.getAge(),
				webProfile.getSkillSummary(), webProfile.getPhoneNum(), location, webProfile.getSkills());

		Name name = new Name();
		name.setName(webProfile.getName());

		profileDao.addProfile(profile, name);
		return "redirect:/profileinformation/confirm";
	}

	@RequestMapping(value = "/profileinformation/confirm", method = RequestMethod.GET)
	public String displayProfileConfirm(Principal principal, Model model) {

		Profile profile = profileDao.getProfileByEmail(principal.getName());

//		Location location= profileDao.getLocationById(profile.getLocId());

		model.addAttribute("age", profile.getAge());
		model.addAttribute("account_email", profile.getEmail());
		model.addAttribute("gender", profile.getGender());
		model.addAttribute("skills_summary", profile.getSkillSummary());
		model.addAttribute("phone_num", profile.getPhoneNum());
		model.addAttribute("location_id", profile.getLocId());

		return "ProfileConfirmation";
	}

	@RequestMapping(value = "/profile/list", method = RequestMethod.GET)
	public String displayProfileList(Model model) {
		List<Profile> profiles = profileDao.getAllProfiles();
		model.addAttribute("profiles", profiles);
		return "ProfileList";
	}

	@RequestMapping(value = "/profile/search", method = RequestMethod.GET)
	public String displayJobSearch(Model model) {
		model.addAttribute("searchData", new SearchData());
		model.addAttribute("possibleCriteria", SearchData.POSSIBLE_PROFILE_CRITERIA);
		return "ProfileSearch";
	}

	@RequestMapping(value = "/profile/search", method = RequestMethod.POST)
	public String processJobSearch(@Valid @ModelAttribute("searchData") SearchData searchData, BindingResult errors,
			Model model, HttpSession session) {
		searchValidator.validate(searchData, errors);
		if (errors.hasErrors()) {
			model.addAttribute("possibleCriteria", SearchData.POSSIBLE_PROFILE_CRITERIA);
			return "ProfileSearch";
		}
		List<String> keywords = parseSearchQuery(searchData);
		String criteria = "";
		switch (searchData.getCriteria()) {
		case "email":
			criteria = "account_email";
			break;
		default:
			criteria = searchData.getCriteria();
			break;
		}
		List<Profile> searchResult = profileDao.getProfileByCriteria(keywords, criteria);
		session.setAttribute("searchResult", searchResult);

		return "redirect:/profile/search/result";
	}

	@RequestMapping(value = "/profile/search/result", method = RequestMethod.GET)
	public String displayJobSearchResult(Model model, HttpSession session) {
		List<Profile> searchResult = (List<Profile>) session.getAttribute("searchResult");
		if (searchResult == null) {
			return "redirect:/profile/search";
		}
		model.addAttribute("profiles", searchResult);
		return "ProfileSearchResult";
	}

	private List<String> parseSearchQuery(SearchData searchData) {
		return Arrays.asList(searchData.getQuery().split(" "));
	}
	
	@RequestMapping(value = "/denied", method = RequestMethod.GET)
	public String accessDenied(Model model, Principal principal) {
		String username = principal.getName();
		model.addAttribute("message", "Sorry "+ username + ", You already have profile");
		return "denied";	
	}	

	@RequestMapping(value = "/update/profile", method = RequestMethod.GET)
	public String updateProfile(Principal principal, Model model) {

		Profile profile = new Profile();
		profile = profileDao.getProfileByEmail(principal.getName());

		Name name = profileDao.getNameByEmail(principal.getName());

		Location location = profileDao.getLocationById(profile.getLocId());

		WebProfile webProfile = new WebProfile(name.getName(), profile.getGender(), profile.getAge(),
				profile.getSkillSummary(), profile.getPhoneNum(), location.getCountry(), location.getState(),
				location.getCity(), location.getZipCode(), location.getAddress(), profile.getSkills());

		String Name = webProfile.getName();
		
		
		model.addAttribute("name", webProfile.getName());
		
		System.out.println("name:"+ webProfile.getName());
		
		model.addAttribute("webProfile", new WebProfile());

		return "UpdateProfile";
	}

	@RequestMapping(value = "/update/profile", method = RequestMethod.POST)
	public String updatedProfile(@Valid @ModelAttribute("webProfile") WebProfile webProfile, BindingResult errors,
			Principal principal, Model model) {

		if (errors.hasErrors()) {
			return "UpdateProfile";
		}

		Location location = new Location(webProfile.getCountry(), webProfile.getState(), webProfile.getCity(),
				webProfile.getZipCode(), webProfile.getAddress());

		Profile profile = new Profile(principal.getName(), webProfile.getGender(), webProfile.getAge(),
				webProfile.getSkillSummary(), webProfile.getPhoneNum(), location, webProfile.getSkills());

		Name name = new Name();
		name.setName(webProfile.getName());

		profileDao.updateProfile(profile, name);

		return "redirect:/profileinformation/confirm";

	}

}
