package com.goldenratio.commonweal.bean;

import java.io.Serializable;

/**
 * Created by Kiuber on 2016/8/17.
 */

public class MySqlOrder implements Serializable {

    private String Object_Id;
    private String Order_Name;
    private String Order_Coin;
    private String Order_PicURL;
    private String Order_Status;
    private String Order_Good;

    public String getObject_Id() {
        return Object_Id;
    }

    public void setObject_Id(String object_Id) {
        Object_Id = object_Id;
    }

    public String getOrder_Name() {
        return Order_Name;
    }

    public void setOrder_Name(String order_Name) {
        Order_Name = order_Name;
    }

    public String getOrder_Coin() {
        return Order_Coin;
    }

    public void setOrder_Coin(String order_Coin) {
        Order_Coin = order_Coin;
    }

    public String getOrder_PicURL() {
        return Order_PicURL;
    }

    public void setOrder_PicURL(String order_PicURL) {
        Order_PicURL = order_PicURL;
    }

    public String getOrder_Status() {
        return Order_Status;
    }

    public void setOrder_Status(String order_Status) {
        Order_Status = order_Status;
    }

    public String getOrder_Good() {
        return Order_Good;
    }

    public void setOrder_Good(String order_Good) {
        Order_Good = order_Good;
    }
}
