package com.food.ordering.zinger.service.impl;

import com.food.ordering.zinger.dao.interfaces.AuditLogDao;
import com.food.ordering.zinger.dao.interfaces.ItemDao;
import com.food.ordering.zinger.model.ItemModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.logger.ItemLogModel;
import com.food.ordering.zinger.service.interfaces.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.food.ordering.zinger.constant.ErrorLog.IDNU1207;
import static com.food.ordering.zinger.constant.ErrorLog.ItemDetailNotUpdated;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemDao itemDao;

    @Autowired
    AuditLogDao auditLogDao;

    @Override
    public Response<String> insertItem(List<ItemModel> itemModelList) {
        Response<String> response = new Response<>();
        if (itemModelList != null && !itemModelList.isEmpty())
            response = itemDao.insertItem(itemModelList);
        auditLogDao.insertItemLog(new ItemLogModel(response, null, itemModelList.toString()));
        return response;
    }

    @Override
    public Response<List<ItemModel>> getItemsByShopId(Integer shopId) {
        Response<List<ItemModel>> response = itemDao.getItemsByShopId(shopId);
        auditLogDao.insertItemLog(new ItemLogModel(response, shopId, shopId.toString()));
        return response;
    }

    @Override
    public Response<List<ItemModel>> getItemsByName(Integer placeId, String itemName) {
        Response<List<ItemModel>> response = itemDao.getItemsByName(placeId, itemName);
        auditLogDao.insertItemLog(new ItemLogModel(response, null, itemName));
        return response;
    }

    @Override
    public Response<String> updateItem(List<ItemModel> itemModelList) {
        Response<String> response = new Response<>();
        try {
            response = itemDao.updateItem(itemModelList);
        }
        catch (Exception e){
            response.setCode(IDNU1207);
            response.setMessage(ItemDetailNotUpdated);
        }
        auditLogDao.insertItemLog(new ItemLogModel(response, null, itemModelList.toString()));
        return response;
    }

    @Override
    public Response<String> deleteItemById(Integer itemId) {
        Response<String> response = itemDao.deleteItemById(itemId);
        auditLogDao.insertItemLog(new ItemLogModel(response, itemId, null));
        return response;
    }
}
