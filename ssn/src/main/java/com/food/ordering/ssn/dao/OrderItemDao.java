package com.food.ordering.ssn.dao;

import com.food.ordering.ssn.column.OrderItemColumn;
import com.food.ordering.ssn.model.OrderItemModel;
import com.food.ordering.ssn.query.OrderItemQuery;
import com.food.ordering.ssn.utils.ErrorLog;
import com.food.ordering.ssn.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public Response<String> insertOrderItem(OrderItemModel orderItemModel, int orderId) {

        Response<String> response = new Response<>();

        try {
            MapSqlParameterSource parameter = new MapSqlParameterSource().addValue(OrderItemColumn.orderId, orderId)
                    .addValue(OrderItemColumn.itemId, orderItemModel.getItemModel().getId())
                    .addValue(OrderItemColumn.quantity, orderItemModel.getQuantity())
                    .addValue(OrderItemColumn.price, orderItemModel.getPrice());

            int result = jdbcTemplate.update(OrderItemQuery.insertOrderItem, parameter);

            if(result>0){
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
