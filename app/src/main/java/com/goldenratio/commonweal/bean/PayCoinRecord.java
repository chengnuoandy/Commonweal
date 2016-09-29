package com.goldenratio.commonweal.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kiuber on 2016/9/14.
 */

public class PayCoinRecord extends BmobObject {
    private Good PC_Good;
    private User_Profile PC_User;
    private String PC_Coin;

    public Good getPC_Good() {
        return PC_Good;
    }

    public void setPC_Good(Good PC_Good) {
        this.PC_Good = PC_Good;
    }

    public User_Profile getPC_User() {
        return PC_User;
    }

    public void setPC_User(User_Profile PC_User) {
        this.PC_User = PC_User;
    }

    public String getPC_Coin() {
        return PC_Coin;
    }

    public void setPC_Coin(String PC_Coin) {
        this.PC_Coin = PC_Coin;
    }
}
