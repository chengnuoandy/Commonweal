package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlGood;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class GoodDetailActivity extends Activity implements View.OnClickListener {

    private CountdownView mCountdownView;
    private CountdownView mCountdownFive;
    private Long endTime;
    private Good mGood;
    private MySqlGood mySqlGood;
    private TextView mTvGoodName;
    private TextView mTvGoodDescription;
    private TextView mTvUserName;
    private GridView mGvPic;
    private TextView mTvBid;
    private LinearLayout mLlCv;
    private ImageView mIvOnePic;
    private int picSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_detail);
        initView();
        initData();
        String status = mySqlGood.getGood_Status();
        if (status.equals("1")) {
            if (mGood.getGood_IsFirstBid()) {
                Toast.makeText(GoodDetailActivity.this, "还未出价，争做沙发吧！", Toast.LENGTH_SHORT).show();
                mLlCv.setVisibility(View.GONE);
                mTvBid.setVisibility(View.VISIBLE);
            } else {
                getBidUpdateTime();
            }
        } else if (status.equals("0")) {
            mLlCv.setVisibility(View.GONE);
            mTvBid.setClickable(false);
            mTvBid.setVisibility(View.VISIBLE);
            mTvBid.setText("物品已经结束");
            mTvBid.setTextColor(Color.RED);
        } else {
            mLlCv.setVisibility(View.GONE);
            mTvBid.setClickable(false);
            mTvBid.setVisibility(View.VISIBLE);
            mTvBid.setText("物品状态获取失败");
            mTvBid.setTextColor(Color.RED);
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
        mGood = (Good) intent.getSerializableExtra("Bmob_Good");
        mySqlGood = (MySqlGood) intent.getSerializableExtra("Mysql_Good");
        Log.d("lxc", "initData: ----> " + mGood.getObjectId() + "endtime-->" + endTime);
        mCountdownView.start(endTime);

        mTvGoodName.setText(mGood.getGood_Name());
        mTvGoodDescription.setText(mGood.getGood_Description());
        picSize = mGood.getGood_Photos().size();
        if (picSize == 1) {
            Glide.with(this).load(mGood.getGood_Photos().get(0)).into(mIvOnePic);
        } else {
            mGvPic.setAdapter(new mAdapter());
        }
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
        mGvPic = (GridView) findViewById(R.id.gv_show_detail_pic);
        mTvBid = (TextView) findViewById(R.id.tv_bid);
        mLlCv = (LinearLayout) findViewById(R.id.ll_good_detail_cv);
        mIvOnePic = (ImageView) findViewById(R.id.iv_one_pic);
        mTvBid.setOnClickListener(this);
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
                        dialog1.getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        TextView mDialogTvBid = (TextView) root.findViewById(R.id.tv_bid);
                        mDialogTvBid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTvBid.setClickable(false);
                                EditText mEtCoin = (EditText) root.findViewById(R.id.et_coin);
                                String mStrCoin = mEtCoin.getText().toString();
                                if (mStrCoin.equals("")) {
                                    Toast.makeText(GoodDetailActivity.this, "请输入出价公益币", Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(mStrCoin) <= Integer.parseInt(mGood.getGood_NowCoin())) {
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
        goodBmobQuery.include("Good_Bid");
        goodBmobQuery.getObject(mGood.getObjectId(), new QueryListener<Good>() {
            @Override
            public void done(Good good, BmobException e) {
                if (e == null) {
                    Log.d("Kiuber_LOG", "onSuccess: " + good.getGood_Bid().getCreatedAt());
                    getBmobServerTime(good.getGood_Bid().getCreatedAt());
                } else {
                    Log.d("Kiuber_LOG", "done: " + e.getMessage() + e.getErrorCode() + good);
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
                        mTvBid.setVisibility(View.VISIBLE);
                        mLlCv.setVisibility(View.GONE);
                    }
                } else {
                    Log.d("Kiuber_LOG", "onSuccess: " + e.getMessage());
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
            good.setObjectId(mGood.getObjectId());

            Bid bid = new Bid();
            bid.setBid_User(user_profile);
            bid.setBid_Good(good);
            bid.setBid_Coin(mEtCoin.getText().toString());
            final String finalMStrObjectId1 = mStrObjectId;
            bid.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        updateGood2Bmob(finalMStrObjectId1, mGood.getObjectId(), objectId, mEtCoin.getText().toString());
                    } else {
                        Log.d("Kiuber_LOG", "done: " + e.getMessage());
                    }
                }
            });
            mTvBid.setClickable(false);
            mTvBid.setVisibility(View.GONE);
            dialog.dismiss();
        }
    }

    private void updateGood2Bmob(String user_id, String good_id, final String bid_id, String coin) {
        User_Profile user_profile = new User_Profile();
        user_profile.setObjectId(user_id);
        Bid bid = new Bid();
        bid.setObjectId(bid_id);

        Good good = new Good();
        good.setGood_NowCoin(coin);
        good.setGood_NowBidUser(user_profile);
        good.setGood_Bid(bid);
        good.setGood_IsFirstBid(false);
        good.update(good_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    updateGood2MySql(bid_id);
                } else {
                    Log.d("Kiuber_LOG", "done: " + e.getMessage());
                }
            }
        });
    }

    private void updateGood2MySql(final String bid_id) {
        String url = "http://123.206.89.67/WebService1.asmx/UpdateGoodNowBid";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Good_ObjectId", mGood.getObjectId())
                .add("Bid_ObjectId", bid_id)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                final String e1 = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GoodDetailActivity.this, e1, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.contains("success")) {
                            Toast.makeText(GoodDetailActivity.this, "出价成功", Toast.LENGTH_SHORT).show();
                            Log.d("Kiuber_LOG", mGood.getObjectId() + ": " + bid_id + "\n" + result);
                            getBidUpdateTime();
                        } else {
                            Log.d("Kiuber_LOG", "fail: " + result);
                        }
                    }
                });
            }
        });
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
        private DisplayMetrics displayMetrics;
        private Context mContext;
        int width, height;

        public mAdapter() {

            mContext = GoodDetailActivity.this;
            displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }

        @Override
        public int getCount() {
            return picSize;
        }

        @Override
        public List getItem(int position) {
            return mGood.getGood_Photos();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(GoodDetailActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(width / 3, width / 3));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(GoodDetailActivity.this).load(getItem(position).get(position)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicPhotoShow.class);
                    intent.putExtra("index", position);
                    intent.putStringArrayListExtra("list", (ArrayList<String>) mGood.getGood_Photos());
                    mContext.startActivity(intent);
                    //设置切换动画
                    ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
            return imageView;
        }

    }
}
