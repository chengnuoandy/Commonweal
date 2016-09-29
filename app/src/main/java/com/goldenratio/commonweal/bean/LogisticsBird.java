package com.goldenratio.commonweal.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 冰封承諾Andy on 2016/8/15 0015.
 * 快递鸟对应的实体类
 */
public class LogisticsBird {

    //用户ID
    private String EBusinessID;
    //快递公司编码
    private String ShipperCode;
    //是否成功
    private boolean Success;
    //运单编号
    private String LogisticCode;
    //物流状态
    // 2-在途中,3-签收,4-问题件
    private String State;

    private List<Traces> Traces ;

    private Map<String,String> mMap;

    public LogisticsBird(){
        mMap = new HashMap<>();
        //初始化状态码相关
        mMap.put("2","运输中");
        mMap.put("3","已签收");
        mMap.put("4","快件出现问题");
        //初始化物流公司信息
        mMap.put("ANE","安能物流");
        mMap.put("AXD","安信达快递");
        mMap.put("BFDF","百福东方");
        mMap.put("BQXHM","北青小红帽");
        mMap.put("CCES","CCES快递");
        mMap.put("CITY100","城市100");
        mMap.put("COE","COE东方快递");
        mMap.put("CSCY","长沙创一");
        mMap.put("DBL","德邦");
        mMap.put("DHL","DHL");
        mMap.put("DSWL","D速物流");
        mMap.put("DTWL","大田物流");
        mMap.put("EMS","EMS");
        mMap.put("FAST","快捷速递");
        mMap.put("FEDEX","FedEx联邦快递");
        mMap.put("FKD","飞康达");
        mMap.put("GDEMS","广东邮政");
        mMap.put("GSD","共速达");
        mMap.put("GTO","国通快递");
        mMap.put("GTSD","高铁速递");
        mMap.put("HFWL","汇丰物流");
        mMap.put("HHTT","天天快递");
        mMap.put("HLWL","恒路物流");
        mMap.put("HOAU","天地华宇");
        mMap.put("hq568","华强物流");
        mMap.put("HTKY","百世汇通");
        mMap.put("HXLWL","华夏龙物流");
        mMap.put("HYLSD","好来运快递");
        mMap.put("JD","京东快递");
        mMap.put("JGSD","京广速递");
        mMap.put("JJKY","佳吉快运");
        mMap.put("JTKD","捷特快递");
        mMap.put("JXD","急先达");
        mMap.put("JYKD","晋越快递");
        mMap.put("JYM","加运美");
        mMap.put("JYWL","佳怡物流");
        mMap.put("LB","龙邦快递");
        mMap.put("LHT","联昊通速递");
        mMap.put("MHKD","民航快递");
        mMap.put("MLWL","明亮物流");
        mMap.put("NEDA","能达速递");
        mMap.put("QCKD","全晨快递");
        mMap.put("QFKD","全峰快递");
        mMap.put("QRT","全日通快递");
        mMap.put("SAWL","圣安物流");
        mMap.put("SDWL","上大物流");
        mMap.put("SF","顺丰快递");
        mMap.put("SFWL","盛丰物流");
        mMap.put("SHWL","盛辉物流");
        mMap.put("ST","速通物流");
        mMap.put("STO","申通快递");
        mMap.put("SURE","速尔快递");
        mMap.put("TSSTO","唐山申通");
        mMap.put("UAPEX","全一快递");
        mMap.put("UC","优速快递");
        mMap.put("WJWL","万家物流");
        mMap.put("WXWL","万象物流");
        mMap.put("XBWL","新邦物流");
        mMap.put("XFEX","信丰快递");
        mMap.put("XYT","希优特");
        mMap.put("YADEX","源安达快递");
        mMap.put("YCWL","远成物流");
        mMap.put("YD","韵达快递");
        mMap.put("YFEX","越丰物流");
        mMap.put("YFHEX","原飞航物流");
        mMap.put("YFSD","亚风快递");
        mMap.put("YTKD","运通快递");
        mMap.put("YTO","圆通速递");
        mMap.put("YZPY","邮政平邮/小包");
        mMap.put("ZENY","增益快递");
        mMap.put("ZHQKD","汇强快递");
        mMap.put("ZJS","宅急送");
        mMap.put("ZTE","众通快递");
        mMap.put("ZTKY","中铁快运");
        mMap.put("ZTO","中通速递");
        mMap.put("ZTWL","中铁物流");
        mMap.put("ZYWL","中邮物流");
    }

    public Map<String, String> getMap() {
        return mMap;
    }

    public String getEBusinessID() {
        return EBusinessID;
    }

    public void setEBusinessID(String EBusinessID) {
        this.EBusinessID = EBusinessID;
    }

    public String getLogisticCode() {
        return LogisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        LogisticCode = logisticCode;
    }

    public String getShipperCode() {
        return ShipperCode;
    }

    public void setShipperCode(String shipperCode) {
        ShipperCode = shipperCode;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public List<LogisticsBird.Traces> getTraces() {
        return Traces;
    }

    public void setTraces(List<LogisticsBird.Traces> traces) {
        Traces = traces;
    }

    /**
     * 匿名内部类
     * 包含订单的详情
     */
    public class Traces{
        //时间
        private String AcceptTime;
        //描述
        private String AcceptStation;
        //备注
        private String Remark;

        public String getAcceptStation() {
            return AcceptStation;
        }

        public void setAcceptStation(String acceptStation) {
            AcceptStation = acceptStation;
        }

        public String getAcceptTime() {
            return AcceptTime;
        }

        public void setAcceptTime(String acceptTime) {
            AcceptTime = acceptTime;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
        }
    }
}
