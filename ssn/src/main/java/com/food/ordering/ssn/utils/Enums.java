package com.food.ordering.ssn.utils;

public interface Enums {

	public enum UserRole{
		customer("CUSTOMER"),
		seller("SELLER");
		
		private String role;
		
		UserRole(String role){
			this.role = role;
		}
		
		public String value() {
			return role;
		}
	}
}
