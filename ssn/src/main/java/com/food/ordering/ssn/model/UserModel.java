package com.food.ordering.ssn.model;

public class UserModel {
	private String oauthId;
	private String name;
	private String email;
	private String mobile;
	private String role;
	private Integer isDelete;
		
	public UserModel() {}
	
	public String getOauthId() {
		return oauthId;
	}

	public void setOauthId(String oauthId) {
		this.oauthId = oauthId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	

	@Override
	public String toString() {
		return "UserModel [oauthId="+ oauthId + ", name=" + name + ", email=" + email + ", mobile=" + mobile + ", role=" + role + ", isDelete=" + isDelete + "]";
	}
	
	
}
