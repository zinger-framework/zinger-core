package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.model.IntegerAuditModel;
import com.food.ordering.zinger.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Response<String> auditCollegeLog(IntegerAuditModel auditModel){
        Response<String> response = new Response<>();
        return response;
    }
}
