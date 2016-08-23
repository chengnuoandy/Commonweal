package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kiuber on 2016/8/22.
 */

public class BidRecordActivity extends Activity {

    private List<Bid> list;
    private ListView mLv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_record);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        String goodId = intent.getStringExtra("goodId");
        BmobQuery<Bid> bidBmobQuery = new BmobQuery<>();
        bidBmobQuery.addWhereEqualTo("Bid_Good", goodId);
        bidBmobQuery.include("Bid_User");
        bidBmobQuery.findObjects(new FindListener<Bid>() {
            @Override
            public void done(List<Bid> list, BmobException e) {
                if (e == null) {
                    BidRecordActivity.this.list = list;
                    initData();
                } else {
                    Toast.makeText(BidRecordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initData() {
        mLv = (ListView) findViewById(R.id.lv_bid_record);
        mLv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Bid getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(BidRecordActivity.this);
                Bid bid = getItem(position);
                textView.setText(bid.getBid_User().getUser_Nickname() + "在" + bid.getCreatedAt()
                        + "出价" + bid.getBid_Coin());
                return textView;
            }
        });
    }
}
