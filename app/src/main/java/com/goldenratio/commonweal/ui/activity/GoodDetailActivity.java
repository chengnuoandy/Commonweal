package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.iwgang.countdownview.CountdownView;

import static android.content.ContentValues.TAG;

public class GoodDetailActivity extends Activity implements View.OnClickListener {

    private CountdownView mCountdownView;
    private CountdownView mCountdownFive;
    private Long endTime;
    private Good mGood;
    private TextView mTvGoodName;
    private TextView mTvGoodDescription;
    private TextView mTvUserName;
    private GridView mGvPic;
    private TextView mTvBid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_detail);
        initView();
        initData();
        if (mGood.getGood_IsFirstBid()) {
            Toast.makeText(GoodDetailActivity.this, "还未出价，争做沙发吧！", Toast.LENGTH_SHORT).show();
            mTvBid.setText("立即出价");
            mTvBid.setClickable(true);
        } else {
            getBidUpdateTime();
        }
    }

    /**
     * 初始化数据
     * 从intent获取传过来的数据
     * 设置相应控件的数据显示
     */
    private void initData() {
        Intent intent = getIntent();
        endTime = intent.getLongExtra("EndTime", 0);
        mGood = (Good) intent.getSerializableExtra("Good");
        Log.d("lxc", "initData: ----> " + mGood.getGood_ID() + "endtime-->" + endTime);
        mCountdownView.start(endTime);

        mTvGoodName.setText(mGood.getGood_Name());
        mTvGoodDescription.setText(mGood.getGood_Description());
        //    mGvPic.setAdapter(new mAdapter(GoodDetailActivity.this));
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mTvGoodName = (TextView) findViewById(R.id.tv_good_name);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mCountdownView = (CountdownView) findViewById(R.id.cv_endtime);
        mCountdownFive = (CountdownView) findViewById(R.id.cv_five);
        mTvGoodDescription = (TextView) findViewById(R.id.tv_good_description);
//        mGvPic = (GridView) findViewById(R.id.gv_show_detail_pic);
        mTvBid = (TextView) findViewById(R.id.tv_bid);
        mTvBid.setOnClickListener(this);
        mTvBid.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bid:
                mTvBid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View root = View.inflate(GoodDetailActivity.this, R.layout.dialog_good_bid, null);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(GoodDetailActivity.this);
                        dialog.setView(root);
                        final Dialog dialog1 = dialog.create();
                        dialog1.show();
                        mTvBid = (TextView) root.findViewById(R.id.tv_bid);
                        mTvBid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTvBid.setClickable(false);
                                EditText mEtCoin = (EditText) root.findViewById(R.id.et_coin);
                                String mStrCoin = mEtCoin.getText().toString();
                                if (mStrCoin.equals("")) {
                                    Toast.makeText(GoodDetailActivity.this, "请输入出价公益币", Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(mStrCoin) <= mGood.getGood_NowCoin()) {
                                    Toast.makeText(GoodDetailActivity.this, "请输入大于当前公益币", Toast.LENGTH_SHORT).show();
                                } else {
                                    saveBidToBmob(mEtCoin, dialog1);
                                }
                            }
                        });
                    }
                });
                break;
        }
    }

    private void getBidUpdateTime() {

        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.addWhereEqualTo("objectId", mGood.getObjectId());
        goodBmobQuery.include("Good_Bid");
        goodBmobQuery.findObjects(new FindListener<Good>() {
            @Override
            public void done(List<Good> list, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "onSuccess: " + list.get(0).getGood_Bid().getCreatedAt());
                    getBmobServerTime(list.get(0).getGood_Bid().getCreatedAt());
                } else {
                    Log.d(TAG, "done: " + e.getMessage());
                    Toast.makeText(GoodDetailActivity.this, "时间获取失败，取消进入详情页", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void getBmobServerTime(final String createdAt) {

        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
                if (e == null) {
                    long createdTime = StringToLong(createdAt);
                    long nowTime = aLong * 1000L;
                    long leftTime = 30000 - (nowTime - createdTime);
                    Log.d(TAG, "onSuccess: " + createdTime + "-->" + nowTime);
                    Log.d(TAG, "onSuccess: " + leftTime);
                    mCountdownFive.start(leftTime);
                    if (leftTime < 0) {
                        mTvBid.setText("立即出价");
                        mTvBid.setClickable(true);
                    }
                } else {
                    Toast.makeText(GoodDetailActivity.this, "时间获取失败，取消进入详情页", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void saveBidToBmob(final EditText mEtCoin, final Dialog dialog) {
        String mStrObjectId = null;
        UserDao userDao = new UserDao(GoodDetailActivity.this);
        Cursor cursor = userDao.query("select * from User_Profile");
        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex("objectId");
            mStrObjectId = cursor.getString(nameColumnIndex);
        }
        cursor.close();

        if (mStrObjectId == null) {
            Toast.makeText(this, "用户信息获取错误,请稍后再试", Toast.LENGTH_SHORT).show();
        } else {
            User_Profile user_profile = new User_Profile();
            user_profile.setObjectId(mStrObjectId);

            Good good = new Good();
            good.setGood_NowCoin(Integer.parseInt(mEtCoin.getText().toString()));
            good.setGood_LatestAucUser(user_profile);
            good.setGood_IsFirstBid(false);
            final String finalMStrObjectId = mStrObjectId;
            good.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        User_Profile user_profile1 = new User_Profile();
                        user_profile1.setObjectId(finalMStrObjectId);
                        Good good1 = new Good();
                        good1.setObjectId(mGood.getObjectId());

                        Bid bid = new Bid();
                        bid.setBid_Coin(mEtCoin.getText().toString());
                        bid.setBid_User(user_profile1);
                        bid.setBid_Good(good1);
                        bid.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    mTvBid.setClickable(false);
                                    Toast.makeText(GoodDetailActivity.this, "出价成功", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    getBidUpdateTime();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(GoodDetailActivity.this, s, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    public long StringToLong(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long updateTime = 0;
        try {
            updateTime = sdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(updateTime);
        return updateTime;
    }

    class mAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public mAdapter(Context context) {
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mGood.getGood_Photos().size();
        }

        @Override
        public Object getItem(int position) {
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
                convertView = mLayoutInflater.inflate(R.layout.view_good_detail, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_good_detail_pic_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Glide.with(getBaseContext()).load(mGood.getGood_Photos().get(position)).into(viewHolder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }
    }
}
