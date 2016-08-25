package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.PayRecord;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kiuber on 2016/8/20.
 */


public class PayRecordActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView mLv;
    private List<PayRecord> mPayRecord;
    private TextView mTvLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
        initView();
        initData();
    }

    private void initData() {
        String objectID = ((MyApplication) getApplication()).getObjectID();
        BmobQuery<PayRecord> payRecordBmobQuery = new BmobQuery<>();
        payRecordBmobQuery.addWhereEqualTo("User_ID", objectID);
        payRecordBmobQuery.findObjects(new FindListener<PayRecord>() {
            @Override
            public void done(final List<PayRecord> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        mPayRecord = list;
                        mTvLoading.setVisibility(View.GONE);
                        mLv.setVisibility(View.VISIBLE);
                        loadData2ListView();
                    } else {
                        mTvLoading.setText("暂无交易记录");
                    }
                } else {
                    Toast.makeText(PayRecordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            private void loadData2ListView() {
                mLv.setAdapter(new BaseAdapter() {

                    @Override
                    public int getCount() {
                        return mPayRecord.size();
                    }

                    @Override
                    public PayRecord getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ViewHolder viewHolder = null;
                        if (viewHolder == null) {
                            viewHolder = new ViewHolder();
                            convertView = View.inflate(PayRecordActivity.this, R.layout.item_pay_record_listview, null);
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolder) convertView.getTag();
                        }
                        viewHolder.initView(convertView);
                        viewHolder.initData(position);
                        return convertView;
                    }

                    class ViewHolder {

                        private TextView mTvPRName;
                        private TextView mTvPRCoin;
                        private TextView mTvPRStatus;
                        private TextView mTvPRTime;
                        private boolean pr_status;

                        public void initView(View convertView) {
                            mTvPRName = (TextView) convertView.findViewById(R.id.tv_pr_name);
                            mTvPRCoin = (TextView) convertView.findViewById(R.id.tv_pr_coin);
                            mTvPRStatus = (TextView) convertView.findViewById(R.id.tv_pr_status);
                            mTvPRTime = (TextView) convertView.findViewById(R.id.tv_pr_time);
                        }

                        public void initData(int position) {
                            mTvPRName.setText(mPayRecord.get(position).getPR_Name());
                            mTvPRCoin.setText(mPayRecord.get(position).getPR_Coin());
                            pr_status = mPayRecord.get(position).isPR_Status();
                            if (pr_status) {
                                mTvPRStatus.setText("支付成功");
                            } else if (!pr_status) {
                                mTvPRStatus.setText("支付失败");
                            } else {
                                mTvPRStatus.setText("未知状态");
                            }
                            mTvPRTime.setText(mPayRecord.get(position).getCreatedAt());
                        }
                    }
                });
            }
        });
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mLv = (ListView) findViewById(R.id.lv_pay_record);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mLv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PayRecordDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("PayRecord", mPayRecord.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
