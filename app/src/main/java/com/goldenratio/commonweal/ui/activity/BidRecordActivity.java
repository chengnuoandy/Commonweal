package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Kiuber on 2016/8/22.
 */

public class BidRecordActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private List<Bid> list;
    private ListView mLv;
    private TextView mTvLoading;
    private int flag;//0 GoodDetailActivity 1 MyFragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_record);
        initView();
        initData();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initView() {
        mLv = (ListView) findViewById(R.id.lv_bid_record);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLv.setOnItemClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", -1);
        if (flag == 0) {
            queryByGoodId();
        } else if (flag == 1) {
            queryByUserId();
        } else {
            mTvLoading.setText("加载失败");
        }
    }

    private void queryByGoodId() {
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
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void queryByUserId() {
        String mUserId = ((MyApplication) getApplication()).getObjectID();
        BmobQuery<Bid> bidBmobQuery = new BmobQuery<Bid>();
        bidBmobQuery.addWhereEqualTo("Bid_User", mUserId);
        bidBmobQuery.include("Bid_Good");
        bidBmobQuery.findObjects(new FindListener<Bid>() {
            @Override
            public void done(List<Bid> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        mTvLoading.setVisibility(View.GONE);
                        BidRecordActivity.this.list = list;
                        setListViewAdapter();
                    } else if (list.size() == 0) {
                        mTvLoading.setText("暂无出价记录");
                    }
                } else {
                    mTvLoading.setText(e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
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
                viewHolder.clickListener(position);
                return convertView;
            }

            class ViewHolder {
                private CircleImageView mCivAvatar;
                private TextView mTvName;
                private TextView mTvCoin;
                private ImageView mIvGoodAvatar;

                private void initView(View convertView) {
                    mCivAvatar = (CircleImageView) convertView.findViewById(R.id.civ_avatar);
                    mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                    mTvCoin = (TextView) convertView.findViewById(R.id.tv_coin);
                    mIvGoodAvatar = (ImageView) convertView.findViewById(R.id.iv_good_avatar);
                }

                private void initData(int position) {
                    Bid bid = list.get(position);
                    mTvCoin.setText("出价公益币：" + bid.getBid_Coin());
                    if (flag == 0) {
                        Glide.with(BidRecordActivity.this)
                                .load(bid.getBid_User().getUser_image_hd())
                                .into(mCivAvatar);
                        mTvName.setText(bid.getBid_User().getUser_Nickname());
                    } else if (flag == 1) {
                        mCivAvatar.setVisibility(View.GONE);
                        Glide.with(BidRecordActivity.this).load(bid.getBid_Good().getGood_Photos().get(0)).into(mIvGoodAvatar);
                        mTvName.setText(bid.getBid_Good().getGood_Name());
                    } else {
                    }
                }

                public void clickListener(final int position) {
                    mCivAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyApplication myApplication = (MyApplication) getApplicationContext();
                            String mStrObjectId = myApplication.getObjectID();
                            if (!list.get(position).getBid_User().getObjectId().equals(mStrObjectId)) {
                                List<String> attenList;
                                attenList = list.get(position).getBid_User().getUser_Attention();
                                int isHas = -1;
                                if (attenList != null)
                                    isHas = attenList.indexOf(getItem(position).getBid_User().getObjectId());
                                Intent intent = new Intent(BidRecordActivity.this, StarInfoActivity.class);
                                intent.putExtra("ishas", isHas != -1);
                                intent.putExtra("id", list.get(position).getBid_User().getObjectId());
                                intent.putExtra("autograph", list.get(position).getBid_User().getUser_Autograph());
                                intent.putExtra("nickName", list.get(position).getBid_User().getUser_Nickname());
                                intent.putExtra("isv", list.get(position).getBid_User().isUser_IsV());
                                intent.putExtra("Avatar", list.get(position).getBid_User().getUser_image_hd());
                                startActivity(intent);
                            } else {
                                Toast.makeText(myApplication, "不要再点了，这里真的什么都没有~", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (flag == 0) {
        } else if (flag == 1) {
            Intent intent = new Intent(BidRecordActivity.this, GoodDetailActivity.class);
            intent.putExtra("objectId", list.get(position).getBid_Good().getObjectId());
            startActivity(intent);
        } else {
        }
    }
}
