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

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Kiuber on 2016/8/22.
 */

public class BidRecordActivity extends Activity {

    private List<Bid> list;
    private ListView mLv;
    private TextView mTvLoading;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_record);
        initView();
        initData();
    }

    private void initView() {
        mLv = (ListView) findViewById(R.id.lv_bid_record);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
    }

    private void initData() {
        Intent intent = getIntent();
        String goodId = intent.getStringExtra("goodId");
        BmobQuery<Bid> bidBmobQuery = new BmobQuery<>();
        bidBmobQuery.addWhereEqualTo("Bid_Good", goodId);
        bidBmobQuery.include("Bid_User");
        bidBmobQuery.findObjects(new FindListener<Bid>() {
            @Override
            public void done(List<Bid> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        mTvLoading.setText("暂无出价记录");
                    } else {
                        mTvLoading.setVisibility(View.GONE);
                        BidRecordActivity.this.list = list;
                        setListViewAdapter();
                    }
                } else {
                    Toast.makeText(BidRecordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setListViewAdapter() {
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
                ViewHolder viewHolder = null;
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(BidRecordActivity.this, R.layout.item_bid_record_listview, null);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.initView(convertView);
                viewHolder.initData(position);
                return convertView;
            }

            class ViewHolder {
                private CircleImageView mCivAvatar;
                private TextView mTvName;
                private TextView mTvCoin;

                private void initView(View convertView) {
                    mCivAvatar = (CircleImageView) convertView.findViewById(R.id.civ_avatar);
                    mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                    mTvCoin = (TextView) convertView.findViewById(R.id.tv_coin);
                }

                private void initData(int position) {
                    Bid bid = list.get(position);
                    Glide.with(BidRecordActivity.this).load(bid.getBid_User().getUser_image_hd()).into(mCivAvatar);
                    mTvName.setText(bid.getBid_User().getUser_Nickname());
                    mTvCoin.setText("出价公益币：" + bid.getBid_Coin());
                }
            }
        });
    }
}
