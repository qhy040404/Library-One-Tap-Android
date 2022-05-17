package com.qhy040404.libraryonetap.constant

object Constants {
    const val BUGLY_APPID = "e9cf71d653"

    const val GLOBAL_ERROR = "Error"

    const val PREF_ID = "userid"
    const val PREF_PASSWD = "passwd"

    const val PREF_DARK = "dark"
    const val PREF_THEME = "theme"
    const val PREF_LOCALE = "locale"

    const val PREF_UPDATE = "update"

    const val DEFAULT_DARK = "system"
    const val DEFAULT_THEME = "simple"
    const val DEFAULT_LOCALE = "system"

    const val CONTENT_TYPE_SSO = "application/x-www-form-urlencoded; charset=utf-8"
    const val CONTENT_TYPE_JSON = "application/json;charset=UTF-8"
    const val CONTENT_TYPE_VCARD = "application/x-www-form-urlencoded"

    const val SHORTCUT_DETAIL = "com.qhy040404.libraryonetap.intent.action.DETAIL"
    const val SHORTCUT_TOOLS = "com.qhy040404.libraryonetap.intent.action.TOOLS"
    const val SHORTCUT_VCARD = "com.qhy040404.libraryonetap.intent.action.VCARD"

    const val LIBRARY_SSO_URL = "https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php"
    const val LIBRARY_SESSION_URL = "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=checkSession"
    const val LIBRARY_ORDER_LIST_URL = "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=myOrderList&order=asc&offset=0&limit=10"
    const val LIBRARY_ORDER_CANCEL_URL = "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=myOrderOperation"
    const val LIBRARY_QR_CERT_URL = "http://seat.lib.dlut.edu.cn/yanxiujian/client/2codecert.php?"

    const val LIBRARY_METHOD_IN = "in"
    const val LIBRARY_METHOD_OUT = "out"
    const val LIBRARY_METHOD_TEMP = "temp"

    const val PORTAL_SSO_URL = "https://sso.dlut.edu.cn/cas/login?service=https%3A%2F%2Fportal.dlut.edu.cn%2Ftp%2Fview%3Fm%3Dup#act=portal/viewhome"
    const val PORTAL_ELEC_URL = "https://portal.dlut.edu.cn/tp/up/subgroup/getElectricCharge"
    const val PORTAL_NET_URL = "https://portal.dlut.edu.cn/tp/up/subgroup/getWlzzList"

    const val PORTAL_DEFAULT_POST = "{}"

    const val BATH_SSO_URL = "https://sso.dlut.edu.cn/cas/login?service=http%3A%2F%2F202.118.74.5%3A8193%2FopenHomeRJPage"
    const val BATH_SAVE_CART_URL = "http://202.118.74.5:8193/goods/saveGoodsShopcar"
    const val BATH_UPDATE_CART_URL = "http://202.118.74.5:8193/goods/updateGoodsShopcar"
    const val BATH_MAIN_FUNC_URL = "http://202.118.74.5:8193/goods/queryNoChoiceTimeInfo"
    const val BATH_PAY_URL = "http://202.118.74.5:8193/payAdvanceOrder"

    const val VCARD_API_URL = "https://api.m.dlut.edu.cn/login?redirect_uri=https://card.m.dlut.edu.cn/homerj/openHomePage&response_type=code&scope=base_api&state=weishao"
    const val VCARD_OPENID_URL = "https://card.m.dlut.edu.cn/homerj/openRjOAuthPage"
}