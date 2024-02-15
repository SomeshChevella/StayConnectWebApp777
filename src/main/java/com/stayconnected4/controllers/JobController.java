package com.stayconnected4.controllers;

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

import com.stayconnected4.daos.JobDao;
import com.stayconnected4.models.JobPost;
import com.stayconnected4.validation.SearchData;
import com.stayconnected4.validation.SearchDataValidator;

@Controller
public class JobController {

	@Autowired
	private JobDao jobDao;
	
	@Autowired
	private SearchDataValidator searchValidator;
	
	@RequestMapping(value="/postjob", method=RequestMethod.GET)
	public String displaypostJob(Model model) {
		return "postJob";
	}
	

	@RequestMapping(value="/jobInfo", method=RequestMethod.GET)
	public String displayJobInfo(Model model) {	
		//model.addAttribute("webJob", new WebProfile());
		return "ProfileNew";
	}
	
	
	@RequestMapping(value="/jobInfo", method=RequestMethod.POST)
	public String processPostJob(Model model, BindingResult errors) {
		return "redirect:/jobconfirm";
	}
	
	@RequestMapping(value="/jobconfirm", method=RequestMethod.GET)
	public String displayJobConfirm(Model model) {
		return "JobConfirmation";
	}
	
	@RequestMapping(value="/job/search", method=RequestMethod.GET)
	public String displayJobSearch(Model model) {
		model.addAttribute("searchData", new SearchData());
		model.addAttribute("possibleCriteria", SearchData.POSSIBLE_JOB_CRITERIA);
		return "JobSearch";
	}
	
	@RequestMapping(value="/job/search", method=RequestMethod.POST)
	public String processJobSearch(@Valid @ModelAttribute("searchData")SearchData searchData, BindingResult errors, Model model, HttpSession session) {
		searchValidator.validate(searchData, errors);
		if (errors.hasErrors()) {
			model.addAttribute("possibleCriteria", SearchData.POSSIBLE_JOB_CRITERIA);
			return "JobSearch";
		}
		List<String> keywords = parseSearchQuery(searchData);
		String criteria = "";
		switch(searchData.getCriteria()) {
		case "type":
			criteria = "job_type";
			break;
		case "title":
			criteria = "job_title";
			break;
		case "location":
			criteria = "location";
			break;
		case "employer":
			criteria = "employer";
			break;
		case "skills":
			criteria = "skills";
			break;
		}
		List<JobPost> searchResult = jobDao.getJobsByCriteria(keywords, criteria);
		session.setAttribute("searchResult", searchResult);
		
		return "redirect:/job/search/result";
	}
	
	@RequestMapping(value="/job/search/result", method=RequestMethod.GET)
	public String displayJobSearchResult(Model model, HttpSession session) {
		List<JobPost> searchResult = (List<JobPost>)session.getAttribute("searchResult");
		if (searchResult == null) {
			return "redirect:/job/search";
		}
		model.addAttribute("jobs", searchResult);
		return "JobSearchResult";
	}
	
	private List<String> parseSearchQuery(SearchData searchData) {
		return Arrays.asList(searchData.getQuery().split(" "));
	}
}
