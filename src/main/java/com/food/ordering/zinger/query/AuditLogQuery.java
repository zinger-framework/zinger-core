package com.food.ordering.zinger.query;

import com.food.ordering.zinger.column.CollegeLogColumn;
import com.food.ordering.zinger.column.ShopLogColumn;
import com.food.ordering.zinger.column.UsersLogColumn;
import com.food.ordering.zinger.column.ItemLogColumn;
import com.food.ordering.zinger.column.TransactionsLogColumn;
import com.food.ordering.zinger.column.OrdersLogColumn;
import com.food.ordering.zinger.column.UsersShopLogColumn;
import com.food.ordering.zinger.column.UsersCollegeLogColumn;
import com.food.ordering.zinger.column.ConfigurationsLogColumn;


public class AuditLogQuery {
	public static final String insertCollegeLog = "INSERT INTO" + CollegeLogColumn.tableName + "(" + CollegeLogColumn.id
			+ ", " + CollegeLogColumn.errorCode + ", " + CollegeLogColumn.mobile + ", " + CollegeLogColumn.message + ", " + CollegeLogColumn.updatedValue + ", " + CollegeLogColumn.date + ", " + CollegeLogColumn.priority
			+ ") VALUES(:" + CollegeLogColumn.id + ", :" + CollegeLogColumn.errorCode + " , :" + CollegeLogColumn.mobile + ", :" + CollegeLogColumn.message + ", :" + CollegeLogColumn.updatedValue + ", :"
		    + CollegeLogColumn.priority + ")";

	public static final String insertShopLog = "INSERT INTO" + ShopLogColumn.tableName + "(" + ShopLogColumn.id
			+ ", " + ShopLogColumn.errorCode + ", " + ShopLogColumn.mobile + ", " + ShopLogColumn.message + ", " + ShopLogColumn.updatedValue + ", " + ShopLogColumn.date + ", " + ShopLogColumn.priority
			+ ") VALUES(:" + ShopLogColumn.id + ", :" + ShopLogColumn.errorCode + " , :" + ShopLogColumn.mobile + ", :" + ShopLogColumn.message + ", :" + ShopLogColumn.updatedValue + ", :"
			+ ShopLogColumn.priority + ")";

	public static final String insertUsersLog = "INSERT INTO" + UsersLogColumn.tableName + "(" + UsersLogColumn.usersMobile
			+ ", " + UsersLogColumn.errorCode + ", " + UsersLogColumn.mobile + ", " + UsersLogColumn.message + ", " + UsersLogColumn.updatedValue + ", " + UsersLogColumn.date + ", " + UsersLogColumn.priority
			+ ") VALUES(:" + UsersLogColumn.usersMobile + ", :" + UsersLogColumn.errorCode + " , :" + UsersLogColumn.mobile + ", :" + UsersLogColumn.message + ", :" + UsersLogColumn.updatedValue + ", :"
		    + UsersLogColumn.priority + ")";
	
	public static final String insertItemLog = "INSERT INTO" + ItemLogColumn.tableName + "(" + ItemLogColumn.id
			+ ", " + ItemLogColumn.errorCode + ", " + ItemLogColumn.mobile + ", " + ItemLogColumn.message + ", " + ItemLogColumn.updatedValue + ", " + ItemLogColumn.date + ", " + ItemLogColumn.priority
			+ ") VALUES(:" + ItemLogColumn.id + ", :" + ItemLogColumn.errorCode + " , :" + ItemLogColumn.mobile + ", :" + ItemLogColumn.message + ", :" + ItemLogColumn.updatedValue + ", :"
		    + ItemLogColumn.priority + ")";
	
	public static final String insertTransactionsLog = "INSERT INTO" + TransactionsLogColumn.tableName + "(" + TransactionsLogColumn.transactionId
			+ ", " + TransactionsLogColumn.errorCode + ", " + TransactionsLogColumn.mobile + ", " + TransactionsLogColumn.message + ", " + TransactionsLogColumn.updatedValue + ", " + TransactionsLogColumn.date + ", " + TransactionsLogColumn.priority
			+ ") VALUES(:" + TransactionsLogColumn.transactionId + ", :" + TransactionsLogColumn.errorCode + " , :" + TransactionsLogColumn.mobile + ", :" + TransactionsLogColumn.message + ", :" + TransactionsLogColumn.updatedValue + ", :"
		    + TransactionsLogColumn.priority + ")";
	
	public static final String insertOrdersLog = "INSERT INTO" + OrdersLogColumn.tableName + "(" + OrdersLogColumn.id
			+ ", " + OrdersLogColumn.errorCode + ", " + OrdersLogColumn.mobile + ", " + OrdersLogColumn.message + ", " + OrdersLogColumn.updatedValue + ", " + OrdersLogColumn.date + ", " + OrdersLogColumn.priority
			+ ") VALUES(:" + OrdersLogColumn.id + ", :" + OrdersLogColumn.errorCode + " , :" + OrdersLogColumn.mobile + ", :" + OrdersLogColumn.message + ", :" + OrdersLogColumn.updatedValue + ", :"
		    + OrdersLogColumn.priority + ")";
	
	public static final String insertUsersShopLog = "INSERT INTO" + UsersShopLogColumn.tableName + "(" +UsersShopLogColumn.usersMobile
			+ ", " + UsersShopLogColumn.errorCode + ", " + UsersShopLogColumn.mobile + ", " + UsersShopLogColumn.message + ", " + UsersShopLogColumn.updatedValue + ", " + UsersShopLogColumn.date + ", " + UsersShopLogColumn.priority
			+ ") VALUES(:" + UsersShopLogColumn.usersMobile + ", :" + UsersShopLogColumn.errorCode + " , :" + UsersShopLogColumn.mobile + ", :" + UsersShopLogColumn.message + ", :" + UsersShopLogColumn.updatedValue + ", :"
	        + UsersShopLogColumn.priority + ")";
	
	public static final String insertUsersCollegeLog = "INSERT INTO" + UsersCollegeLogColumn.tableName + "(" +UsersCollegeLogColumn.usersMobile
			+ ", " + UsersCollegeLogColumn.errorCode + ", " + UsersCollegeLogColumn.mobile + ", " + UsersCollegeLogColumn.message + ", " + UsersCollegeLogColumn.updatedValue + ", " + UsersCollegeLogColumn.date + ", " + UsersCollegeLogColumn.priority
			+ ") VALUES(:" + UsersCollegeLogColumn.usersMobile + ", :" + UsersCollegeLogColumn.errorCode + " , :" + UsersCollegeLogColumn.mobile + ", :" + UsersCollegeLogColumn.message + ", :" + UsersCollegeLogColumn.updatedValue + ", :"
		    + UsersCollegeLogColumn.priority + ")";

	public static final String insertConfigurationsLog = "INSERT INTO" + ConfigurationsLogColumn.tableName + "(" + ConfigurationsLogColumn.shopId
			+ ", " + ConfigurationsLogColumn.errorCode + ", " + ConfigurationsLogColumn.mobile + ", " + ConfigurationsLogColumn.message + ", " + ConfigurationsLogColumn.updatedValue + ", " + ConfigurationsLogColumn.date + ", " + ConfigurationsLogColumn.priority
			+ ") VALUES(:" + ConfigurationsLogColumn.shopId + ", :" + ConfigurationsLogColumn.errorCode + " , :" + ConfigurationsLogColumn.mobile + ", :" + ConfigurationsLogColumn.message + ", :" + ConfigurationsLogColumn.updatedValue + ", :"
		    + ConfigurationsLogColumn.priority + ")";
}