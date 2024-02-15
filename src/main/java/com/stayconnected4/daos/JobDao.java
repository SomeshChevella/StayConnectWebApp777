package com.stayconnected4.daos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.stayconnected4.models.JobPost;

@Component
public interface JobDao {
	public JobPost addJobPost(JobPost jobPost);
	public JobPost getJobByJobNum(String email, int jobNum);
	public List<JobPost> getJobsByCriteria(List<String> keywords, String criteria);
}
