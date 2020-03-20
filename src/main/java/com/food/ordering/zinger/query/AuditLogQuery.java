package com.food.ordering.zinger.query;

import com.food.ordering.zinger.column.logger.CollegeLogColumn;
import com.food.ordering.zinger.column.logger.ShopLogColumn;
import com.food.ordering.zinger.column.logger.UserLogColumn;
import com.food.ordering.zinger.column.logger.ItemLogColumn;
import com.food.ordering.zinger.column.logger.TransactionLogColumn;
import com.food.ordering.zinger.column.logger.OrderLogColumn;
import com.food.ordering.zinger.column.logger.UserShopLogColumn;
import com.food.ordering.zinger.column.logger.UserCollegeLogColumn;
import com.food.ordering.zinger.column.logger.ConfigurationLogColumn;

public class AuditLogQuery {
	public static final String insertCollegeLog = "INSERT INTO " + CollegeLogColumn.tableName + "(" + CollegeLogColumn.id
			+ ", " + CollegeLogColumn.errorCode + ", " + CollegeLogColumn.mobile + ", " + CollegeLogColumn.message + ", " + CollegeLogColumn.updatedValue + ", " + CollegeLogColumn.priority
			+ ") VALUES(:" + CollegeLogColumn.id + ", :" + CollegeLogColumn.errorCode + " , :" + CollegeLogColumn.mobile + ", :" + CollegeLogColumn.message + ", :" + CollegeLogColumn.updatedValue + ", :"
		    + CollegeLogColumn.priority + ")";

	public static final String insertShopLog = "INSERT INTO " + ShopLogColumn.tableName + "(" + ShopLogColumn.id
			+ ", " + ShopLogColumn.errorCode + ", " + ShopLogColumn.mobile + ", " + ShopLogColumn.message + ", " + ShopLogColumn.updatedValue + ", " + ShopLogColumn.priority
			+ ") VALUES(:" + ShopLogColumn.id + ", :" + ShopLogColumn.errorCode + " , :" + ShopLogColumn.mobile + ", :" + ShopLogColumn.message + ", :" + ShopLogColumn.updatedValue + ", :"
			+ ShopLogColumn.priority + ")";

	public static final String insertUserLog = "INSERT INTO " + UserLogColumn.tableName + "(" + UserLogColumn.usersMobile
			+ ", " + UserLogColumn.errorCode + ", " + UserLogColumn.mobile + ", " + UserLogColumn.message + ", " + UserLogColumn.updatedValue + ", " + UserLogColumn.priority
			+ ") VALUES(:" + UserLogColumn.usersMobile + ", :" + UserLogColumn.errorCode + " , :" + UserLogColumn.mobile + ", :" + UserLogColumn.message + ", :" + UserLogColumn.updatedValue + ", :"
		    + UserLogColumn.priority + ")";

	public static final String insertItemLog = "INSERT INTO " + ItemLogColumn.tableName + "(" + ItemLogColumn.id
			+ ", " + ItemLogColumn.errorCode + ", " + ItemLogColumn.mobile + ", " + ItemLogColumn.message + ", " + ItemLogColumn.updatedValue + ", " + ItemLogColumn.priority
			+ ") VALUES(:" + ItemLogColumn.id + ", :" + ItemLogColumn.errorCode + " , :" + ItemLogColumn.mobile + ", :" + ItemLogColumn.message + ", :" + ItemLogColumn.updatedValue + ", :"
		    + ItemLogColumn.priority + ")";

	public static final String insertTransactionLog = "INSERT INTO " + TransactionLogColumn.tableName + "(" + TransactionLogColumn.transactionId
			+ ", " + TransactionLogColumn.errorCode + ", " + TransactionLogColumn.mobile + ", " + TransactionLogColumn.message + ", " + TransactionLogColumn.updatedValue + ", " + TransactionLogColumn.priority
			+ ") VALUES(:" + TransactionLogColumn.transactionId + ", :" + TransactionLogColumn.errorCode + " , :" + TransactionLogColumn.mobile + ", :" + TransactionLogColumn.message + ", :" + TransactionLogColumn.updatedValue + ", :"
		    + TransactionLogColumn.priority + ")";

	public static final String insertOrderLog = "INSERT INTO " + OrderLogColumn.tableName + "(" + OrderLogColumn.id
			+ ", " + OrderLogColumn.errorCode + ", " + OrderLogColumn.mobile + ", " + OrderLogColumn.message + ", " + OrderLogColumn.updatedValue + ", " + OrderLogColumn.priority
			+ ") VALUES(:" + OrderLogColumn.id + ", :" + OrderLogColumn.errorCode + " , :" + OrderLogColumn.mobile + ", :" + OrderLogColumn.message + ", :" + OrderLogColumn.updatedValue + ", :"
		    + OrderLogColumn.priority + ")";

	public static final String insertUserShopLog = "INSERT INTO " + UserShopLogColumn.tableName + "(" + UserShopLogColumn.usersMobile
			+ ", " + UserShopLogColumn.errorCode + ", " + UserShopLogColumn.mobile + ", " + UserShopLogColumn.message + ", " + UserShopLogColumn.updatedValue + ", " + UserShopLogColumn.priority
			+ ") VALUES(:" + UserShopLogColumn.usersMobile + ", :" + UserShopLogColumn.errorCode + " , :" + UserShopLogColumn.mobile + ", :" + UserShopLogColumn.message + ", :" + UserShopLogColumn.updatedValue + ", :"
	        + UserShopLogColumn.priority + ")";

	public static final String insertUserCollegeLog = "INSERT INTO " + UserCollegeLogColumn.tableName + "(" + UserCollegeLogColumn.usersMobile
			+ ", " + UserCollegeLogColumn.errorCode + ", " + UserCollegeLogColumn.mobile + ", " + UserCollegeLogColumn.message + ", " + UserCollegeLogColumn.updatedValue + ", " + UserCollegeLogColumn.priority
			+ ") VALUES(:" + UserCollegeLogColumn.usersMobile + ", :" + UserCollegeLogColumn.errorCode + " , :" + UserCollegeLogColumn.mobile + ", :" + UserCollegeLogColumn.message + ", :" + UserCollegeLogColumn.updatedValue + ", :"
		    + UserCollegeLogColumn.priority + ")";

	public static final String insertConfigurationLog = "INSERT INTO " + ConfigurationLogColumn.tableName + "(" + ConfigurationLogColumn.shopId
			+ ", " + ConfigurationLogColumn.errorCode + ", " + ConfigurationLogColumn.mobile + ", " + ConfigurationLogColumn.message + ", " + ConfigurationLogColumn.updatedValue + ", " + ConfigurationLogColumn.priority
			+ ") VALUES(:" + ConfigurationLogColumn.shopId + ", :" + ConfigurationLogColumn.errorCode + " , :" + ConfigurationLogColumn.mobile + ", :" + ConfigurationLogColumn.message + ", :" + ConfigurationLogColumn.updatedValue + ", :"
		    + ConfigurationLogColumn.priority + ")";
}
