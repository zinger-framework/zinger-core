package com.food.ordering.ssn.model;

import java.util.List;

public class UserCollege {
	
	UserModel user;
	
	List<CollegeModel> colleges;

	public UserModel getUser() {
		return user;
	}

	public UserCollege(UserModel user, List<CollegeModel> colleges) {
		super();
		this.user = user;
		this.colleges = colleges;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public List<CollegeModel> getColleges() {
		return colleges;
	}

	public void setColleges(List<CollegeModel> colleges) {
		this.colleges = colleges;
	}
	
	
	
}
