package com.food.ordering.ssn.model;

public class UserCollegeModel {
	private UserModel userModel;
	private CollegeModel collegeModel;

	public UserCollegeModel() {}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public CollegeModel getCollegeModel() {
		return collegeModel;
	}

	public void setCollegeModel(CollegeModel collegeModel) {
		this.collegeModel = collegeModel;
	}

	@Override
	public String toString() {
		return "UserCollegeModel{" +
				"userModel=" + userModel +
				", collegeModel=" + collegeModel +
				'}';
	}
}
