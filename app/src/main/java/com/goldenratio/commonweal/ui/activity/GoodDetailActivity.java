package com.goldenratio.commonweal.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
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
import com.goldenratio.commonweal.dao.UserDao;
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
import java.util.Objects;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class GoodDetailActivity extends Activity implements View.OnClickListener {

    private CountdownView mCountdownView, mCountdownFive;
    private Long endTime;
    private Good mGood;
    private MySqlGood mySqlGood;
    private TextView mTvGoodName, mTvGoodDescription, mTvUserName,
            mTvBid, mTvDeposit, mTvNowCoin, mTvStartCoin;
    private GridView mGvPic;
    private LinearLayout mLlCv;
    private ImageView mIvOnePic;
    private int picSize;
    private GoodDetailActivity mContext;
    private Deposit mDeposit = null;
    private String mStrObjectId;
    private String mStrPwd;
    private String mStrUserCoin;
    private String mUserId;
    private Double deposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_detail);
        initView();
        initData();
        queryUserIdFromSqlite();
        isDeposit();
        mUserId = ((MyApplication) getApplication()).getObjectID();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initIsGoodStatus();
    }

    private void initIsGoodStatus() {
        //初始化物品的状态（从MySql中获取的数据）
        String status = mySqlGood.getGood_Status();
        if (status.equals("1")) {
            //正在进行
            //有人已经出价，现在才获取服务器时间，尽可能的保证时间准确
            getLastBidUpdatedAt();
        } else if (status.equals("0")) {
            changeTextViewVisibitity(2);
            mTvBid.setText("出价结束");
            //已经结束出价
        } else {
            changeTextViewVisibitity(2);
            mTvBid.setText("状态未知");
            //未知状态
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
        //公益币
        deposit = (Double.valueOf(mGood.getGood_StartCoin()) * 0.3);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(root);
                        final Dialog dialog = builder.create();
                        dialog.show();
                        dialog.getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        TextView mDialogTvBid = (TextView) root.findViewById(R.id.tv_bid);
                        mDialogTvBid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText mEtCoin = (EditText) root.findViewById(R.id.et_coin);
                                String mStrCoin = mEtCoin.getText().toString();
                                if (mStrCoin.equals("")) {
                                    Toast.makeText(mContext, "请输入出价公益币", Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(mStrCoin) <= Integer.parseInt(mGood.getGood_NowCoin())) {
                                    Toast.makeText(mContext, "请输入大于当前公益币", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    querySixPwdStatusFromMySql(mEtCoin.getText().toString());
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.tv_deposit:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                builder.setMessage("保证金为物品起步价的30%。\n" + "本物品起步价：" + mGood.getGood_StartCoin() + "公益币\n保证金：" +
                        deposit + "公益币");
                builder.setPositiveButton("交保证金", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //从Mysql查询用户当前公益币
                        queryUserCoinByObjectId();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
        }
    }

    private void getLastBidUpdatedAt() {
        BmobQuery<Bid> bidBmobQuery = new BmobQuery<>();
        bidBmobQuery.addQueryKeys("createdAt");
        bidBmobQuery.order("-createdAt");
        bidBmobQuery.addWhereEqualTo("Bid_Good", mGood.getObjectId());
        bidBmobQuery.findObjects(new FindListener<Bid>() {
            @Override
            public void done(List<Bid> list, BmobException e) {
                if (e == null) {
                    getBmobServerTime(list.get(0).getCreatedAt());
                } else {
                    changeTextViewVisibitity(2);
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
                    //30000： 30秒内所有不能再次出价。降低服务器并发及最后一位出价者的不确定性。
                    long leftTime = 30000 - (nowTime - createdTime);
                    if (leftTime < 0) {
                        changeTextViewVisibitity(2);
                        Log.d(TAG, "onSuccess: " + createdTime + "-->" + nowTime);
                        Log.d(TAG, "onSuccess: " + leftTime);
                        //30秒内无用户出价
                    } else {
                        changeTextViewVisibitity(0);
                        //30秒内有用户出价
                        mCountdownFive.start(leftTime);
                        mCountdownFive.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                            @Override
                            public void onEnd(CountdownView cv) {
                                //30秒倒计时结束，显示“出价”控件
                                changeTextViewVisibitity(2);
                            }
                        });
                    }
                } else {
                    Log.d("Kiuber_LOG", "fail: " + e.getMessage());
                    changeTextViewVisibitity(3);
                }
            }
        });
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
                    } else if (listSize == 0) {
                        changeTextViewVisibitity(1);
//                        Toast.makeText(mContext, "未支付保证金", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "未知状态", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Kiuber_LOG", "done: " + mStrObjectId + "~" + mGood.getObjectId());
                }
            }
        });
    }

    private void pay(final boolean alipayOrWechatPay, final double price, final ProgressDialog progressDialog) {

        BP.pay("公益币充值", "描述", price, alipayOrWechatPay, new PListener() {


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
                updateUserCoin(deposit + price);
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderI) {
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
                            showPayKeyBoard(deposit + "", progressDialog);
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

    public void showPayKeyBoard(String coin, ProgressDialog progressDialog) {
        progressDialog.dismiss();
        PopEnterPassword popEnterPassword = new PopEnterPassword(this, "交保证金", coin, "保证金", mGood.getObjectId());

        // 显示窗口
        popEnterPassword.showAtLocation(this.findViewById(R.id.layoutContent),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    public void showPayKeyBoard1(String coin) {
        PopEnterPassword popEnterPassword = new PopEnterPassword(this, "出价验证", coin, "出价", mUserId, mGood.getObjectId());

        // 显示窗口
        popEnterPassword.showAtLocation(this.findViewById(R.id.layoutContent),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    public void showPayKeyBoard2() {
        PopEnterPassword popEnterPassword = new PopEnterPassword(this, "设置六位数密码", "", "新密码", mUserId, 0);

        // 显示窗口
        popEnterPassword.showAtLocation(this.findViewById(R.id.layoutContent),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    private void queryUserCoinByObjectId() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "正在加载", false);
        String url = "http://123.206.89.67/WebService1.asmx/QueryUserCoinByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
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
                                //保证金-用户的公益币
                                double usercoin = Double.valueOf(mStrUserCoin);
                                //用户所缺公益币数
                                final double poor = deposit - usercoin;
                                final double sxf = (poor / 10) * 0.05;
                                if (poor > 0) {
                                    //公益币不足，提示充值
                                    final android.support.v7.app.AlertDialog.Builder builder =
                                            new android.support.v7.app.AlertDialog.Builder(GoodDetailActivity.this);
                                    builder.setMessage("账号公益币：" + usercoin + "，还缺" + poor + "公益币，您将充值"
                                            + poor + "公益币（由于个人开发团队限制，" +
                                            "平台收取5%的手续费）");
                                    builder.setPositiveButton("去充值", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //充值金额包括公益币、充值Bmob收取的0.05的费率。
                                            pay(false, sxf + poor / 10, progressDialog);
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
                                    //提交物品起步价的30%的公益币作为保证金
                                    showPayKeyBoard(deposit + "", progressDialog);
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

    private void changeTextViewVisibitity(int flag) {
        switch (flag) {
            case 0:
                mLlCv.setVisibility(View.VISIBLE);
                mTvDeposit.setVisibility(View.GONE);
                mTvBid.setVisibility(View.GONE);
                break;
            case 1:
                mLlCv.setVisibility(View.GONE);
                mTvDeposit.setVisibility(View.VISIBLE);
                mTvBid.setVisibility(View.GONE);
                break;
            case 2:
                mLlCv.setVisibility(View.GONE);
                mTvDeposit.setVisibility(View.GONE);
                mTvBid.setVisibility(View.VISIBLE);
                break;
            case 3:
                mLlCv.setVisibility(View.GONE);
                mTvDeposit.setVisibility(View.GONE);
                mTvBid.setVisibility(View.GONE);
                break;
        }
    }

    private void querySixPwdStatusFromMySql(final String bidCoin) {
        String url = "http://123.206.89.67/WebService1.asmx/QueryUserSixPwdByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("ObjectId", mUserId)
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
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, e1, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                mContext.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                String sixPwd = "0";
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    sixPwd = jsonObject.getString("User_SixPwd");
                                }
                                if (Objects.equals(sixPwd, "0")) {
                                    showPayKeyBoard2();
                                } else {
                                    showPayKeyBoard1(bidCoin);
                                }
                                Log.d(TAG, "run: " + sixPwd + "-->" + result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, "JSONException: " + e.getMessage());
                            }
                        }
                    }
                });
            }
        });
    }
}
