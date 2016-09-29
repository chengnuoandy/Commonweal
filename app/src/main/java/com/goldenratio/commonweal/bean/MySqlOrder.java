package com.goldenratio.commonweal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kiuber on 2016/8/17.
 */

public class MySqlOrder implements Serializable {

    private String Object_Id;
    private Good Order_Good;
    private User_Profile Order_User;
    private String Order_Code;
    private String Order_Company;

    public String getObject_Id() {
        return Object_Id;
    }

    public void setObject_Id(String object_Id) {
        Object_Id = object_Id;
    }

    public Good getOrder_Good() {
        return Order_Good;
    }

    public void setOrder_Good(Good order_Good) {
        Order_Good = order_Good;
    }

    public User_Profile getOrder_User() {
        return Order_User;
    }

    public void setOrder_User(User_Profile order_User) {
        Order_User = order_User;
    }

    public String getOrder_Code() {
        return Order_Code;
    }

    public void setOrder_Code(String order_Code) {
        Order_Code = order_Code;
    }

    public String getOrder_Company() {
        return Order_Company;
    }

    public void setOrder_Company(String order_Company) {
        Order_Company = order_Company;
    }
}
