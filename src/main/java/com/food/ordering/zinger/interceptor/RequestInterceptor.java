package com.food.ordering.zinger.interceptor;

import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.dao.interfaces.InterceptorDao;
import com.food.ordering.zinger.model.RequestHeaderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static com.food.ordering.zinger.constant.ApiConfig.*;
import static com.food.ordering.zinger.constant.ApiConfig.ItemApi.insertItem;
import static com.food.ordering.zinger.constant.ApiConfig.ItemApi.updateItemById;
import static com.food.ordering.zinger.constant.ApiConfig.ShopApi.updateShopConfiguration;
import static com.food.ordering.zinger.constant.ApiConfig.UserApi.*;
import static com.food.ordering.zinger.constant.Column.UserColumn.*;
import static com.food.ordering.zinger.constant.Enums.UserRole.*;
import static org.springframework.http.HttpMethod.*;

@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private static final int whiteListFlag = 0;
    private static final int superAdminFlag = 1;
    private static final int shopOwnerFlag = 2;
    private static final int sellerFlag = 3;
    @Autowired
    InterceptorDao interceptorDao;
    private ArrayList<Map<String, HttpMethod>> whiteListUrls;
    private ArrayList<Map<String, HttpMethod>> superAdminUrls;
    private ArrayList<Map<String, HttpMethod>> shopOwnerUrls;
    private ArrayList<Map<String, HttpMethod>> sellerUrls;

    @Bean
    public void populateUrls() {
        whiteListUrls = new ArrayList<>();
        superAdminUrls = new ArrayList<>();
        shopOwnerUrls = new ArrayList<>();
        sellerUrls = new ArrayList<>();

        whiteListUrls.add(Collections.singletonMap(UserApi.BASE_URL + loginRegisterCustomer, POST));
        whiteListUrls.add(Collections.singletonMap(UserApi.BASE_URL + verifySeller, POST));
        whiteListUrls.add(Collections.singletonMap(UserApi.BASE_URL + acceptInvite, POST));
        whiteListUrls.add(Collections.singletonMap(UserApi.BASE_URL + verifyInvite, GET));

        superAdminUrls.add(Collections.singletonMap(ShopApi.BASE_URL, POST));
        superAdminUrls.add(Collections.singletonMap(PlaceApi.BASE_URL, POST));

        shopOwnerUrls.add(Collections.singletonMap(ShopApi.BASE_URL + updateShopConfiguration, PATCH));
        shopOwnerUrls.add(Collections.singletonMap(UserApi.BASE_URL + inviteSeller, POST));
        shopOwnerUrls.add(Collections.singletonMap(UserApi.BASE_URL + deleteInvite, PATCH));
        shopOwnerUrls.add(Collections.singletonMap(UserApi.BASE_URL + "/seller/", GET));
        shopOwnerUrls.add(Collections.singletonMap(UserApi.BASE_URL + "/seller/", DELETE));

        sellerUrls.add(Collections.singletonMap(ItemApi.BASE_URL + insertItem, POST));
        sellerUrls.add(Collections.singletonMap(ItemApi.BASE_URL + updateItemById, PATCH));
        sellerUrls.add(Collections.singletonMap(ItemApi.BASE_URL + "/delete/", DELETE));
        sellerUrls.add(Collections.singletonMap(ItemApi.BASE_URL + "/undelete/", DELETE));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isPresent(request, whiteListFlag)) {
            if (request.getHeader(oauthId) == null ||
                    request.getHeader(id) == null ||
                    request.getHeader(role) == null)
                throw new InvalidException(ErrorLog.MissingRequestHeader);

            if (isPresent(request, superAdminFlag) &&
                    !request.getHeader(role).equals(SUPER_ADMIN.name()))
                throw new InvalidException(ErrorLog.UnAuthorizedAccess);

            if (isPresent(request, shopOwnerFlag) &&
                    !(request.getHeader(role).equals(SHOP_OWNER.name()) ||
                            request.getHeader(role).equals(SUPER_ADMIN.name())))
                throw new InvalidException(ErrorLog.UnAuthorizedAccess);

            if (isPresent(request, sellerFlag) &&
                    !(request.getHeader(role).equals(SELLER.name()) ||
                            request.getHeader(role).equals(DELIVERY.name()) ||
                            request.getHeader(role).equals(SHOP_OWNER.name()) ||
                            request.getHeader(role).equals(SUPER_ADMIN.name())))
                throw new InvalidException(ErrorLog.UnAuthorizedAccess);

            RequestHeaderModel requestHeaderModel = new RequestHeaderModel(request.getHeader(oauthId), Integer.parseInt(request.getHeader(id)), request.getHeader(role));
            if (!interceptorDao.validateUser(requestHeaderModel).getCode().equals(ErrorLog.CodeSuccess))
                throw new InvalidException(ErrorLog.InvalidHeader);
        }
        return super.preHandle(request, response, handler);
    }

    private Boolean isPresent(HttpServletRequest request, int flag) {
        String url = request.getRequestURI();
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        ArrayList<Map<String, HttpMethod>> mapArrayList = new ArrayList<>();

        switch (flag) {
            case whiteListFlag:
                mapArrayList = new ArrayList<>(whiteListUrls);
                break;
            case superAdminFlag:
                mapArrayList = new ArrayList<>(superAdminUrls);
                break;
            case shopOwnerFlag:
                mapArrayList = new ArrayList<>(shopOwnerUrls);
                break;
            case sellerFlag:
                mapArrayList = new ArrayList<>(sellerUrls);
                break;
        }

        for (int i = 0; i < mapArrayList.size(); i++) {
            Map<String, HttpMethod> mapper = mapArrayList.get(i);
            for (Map.Entry<String, HttpMethod> entry : mapper.entrySet()) {
                if (url.startsWith(entry.getKey()) && httpMethod.equals(entry.getValue()))
                    return true;
            }
        }

        return false;
    }
}
