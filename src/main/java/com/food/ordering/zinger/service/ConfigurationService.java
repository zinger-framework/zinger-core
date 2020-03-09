package com.food.ordering.zinger.service;

import com.food.ordering.zinger.column.UserColumn;
import com.food.ordering.zinger.dao.ConfigurationDao;
import com.food.ordering.zinger.model.ConfigurationModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
public class ConfigurationService {

    @Autowired
    ConfigurationDao configurationDao;

    public Response<String> updateConfiguration(ConfigurationModel configurationModel,String oauthId, String mobile,String role){
        return configurationDao.updateConfigurationModel(configurationModel,oauthId,mobile,role);
    }

    public Response<String> deleteConfiguration(ConfigurationModel configurationModel,String oauthId, String mobile,String role){
        return configurationDao.deleteConfiguration(configurationModel,oauthId,mobile,role);
    }
}
