package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.BusinessAdapter;
import com.goldenratio.commonweal.bean.Business;
import com.goldenratio.commonweal.util.BusinessUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/8/20.
 */



public class BusinessActivity extends Activity {

    private ImageView mIvBack;
    private ListView mLvBusiness;
    private ArrayList mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        initView();
        initViewBtn();
        show();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mLvBusiness = (ListView) findViewById(R.id.lv_business);
    }

    /**
     * 页面的点击事件
     */
    private void initViewBtn() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * Item的点击事件
         * 并将查询到的参数传递给下个页面
         */
        mLvBusiness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusinessUtil businessUtil = (BusinessUtil) mArrayList.get(position);
                Intent intent = new Intent(BusinessActivity.this,BusinessDetailed.class);
                intent.putExtra("money",businessUtil.Business_money); //交易金额
                intent.putExtra("name",businessUtil.Business_Name);//交易名称
                intent.putExtra("state",businessUtil.Business_state);//交易状态 是否支付成功 ---当前状态
                intent.putExtra("time",businessUtil.Business_time);//交易时间
                intent.putExtra("use",businessUtil.Business_Use);//交易对象  ----商户对象
                intent.putExtra("type",businessUtil.Business_type);//交易类型   公益币 或 Rmb
                intent.putExtra("payment",businessUtil.Business_payment); //支付方式
                intent.putExtra("Business_number",businessUtil.Business_numbers);//交易单号
                intent.putExtra("Customer_numbers",businessUtil.Customer_numbers);//客户单号
                startActivity(intent);
            }
        });
    }

    /**
     * 从服务器获取数据
     */
    private void show(){
        mArrayList = new ArrayList();
        final BmobQuery<Business> businessBmobQuery = new BmobQuery<>();
        businessBmobQuery.findObjects(new FindListener<Business>() {
            @Override
            public void done(List<Business> list, BmobException e) {
                if (e == null){
                    for(Business business : list){
                        BusinessUtil businessUtil = new BusinessUtil();
                        businessUtil.Business_time=business.getBusiness_time();
                        businessUtil.Business_Name = business.getBusiness_Name();
                        businessUtil.Business_money = business.getBusiness_Coin();
                        businessUtil.Business_state = business.getBusiness_state();
                        businessUtil.Business_Use = business.getBusiness_Use();
                        businessUtil.Business_type = business.getBusiness_Type();
                        businessUtil.Business_payment = business.getBusiness_payment();
                        businessUtil.Business_numbers = business.getBusiness_Number();
                        businessUtil.Customer_numbers = business.getCustomer_numbers();
                        mArrayList.add(businessUtil);
                        BusinessAdapter businessAdapter = new BusinessAdapter(mArrayList,getApplicationContext());
                        mLvBusiness.setAdapter(businessAdapter);
                    }
                }else {
                    Log.d("Charm", "done: "+e);
                }
            }
        });
    }
}
