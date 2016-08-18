package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;
import com.goldenratio.commonweal.bean.Deposit;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlGood;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.util.MD5Util;
import com.goldenratio.commonweal.widget.PopEnterPassword;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
    private TextView mTvNowCoin;
    private TextView mTvStartCoin;
    private String mStrObjectId;
    private TextView mTvDeposit;
    private GoodDetailActivity mContext;
    private Deposit mDeposit = null;
    private String mStrPwd;
    private String mStrGoodCoin;
    private String mStrUserCoin;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_detail);
        initView();
        initData();
        queryUserIdFromSqlite();
        isDeposit();
        initIsGoodStatus();
    }

    private void initIsGoodStatus() {
        String status = mySqlGood.getGood_Status();
        if (status.equals("1")) {
            if (mGood.getGood_IsFirstBid()) {
                Toast.makeText(GoodDetailActivity.this, "还未出价，争做沙发吧！", Toast.LENGTH_SHORT).show();
                mLlCv.setVisibility(View.GONE);
                mTvDeposit.setVisibility(View.VISIBLE);
            } else {
                getBidUpdateTime();
                if (mTvDeposit.getVisibility() == View.VISIBLE) {
                    mLlCv.setVisibility(View.GONE);
                }
            }
        } else if (status.equals("0")) {
            mLlCv.setVisibility(View.GONE);
            mTvBid.setClickable(false);
            mTvBid.setVisibility(View.VISIBLE);
            mTvBid.setText("物品已经结束");
            mTvDeposit.setVisibility(View.GONE);
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
        mTvStartCoin.setText(mGood.getGood_StartCoin());
        mTvNowCoin.setText(mGood.getGood_NowCoin());
        picSize = mGood.getGood_Photos().size();
        if (picSize == 1) {
            Glide.with(this).load(mGood.getGood_Photos().get(0)).into(mIvOnePic);
        } else {
            mGvPic.setAdapter(new mAdapter());
        }
        mStrGoodCoin = (Long.parseLong(mGood.getGood_StartCoin()) * 0.3) + "";
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mContext = GoodDetailActivity.this;
        mTvGoodName = (TextView) findViewById(R.id.tv_good_name);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mCountdownView = (CountdownView) findViewById(R.id.cv_endtime);
        mCountdownFive = (CountdownView) findViewById(R.id.cv_five);
        mTvGoodDescription = (TextView) findViewById(R.id.tv_good_description);
        mGvPic = (GridView) findViewById(R.id.gv_show_detail_pic);
        mTvBid = (TextView) findViewById(R.id.tv_bid);
        mLlCv = (LinearLayout) findViewById(R.id.ll_good_detail_cv);
        mIvOnePic = (ImageView) findViewById(R.id.iv_one_pic);
        mTvStartCoin = (TextView) findViewById(R.id.tv_start_coin);
        mTvNowCoin = (TextView) findViewById(R.id.tv_now_coin);
        mTvDeposit = (TextView) findViewById(R.id.tv_deposit);
        mTvBid.setOnClickListener(this);
        mTvDeposit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bid:
                mTvBid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View root = View.inflate(mContext, R.layout.dialog_good_bid, null);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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
                                    Toast.makeText(mContext, "请输入出价公益币", Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(mStrCoin) <= Integer.parseInt(mGood.getGood_NowCoin())) {
                                    Toast.makeText(mContext, "请输入大于当前公益币", Toast.LENGTH_SHORT).show();
                                } else {
                                    saveBidToBmob(mEtCoin, dialog1);
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.tv_deposit:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                builder.setMessage("您将支付" + mStrGoodCoin + "保证金，是否继续？");
                builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //pwdConfirm();
                        queryUserCoinByObjectId();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
        }
    }

    private void pwdConfirm() {
        View view = View.inflate(mContext, R.layout.dialog_good_deposit, null);
        final EditText mEtPwd = (EditText) view.findViewById(R.id.et_password);
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder1.setView(view);
        builder1.setPositiveButton("验证", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mD5Pwd = MD5Util.createMD5(mEtPwd.getText().toString().trim());
                if (mD5Pwd.equals(mStrPwd)) {
                    ProgressDialog progressDialog = ProgressDialog.show(mContext, null, "正在跳转", false);
                    pay(false, 0.01, progressDialog);
                } else {
                    Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder1.setNegativeButton("取消", null);
        builder1.show();
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
                    Toast.makeText(mContext, "时间获取失败，取消进入详情页", Toast.LENGTH_SHORT).show();
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
                    mCountdownFive.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                        @Override
                        public void onEnd(CountdownView cv) {
                            mTvBid.setClickable(true);
                            mLlCv.setVisibility(View.GONE);
                            mTvBid.setVisibility(View.VISIBLE);
                        }
                    });
                    if (leftTime < 0) {
                        mLlCv.setVisibility(View.GONE);
                        mTvBid.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("Kiuber_LOG", "onSuccess: " + e.getMessage());
                    Toast.makeText(mContext, "时间获取失败，取消进入详情页", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void saveBidToBmob(final EditText mEtCoin, final Dialog dialog) {

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

    private void queryUserIdFromSqlite() {
        mStrObjectId = null;
        UserDao userDao = new UserDao(mContext);
        Cursor cursor = userDao.query("select * from User_Profile");
        while (cursor.moveToNext()) {
            int objectIdColumnIndex = cursor.getColumnIndex("objectId");
            int userPwdColumnIndex = cursor.getColumnIndex("User_Password");
            mStrObjectId = cursor.getString(objectIdColumnIndex);
            mStrPwd = cursor.getString(userPwdColumnIndex);
        }
        cursor.close();
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
                        Toast.makeText(mContext, e1, Toast.LENGTH_SHORT).show();
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
                            mTvBid.setVisibility(View.GONE);
                            mLlCv.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, "出价成功", Toast.LENGTH_SHORT).show();
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

        private mAdapter() {

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
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(width / 3, width / 3));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(mContext).load(getItem(position).get(position)).into(imageView);

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

    private void isDeposit() {
        BmobQuery<Deposit> depositBmobQuery = new BmobQuery<>();
        depositBmobQuery.addWhereEqualTo("D_User", mStrObjectId);
        depositBmobQuery.include("D_User");
        depositBmobQuery.addWhereEqualTo("D_GoodId", mGood.getObjectId());
        depositBmobQuery.findObjects(new FindListener<Deposit>() {
            @Override
            public void done(List<Deposit> list, BmobException e) {
                if (e == null) {
                    int listSize = list.size();
//                    Toast.makeText(mContext, list.get(0).getCreatedAt(), Toast.LENGTH_SHORT).show();
                    if (listSize == 1) {
                        mDeposit = list.get(0);
                        mTvDeposit.setVisibility(View.GONE);
                        mTvBid.setVisibility(View.VISIBLE);
                    } else if (listSize == 0) {
//                        Toast.makeText(mContext, "未支付保证金", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "未知状态", Toast.LENGTH_SHORT).show();
                        mTvDeposit.setVisibility(View.GONE);
                    }
                } else {
//                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Kiuber_LOG", "done: " + mStrObjectId + "~" + mGood.getObjectId());
                }
            }
        });
    }

    private void pay(final boolean alipayOrWechatPay, final double price, final ProgressDialog progressDialog) {

        BP.pay("名称", "描述", price, alipayOrWechatPay, new PListener() {


            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(mContext, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                progressDialog.dismiss();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                updateUserCoin(Double.valueOf(mStrGoodCoin) + price);
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderI) {
                orderId = orderI;
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                progressDialog.dismiss();
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            mContext,
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
                    installBmobPayPlugin("bp.db");
                } else {
                    Toast.makeText(mContext, "支付中断!" + reason, Toast.LENGTH_SHORT)
                            .show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void updateUserCoin(double sumCoin) {
        Toast.makeText(mContext, sumCoin + "", Toast.LENGTH_SHORT).show();
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "正在加载", false);
        String url = "http://123.206.89.67/WebService1.asmx/UpdateUserCoinByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        final String mStrObjectId = ((MyApplication) getApplication()).getObjectID();
        if (mStrObjectId != null) {
            RequestBody body = new FormBody.Builder()
                    .add("ObjectId", mStrObjectId)
                    .add("UserCoin", sumCoin + "")
                    .build();
            Log.d("Kiuber_LOG", "updateUserCoin: " + mStrObjectId + "-->" + sumCoin);

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showPayKeyBoard(mStrGoodCoin, progressDialog, 0);
                        }
                    });
                }
            });
        }
    }


    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPayKeyBoard(String order_coin, ProgressDialog progressDialog, int flag) {
        progressDialog.dismiss();
        PopEnterPassword popEnterPassword = new PopEnterPassword(this, "支付保证金", order_coin, flag,mGood.getObjectId(),orderId);

        // 显示窗口
        popEnterPassword.showAtLocation(this.findViewById(R.id.layoutContent),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    private void queryUserCoinByObjectId() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "正在加载", false);
        String url = "http://123.206.89.67/WebService1.asmx/QueryUserCoinByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        String mStrObjectId = ((MyApplication) getApplication()).getObjectID();
        if (mStrObjectId != null) {
            RequestBody body = new FormBody.Builder()
                    .add("ObjectId", mStrObjectId)
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
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(result);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    mStrUserCoin = jsonObject.getString("User_Coin");
                                }
                                mStrGoodCoin = (Long.parseLong(mGood.getGood_StartCoin()) * 0.3) + "";
                                final double poor = Double.valueOf(mStrGoodCoin) - Double.valueOf(mStrUserCoin);
                                if (poor > 0) {
                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GoodDetailActivity.this);
                                    builder.setMessage("账号公益币不足，是否立即充值？");
                                    builder.setPositiveButton("去充值", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            pay(false, poor, progressDialog);
                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                } else {
                                    showPayKeyBoard(mStrGoodCoin, progressDialog, 0);
                                }
                            } catch (JSONException e) {
                                Log.d("Kiuber_LOG", e.getMessage() + request);
                            }
                        }
                    });
                }
            });
        }
    }
}
