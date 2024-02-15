package com.stayconnected4.models;

public class Skill {
	
	private String skill;
	private int proficiency;
	
	public Skill() {}
	
	public Skill(String skill, int proficiency) {
		super();
		this.skill = skill;
		this.proficiency = proficiency;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public int getProficiency() {
		return proficiency;
	}

	public void setProficiency(int proficiency) {
		this.proficiency = proficiency;
	}
	
	
}
