package com.food.ordering.zinger.constant;

public class ErrorLog {
    public static final Integer CodeFailure = 0;
    public static final Integer CodeSuccess = 1;
    public static final Integer CodeEmpty = 2;

    public static final String MissingRequestHeader = "Missing Request Header";
    public static final String InvalidHeader = "Invalid Header Values";
    public static final String UnAuthorizedAccess = "Unauthorized Access";

    public static final String Success = "Success";
    public static final String Failure = "Failure";

    public static final String UserDetailNotAvailable = "User detail not available";
    public static final String UserHasBeenBlocked = "User has been blocked";
    public static final String PlaceDetailNotAvailable = "Place detail not available";
    public static final String ShopDetailNotAvailable = "No shops found in this place";
    public static final String OrderDetailNotAvailable = "Order detail not available";
    public static final String ItemsNotAvailable = "Some Items are not available";
    public static final String DeliveryOptionNotAvailable = "Delivery Option not available";

    public static final String UserDetailNotUpdated = "Profile not updated";
    public static final String ConfigurationDetailNotUpdated = "Configuration detail not updated";
    public static final String ShopDetailNotUpdated = "Shop detail not updated";
    public static final String PlaceDetailNotUpdated = "Place detail not updated";
    public static final String TransactionDetailNotUpdated = "Transaction detail not updated";
    public static final String OrderDetailNotUpdated = "Unable to place order";
    public static final String OrderItemDetailNotUpdated = "Unable to place order";
    public static final String ItemDetailNotUpdated = "Unable to add or update current item";

    public static final String UnableToDeleteSeller = "Unable to delete seller";
    public static final String UnableToDeleteInvite = "Unable to delete invite";
    public static final String ShopDetailNotDeleted = "Unable to delete shop";

    public static final String InvalidOrderStatus = "Invalid Order status";
    public static final String SecretKeyMismatch = "Invalid secret key";
    public static final String OrderPriceMismatch = "Order price mismatch";
    public static final String RestaurantNotAcceptingOrders = "Restaurant not accepting orders";
    public static final String InviteExpired = "Invite Expired";
    public static final String TransactionTokenNotAvailable = "Transaction Token Not Available";

    /**********************************************************************/
    /* Place Controller Error Code 1100-1150
     *
     *   CE -> Catch Exception
     * */

    public static final Integer CDNU1100 = 1100;
    public static final Integer CE1101 = 1101;
    public static final Integer CDNA1102 = 1102;
    public static final Integer CE1103 = 1103;
    public static final Integer CE1104 = 1104;
    public static final Integer CE1105 = 1105;
    public static final Integer CE1106 = 1106;
    public static final Integer CE1107 = 1107;
    public static final Integer CE1108 = 1108;
    public static final Integer CE1109 = 1109;

    /**********************************************************************/
    // User Controller Error Code 1151-1200

    public static final Integer UDNU1151 = 1151;
    public static final Integer CE1152 = 1152;
    public static final Integer UDNU1153 = 1153;
    public static final Integer CE1154 = 1154;
    public static final Integer UDNA1155 = 1155;
    public static final Integer ODNU1156 = 1156;
    public static final Integer UDNU1157 = 1157;
    public static final Integer UDNU1158 = 1158;
    public static final Integer UDNU1159 = 1159;
    public static final Integer UDNU1160 = 1160;
    public static final Integer CE1161 = 1161;
    public static final Integer UDND1162 = 1162;
    public static final Integer PDNA1163 = 1163;
    public static final Integer UDND1164 = 1164;
    public static final Integer UDNU1165 = 1165;
    public static final Integer IE1166 = 1166;
    public static final Integer ODNA1167 = 1167;
    public static final Integer SDNA1168 = 1168;

    /**********************************************************************/
    // Item Controller Error Code 1200-1250

    public static final Integer IDNU1201 = 1201;
    public static final Integer CE1202 = 1202;
    public static final Integer IDNA1203 = 1203;
    public static final Integer CE1204 = 1204;
    public static final Integer IDNA1205 = 1205;
    public static final Integer CE1206 = 1206;
    public static final Integer IDNU1207 = 1207;
    public static final Integer CE1208 = 1208;
    public static final Integer CE1209 = 1209;
    public static final Integer IDNU1210 = 1210;
    public static final Integer IDNU1211 = 1211;
    public static final Integer CE1212 = 1212;
    public static final Integer CE1213 = 1213;
    public static final Integer SDNU1214 = 1214;
    public static final Integer SDNU1215 = 1215;

    /**********************************************************************/
    // Shop Controller Error Code 1251-1260

    public static final Integer SDNU1251 = 1251;
    public static final Integer CDNU1252 = 1252;
    public static final Integer CE1253 = 1253;
    public static final Integer CE1254 = 1254;
    public static final Integer CE1255 = 1255;
    public static final Integer SDNA1256 = 1256;
    public static final Integer SDND1257 = 1257;
    public static final Integer CE1258 = 1258;
    public static final Integer CE1259 = 1259;
    public static final Integer CDNU1260 = 1260;

    /**********************************************************************/
    // Order Controller Error Code 1261-1300

    public static final Integer CE1261 = 1261;
    public static final Integer UDNA1262 = 1262;
    public static final Integer DONA1263 = 1263;
    public static final Integer TDNU1264 = 1264;
    public static final Integer SDNA1265 = 1265;
    public static final Integer RNAOC1266 = 1266;
    public static final Integer CE1267 = 1267;
    public static final Integer CE1268 = 1268;
    public static final Integer CE1269 = 1269;
    public static final Integer CE1270 = 1270;
    public static final Integer TTNA1271 = 1271;
    public static final Integer SDNA1272 = 1272;
    public static final Integer OIDNA1273 = 1273;
    public static final Integer CE1274 = 1274;
    public static final Integer ODNU1275 = 1275;
    public static final Integer ODNA1276 = 1276;
    public static final Integer UDNA1277 = 1277;
    public static final Integer CE1278 = 1278;
    public static final Integer CE1279 = 1279;
    public static final Integer ODNU1280 = 1280;
    public static final Integer SKM1281 = 1281;
    public static final Integer IOS1282 = 1282;
    public static final Integer CE1283 = 1283;
    public static final Integer CE1284 = 1284;
    public static final Integer ODNU1285 = 1285;
    public static final Integer CE1286 = 1286;
    public static final Integer ODNA1287 = 1287;
    public static final Integer CE1289 = 1289;
    public static final Integer UDNA1290 = 1290;
    public static final Integer ODNA1291 = 1291;
    public static final Integer TDNA1292 = 1292;
    public static final Integer UDNA1293 = 1293;
    public static final Integer SDNA1294 = 1294;
    public static final Integer ODNU1295 = 1295;
    public static final Integer INA1296 = 1296;
    public static final Integer OIDNA1297 = 1297;
    public static final Integer OIDNA1298 = 1298;
    public static final Integer ODNA1299 = 1299;
    public static final Integer OPM1300 = 1300;
    public static final Integer OIDNU301 = 1301;
}
