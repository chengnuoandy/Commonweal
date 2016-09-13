package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.VerticalStepView;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.api.KdniaoTrackQueryAPI;
import com.goldenratio.commonweal.bean.LogisticsBird;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogisticsInformation extends BaseActivity {

    private VerticalStepView mStepView;
    private List<String> mList = new ArrayList<>();
    private TextView mTvStatus;
    private TextView mTvName;
    private TextView mTvNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_information);

        mStepView = (VerticalStepView) findViewById(R.id.step_view);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvNo = (TextView) findViewById(R.id.tv_no);

        initData();
    }

    private void initData() {
        new GetJson().execute("SF", "973134410593");
    }

    /**
     * 将json的Javabean实体转换成list<string>数据
     * 并且将数据设置到控件
     *
     * @param kdn 实体类
     */
    private void JavaBean2List(LogisticsBird kdn) {
        List<LogisticsBird.Traces> data = kdn.getTraces();
        Map<String,String> map = kdn.getMap();
        //转换成list<string>集合
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i).getAcceptStation() + "\n" + data.get(i).getAcceptTime());
        }

        mTvName.setText("承运来源：" + map.get(kdn.getShipperCode()));
        mTvNo.setText("运单编号：" + kdn.getLogisticCode());
        if (kdn.getState() == null){
            mTvStatus.setText("物流状态：未查询到结果");
            return;
        }else {
            mTvStatus.setText("物流状态：" + map.get(kdn.getState()));
        }

        mStepView.setStepsViewIndicatorComplectingPosition(mList.size() - 1)//设置完成的步数
                .reverseDraw(true)//default is true
                .setStepViewTexts(mList)//总步骤
                .setLinePaddingProportion(1.25f)//设置indicator线与线间距的比例系数
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(LogisticsInformation.this, R.color.orange))//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(LogisticsInformation.this, R.color.ordinary))//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(ContextCompat.getColor(LogisticsInformation.this, R.color.orange))//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(LogisticsInformation.this, R.color.ordinary))//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(LogisticsInformation.this, R.drawable.logistics))//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(LogisticsInformation.this, R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(LogisticsInformation.this, R.drawable.kd_now));//设置StepsViewIndicator AttentionIcon
    }

    private class GetJson extends AsyncTask<String, Void, String> {

        //获取json数据
        @Override
        protected String doInBackground(String... params) {
            KdniaoTrackQueryAPI kd = new KdniaoTrackQueryAPI();
            try {
                String data = kd.getOrderTracesByJson(params[0],params[1]);
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            LogisticsBird kdn = gson.fromJson(s,LogisticsBird.class);
            //判断是否查询成功
            if (kdn.isSuccess()){
                JavaBean2List(kdn);
            }else {
                Toast.makeText(LogisticsInformation.this, "查询失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
//            Log.d("lxc", "onPostExecute: " + s);
        }
    }
}
