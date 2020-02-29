package com.food.ordering.ssn.model;

import java.sql.Time;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ShopModel {
	private Integer ID;
	
	private String name;
	
	private String photoUrl;
	
	private String mobile;
	
	private Integer collegeId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern= "HH:mm:ss")
	private Time openingTime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern= "HH:mm:ss")
	private Time closingTime;

	private Integer isDelete;
	
	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(Integer collegeId) {
		this.collegeId = collegeId;
	}

	public Time getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(Time openingTime) {
		this.openingTime = openingTime;
	}

	public Time getClosingTime() {
		return closingTime;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public void setClosingTime(Time closingTime) {
		this.closingTime = closingTime;
	}
	
	
}
