package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.model.ShopModel;
import com.food.ordering.zinger.utils.ErrorLog;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationDao {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    UtilsDao utilsDao;

    //TODO: Finish configuration
    public Response<ConfigurationModel> getConfiguration(ShopModel shopModel) {
        Response<ConfigurationModel> response = new Response<>();
        response.setCode(ErrorLog.CodeSuccess);
        response.setMessage(ErrorLog.Success);

        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setDeliveryPrice(5.0);
        response.setData(configurationModel);

        return response;
    }
}
