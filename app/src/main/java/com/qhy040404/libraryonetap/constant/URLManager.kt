package com.qhy040404.libraryonetap.constant

object URLManager {
    const val GITHUB_PAGE = "https://github.com/qhy040404"
    const val GITHUB_REPO = "https://github.com/qhy040404/Library-One-Tap-Android"

    const val LIBRARY_SSO_URL =
        "https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php"
    const val LIBRARY_LOGIN_DIRECT_URL =
        "http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php"
    const val LIBRARY_SESSION_URL =
        "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=checkSession"
    const val LIBRARY_ORDER_LIST_URL =
        "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=myOrderList&order=asc&offset=0&limit=15"
    const val LIBRARY_ORDER_CANCEL_URL =
        "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=myOrderOperation"
    const val LIBRARY_QR_CERT_URL = "http://seat.lib.dlut.edu.cn/yanxiujian/client/2codecert.php?"
    const val LIBRARY_RESERVE_URL =
        "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=randomRoomSeatChoose"
    const val LIBRARY_RESERVE_ADDCODE_URL =
        "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=seatChoose"
    const val LIBRART_RESERVE_FINAL_URL =
        "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=addSeatOrder"

    const val PORTAL_SSO_URL =
        "https://sso.dlut.edu.cn/cas/login?service=https%3A%2F%2Fportal.dlut.edu.cn%2Ftp%2Fview%3Fm%3Dup#act=portal/viewhome"
    const val PORTAL_ELEC_URL = "https://portal.dlut.edu.cn/tp/up/subgroup/getElectricCharge"
    const val PORTAL_NET_URL = "https://portal.dlut.edu.cn/tp/up/subgroup/getWlzzList"

    const val BATH_SSO_URL =
        "https://sso.dlut.edu.cn/cas/login?service=http%3A%2F%2F202.118.74.5%3A8193%2FopenHomeRJPage"
    const val BATH_DIRECT_URL = "http://202.118.74.5:8193/openHomeRJPage"
    const val BATH_SAVE_CART_URL = "http://202.118.74.5:8193/goods/saveGoodsShopcar"
    const val BATH_UPDATE_CART_URL = "http://202.118.74.5:8193/goods/updateGoodsShopcar"
    const val BATH_MAIN_FUNC_URL = "http://202.118.74.5:8193/goods/queryNoChoiceTimeInfo"
    const val BATH_PAY_URL = "http://202.118.74.5:8193/payAdvanceOrder"

    const val VCARD_API_URL =
        "https://api.m.dlut.edu.cn/login?redirect_uri=https://card.m.dlut.edu.cn/homerj/openHomePage&response_type=code&scope=base_api&state=weishao"
    const val VCARD_OPENID_URL = "https://card.m.dlut.edu.cn/homerj/openRjOAuthPage"

    const val VOLTIME_POST_URL = "https://www.dutbit.com/apivue/voltime/"

    fun constructAvailableUrl(date: String, room: String): String {
        return "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=querySeatMap&order_date=$date&room_id=$room"
    }


    fun getQRUrl(method: String, id: String): String {
        return "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$id"
    }
}