package com.goldenratio.commonweal.iview;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/20.
 * Email:jxfengmtx@163.com
 */
public interface IMySqlManager {
    void pay(boolean alipayOrWechatPay, double price, double allCoin, String changeCoin);

    void showSixPwdOnFinishInput(String sixPwd, int event);

    void updateUserCoinByObjectId(String sumCoin, String changeCoin,int PRName);

    void queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd,String DonateCoin);

    void updateUserSixPwdByObjectId(String sixPwd);
}
