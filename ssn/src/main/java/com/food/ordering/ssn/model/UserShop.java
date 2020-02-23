package com.food.ordering.ssn.model;

import java.util.List;

public class UserShop {
	
	public UserShop(UserModel user, List<ShopModel> shops) {
		super();
		this.user = user;
		this.shops = shops;
	}

	UserModel user;
	
	List<ShopModel> shops;

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public List<ShopModel> getShops() {
		return shops;
	}

	public void setShops(List<ShopModel> shops) {
		this.shops = shops;
	}
}
