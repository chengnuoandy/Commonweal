package com.goldenratio.commonweal.bean;

import java.io.Serializable;

/**
 * Created by Kiuber on 2016/8/14.
 */

public class MySqlGood implements Serializable {
    private String Object_Id;
    private String Start_Time;
    private String End_Time;
    private String Good_Status;

    public String getObject_Id() {
        return Object_Id;
    }

    public void setObject_Id(String object_Id) {
        Object_Id = object_Id;
    }

    public String getStart_Time() {
        return Start_Time;
    }

    public void setStart_Time(String start_Time) {
        Start_Time = start_Time;
    }

    public String getEnd_Time() {
        return End_Time;
    }

    public void setEnd_Time(String end_Time) {
        End_Time = end_Time;
    }

    public String getGood_Status() {
        return Good_Status;
    }

    public void setGood_Status(String good_Status) {
        Good_Status = good_Status;
    }
}
