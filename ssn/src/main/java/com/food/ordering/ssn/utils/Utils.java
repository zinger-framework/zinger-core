package com.food.ordering.ssn.utils;

public class Utils {

	public static boolean isNullOrEmpty(Object obj){
		if(obj == null || obj == "" || obj == " ")
			return true;
		else
			return false;
	}
	
}
