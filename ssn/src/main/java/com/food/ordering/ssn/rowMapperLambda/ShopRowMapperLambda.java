package com.food.ordering.ssn.rowMapperLambda;

import org.springframework.jdbc.core.RowMapper;

import com.food.ordering.ssn.model.*;
import static com.food.ordering.ssn.column.ShopColumn.*;

public class ShopRowMapperLambda {
	public static final RowMapper<ShopModel> shopRowMapperLambda = (rs,rownum) -> {
		ShopModel shop = new ShopModel();
		shop.setId(rs.getInt(id));
		shop.setName(rs.getString(name));
		shop.setPhotoUrl(rs.getString(photoUrl));
		shop.setMobile(rs.getString(mobile));

		CollegeModel collegeModel = new CollegeModel();
		collegeModel.setId(rs.getInt(collegeId));
		shop.setCollegeModel(collegeModel);

		shop.setOpeningTime(rs.getTime(openingTime));
		shop.setClosingTime(rs.getTime(closingTime));
		shop.setIsDelete(rs.getInt(isDelete));
		return shop;
	};
}
