package com.goldenratio.commonweal.iview;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/20.
 * Email:jxfengmtx@163.com
 */
public interface IMySqlManager {
    void showSixPwdOnFinishInput(String sixPwd, int event);

    boolean updateUserCoinByObjectId(String sumCoin);

    boolean queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd);

    boolean updateUserSixPwdByObjectId(String sixPwd);
}
