package com.goldenratio.commonweal.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;
import com.goldenratio.commonweal.bean.Deposit;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class GoodDetailActivity extends BaseActivity implements View.OnClickListener
        , BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, IMySqlManager {

    private CountdownView mCountdownView, mCountdownFive;
    private Long endTime;
    private Good mGood;
    private TextView mTvGoodName, mTvGoodDescription, mTvUserName,
            mTvBid, mTvDeposit, mTvNowCoin, mTvStartCoin, mTvStartTime, mTvLastTime, mTvLoading;
    private LinearLayout mLlCv, mLlCv1;
    private int picSize;
    private GoodDetailActivity mContext;
    private String mUserId;
    private int depositCoin;
    private SliderLayout mDemoSlider;
    private MySqlManagerImpl mySqlManager;
    private String sixPwd;
    private String userCoin;
    private int flag;
    private ImageView mIvComment, mIvShowMore;
    private double bidPoorMoney;
    private double dePoorMoney;
    private int bidCoin;
    private TextView mTvBidRecord;
    private CountdownView mCountdownView1;
    private String mObjectId;
    private List mPicList;
    private TextView mTvLoading1;
    private ScrollView mSvGood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_detail);
        initView();
        queryGoodData();
        mUserId = ((MyApplication) getApplication()).getObjectID();
        mySqlManager = new MySqlManagerImpl(this, this);
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void queryGoodData() {
        mObjectId = getIntent().getStringExtra("objectId");
        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.addWhereEqualTo("objectId", mObjectId);
        goodBmobQuery.include("Good_User");
        goodBmobQuery.findObjects(new FindListener<Good>() {
            @Override
            public void done(final List<Good> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 1) {
                        Bmob.getServerTime(new QueryListener<Long>() {
                            @Override
                            public void done(Long aLong, BmobException e) {
                                mGood = list.get(0);
                                long endTimeM = mGood.getGood_UpDateM();
                                endTime = endTimeM - (aLong * 1000L);
                                if (!mGood.getGood_User().getObjectId().equals(mUserId)) {
                                    initIsGoodStatus();
                                } else {
                                    mTvLoading.setVisibility(View.GONE);
                                }
                                initViewData();
                                initSliderLayout();
                            }
                        });
                    } else {
                        Toast.makeText(mContext, "未找到当前物品信息", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void initSliderLayout() {
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        HashMap<String, String> urlMaps = new HashMap<>();
        Log.d(TAG, "initView: " + mGood.getGood_Photos().size());
        mPicList = mGood.getGood_Photos();
        for (int i = 0; i < mPicList.size(); i++) {
            urlMaps.put("点击查看大图", mPicList.get(i).toString());
        }

        for (String name : urlMaps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(urlMaps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mContext = GoodDetailActivity.this;
        mTvGoodName = (TextView) findViewById(R.id.tv_good_name);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mCountdownView = (CountdownView) findViewById(R.id.cv_endtime);
        mCountdownView1 = (CountdownView) findViewById(R.id.cv_endtime1);
        mCountdownFive = (CountdownView) findViewById(R.id.cv_five);
        mTvGoodDescription = (TextView) findViewById(R.id.tv_good_description);
        mTvBid = (TextView) findViewById(R.id.tv_bid);
        mLlCv = (LinearLayout) findViewById(R.id.ll_good_detail_cv);
        mTvStartCoin = (TextView) findViewById(R.id.tv_start_coin);
        mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
        mTvLastTime = (TextView) findViewById(R.id.tv_last_time);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mTvNowCoin = (TextView) findViewById(R.id.tv_now_coin);
        mTvDeposit = (TextView) findViewById(R.id.tv_deposit);
        mTvBid.setOnClickListener(this);
        mTvDeposit.setOnClickListener(this);
        mIvComment = (ImageView) findViewById(R.id.iv_comment);
        mIvComment.setOnClickListener(this);
        mIvShowMore = (ImageView) findViewById(R.id.iv_show_more);
        mLlCv1 = (LinearLayout) findViewById(R.id.ll_cd);
        mIvShowMore.setOnClickListener(this);
        mTvBidRecord = (TextView) findViewById(R.id.tv_bid_record);
        mTvBidRecord.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvLoading1 = (TextView) findViewById(R.id.tv_loading1);
        mSvGood = (ScrollView) findViewById(R.id.sv_good);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bid:
                if (TextUtils.isEmpty(mUserId)) {
                    Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    BmobQuery<Bid> bidBmobQuery = new BmobQuery<>();
                    bidBmobQuery.addQueryKeys("Bid_Coin,createdAt");
                    bidBmobQuery.order("-createdAt");
                    bidBmobQuery.addWhereEqualTo("Bid_Good", mGood.getObjectId());
                    bidBmobQuery.findObjects(new FindListener<Bid>() {
                        @Override
                        public void done(final List<Bid> list, BmobException e) {
                            if (e == null) {
                                Bmob.getServerTime(new QueryListener<Long>() {
                                    @Override
                                    public void done(Long aLong, BmobException e) {
                                        if (e == null) {
                                            long leftTime;
                                            if (list.size() > 0) {
                                                long createdTime = StringToLongAll(list.get(0).getCreatedAt());
                                                long nowTime = aLong * 1000L;
                                                //30000： 30秒内所有不能再次出价。降低服务器并发及最后一位出价者的不确定性。
                                                leftTime = 30000 - (nowTime - createdTime);
                                            } else {
                                                leftTime = -1;
                                            }
                                            if (leftTime < 0) {
                                                changeTextViewVisibitity(2);
                                                //没有出价的情况下
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
                                                        } else {
                                                            if (mTvNowCoin.getText().equals("暂未出价")) {
                                                                if (Double.valueOf(mStrCoin) <= Double.valueOf(mGood.getGood_NowCoin())) {
                                                                    Toast.makeText(mContext, "请输入大于当前公益币", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    dialog.dismiss();
                                                                    bidCoin = Integer.parseInt(mStrCoin);
                                                                    mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null, null);
                                                                    flag = 2;
                                                                }
                                                            } else if (!mTvNowCoin.getText().equals("暂未出价")) {
                                                                if (Double.valueOf(mStrCoin) <= Double.valueOf(mTvNowCoin.getText().toString())) {
                                                                    Toast.makeText(mContext, "请输入大于当前公益币", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    dialog.dismiss();
                                                                    bidCoin = Integer.parseInt(mStrCoin);
                                                                    mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null, null);
                                                                    flag = 2;
                                                                }
                                                            } else {
                                                                Toast.makeText(GoodDetailActivity.this, "未知状态", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
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
                                                mTvNowCoin.setText(list.get(0).getBid_Coin());
                                                new AlertDialog.Builder(mContext)
                                                        .setTitle("提示")
                                                        .setMessage("当前有出价者，倒计时已刷新，请等待倒计时结束后再试！")
                                                        .setPositiveButton("确定", null)
                                                        .create()
                                                        .show();
                                            }
                                        } else {
                                            ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                                            changeTextViewVisibitity(3);
                                        }
                                    }
                                });
                            } else {
//                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                            }
                        }
                    });
                }
                break;
            case R.id.tv_deposit:
                if (TextUtils.isEmpty(mUserId)) {
                    Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("保证金为物品起步价的30%。\n" + "本物品起步价：" + mGood.getGood_StartCoin() + "公益币\n保证金：" +
                            depositCoin + "公益币");
                    builder.setPositiveButton("交保证金", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mySqlManager.queryUserCoinAndSixPwdByObjectId(null, null, null);
                            flag = 1;
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
                break;
            //评论按钮
            case R.id.iv_comment:
                Intent intent = new Intent(GoodDetailActivity.this, GoodDetailCommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Good", mGood);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_show_more:
                mCountdownView.setVisibility(View.GONE);
                mCountdownView1.setVisibility(View.VISIBLE);
                mIvShowMore.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_bid_record:
                Intent intent1 = new Intent(GoodDetailActivity.this, BidRecordActivity.class);
                intent1.putExtra("goodId", mGood.getObjectId());
                intent1.putExtra("flag", 0);
                startActivity(intent1);
                break;
        }
    }


    /**
     * 初始化数据
     * 从intent获取传过来的数据
     * 设置相应控件的数据显示
     */

    private void initViewData() {
        mTvLoading1.setVisibility(View.GONE);
        mSvGood.setVisibility(View.VISIBLE);
        mCountdownView1.start(endTime);
        mCountdownView.start(endTime);
        mTvGoodName.setText(mGood.getGood_Name());
        mTvGoodDescription.setText(mGood.getGood_Description());
        mTvStartCoin.setText(mGood.getGood_StartCoin());
        picSize = mGood.getGood_Photos().size();
        //保证金为公益币的30%
        depositCoin = (int) (Integer.parseInt(mGood.getGood_StartCoin()) * 0.3);

        mTvStartTime.setText(mGood.getCreatedAt());
        String good_startCoin = mGood.getGood_StartCoin();
        String good_nowCoin = mGood.getGood_NowCoin();
        if (TextUtils.equals(good_startCoin, good_nowCoin)) {
            Toast.makeText(mContext, "起步价" + good_startCoin + "现在出价" + good_nowCoin, Toast.LENGTH_SHORT).show();
            mTvNowCoin.setText("暂未出价");
            mTvLastTime.setVisibility(View.GONE);
        } else {
            mTvNowCoin.setText(mGood.getGood_NowCoin());
            mTvLastTime.setText(mGood.getUpdatedAt());
        }
        mTvUserName.setText(mGood.getGood_User().getUser_Nickname());
    }

    private void initIsGoodStatus() {
        if (endTime > 0) {
            isDeposit();
        } else {
            changeTextViewVisibitity(2);
            mTvBid.setClickable(false);
            mLlCv1.setVisibility(View.GONE);
            mTvBid.setText("出价结束");
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
                    if (list.size() == 0) {
                        //没有出价的
                        changeTextViewVisibitity(2);
                    } else {
                        getBmobServerTime(list.get(0).getCreatedAt());
                    }
                } else {
//                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void getBmobServerTime(final String createdAt) {
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {
                if (e == null) {
                    long createdTime = StringToLongAll(createdAt);
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
//                    Log.d("Kiuber_LOG", "fail: " + e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                    changeTextViewVisibitity(3);
                }
            }
        });
    }

    public long StringToLongAll(String time) {
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

    private void isDeposit() {
        BmobQuery<Deposit> depositBmobQuery = new BmobQuery<>();
        depositBmobQuery.addWhereEqualTo("D_User", mUserId);
        depositBmobQuery.include("D_User");
        depositBmobQuery.addWhereEqualTo("D_GoodId", mGood.getObjectId());
        depositBmobQuery.findObjects(new FindListener<Deposit>() {
            @Override
            public void done(List<Deposit> list, BmobException e) {
                if (e == null) {
                    int listSize = list.size();
                    if (listSize == 1) {
                        //支付保证金了
                        getLastBidUpdatedAt();
                    } else if (listSize == 0) {
                        //未支付保证金
                        changeTextViewVisibitity(1);
                        Toast.makeText(mContext, "未支付保证金", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "未知状态", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.d("Kiuber_LOG", "done: " + mUserId + "~" + mGood.getObjectId());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void saveDeposit2Bmob() {
        User_Profile user_profile = new User_Profile();
        user_profile.setObjectId(mUserId);

        Deposit deposit = new Deposit();
        deposit.setD_User(user_profile);
        deposit.setD_GoodId(mGood.getObjectId());
        deposit.setD_Coin(depositCoin + "");
        deposit.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    addDeposit(depositCoin, mGood.getObjectId(), mUserId);
                } else {
//                    Toast.makeText(mContext, "保证金支付失败", Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void addDeposit(int depositCoin, String objectId, String mUserId) {
        String webServiceIp = ((MyApplication) (getApplication())).getWebServiceIp();
        if (!(webServiceIp == null)) {
            String URL = webServiceIp + "AddDeposit";
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("D_Coin", String.valueOf(depositCoin))
                    .add("D_Good", objectId)
                    .add("D_User", mUserId)
                    .build();

            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "fail: " + e.getMessage());
//                            Toast.makeText(GoodDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String result = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("success")) {
                                Toast.makeText(mContext, "保证金支付成功", Toast.LENGTH_SHORT).show();
                                mTvDeposit.setVisibility(View.GONE);
                                mTvDeposit.setVisibility(View.GONE);
                                mTvBid.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "run: " + result);
//                                Toast.makeText(GoodDetailActivity.this, result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            MyApplication myApplication = (MyApplication) getApplication();
            myApplication.isLogin();
            Toast.makeText(this, "服务器地址获取失败，请重新试一次~", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeTextViewVisibitity(int flag) {
        switch (flag) {
            case 0:
                mTvLoading.setVisibility(View.GONE);
                mLlCv.setVisibility(View.VISIBLE);
                mTvDeposit.setVisibility(View.GONE);
                mTvBid.setVisibility(View.GONE);
                break;
            case 1:
                mTvLoading.setVisibility(View.GONE);
                mLlCv.setVisibility(View.GONE);
                mTvDeposit.setVisibility(View.VISIBLE);
                mTvBid.setVisibility(View.GONE);
                break;
            case 2:
                mTvLoading.setVisibility(View.GONE);
                mLlCv.setVisibility(View.GONE);
                mTvDeposit.setVisibility(View.GONE);
                mTvBid.setVisibility(View.VISIBLE);
                break;
            case 3:
                mTvLoading.setVisibility(View.GONE);
                mLlCv.setVisibility(View.GONE);
                mTvDeposit.setVisibility(View.GONE);
                mTvBid.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(mContext, DynamicPhotoShow.class);
        intent.putExtra("index", 0);
        intent.putStringArrayListExtra("list", (ArrayList<String>) mPicList);
        mContext.startActivity(intent);
        //设置切换动画
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void pay(boolean alipayOrWechatPay, double price, double allCoin, String changeCoin) {

    }

    @Override
    public void showSixPwdOnFinishInput(String sixPwd, int event) {
        if (event == 0) {
            mySqlManager.updateUserSixPwdByObjectId(sixPwd);
        } else if (event == 1) {
            if (flag == 1) {
                //保证金
                updateGoodDeposit();
                mySqlManager.updateUserCoinByObjectId((Double.valueOf(userCoin) - depositCoin) + "", depositCoin + "", 1);
            } else if (flag == 2) {
                //出价
                saveBid2Bmob(mGood.getObjectId(), bidCoin + "");
//                mySqlManager.updateUserCoinByObjectId((Double.valueOf(userCoin) - bidCoin) + "", bidCoin + "", -2);
            }
        }
    }

    private void updateGoodDeposit() {
        Good good = new Good();
        good.setFirstDeposit(true);
        good.update(mGood.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    saveDeposit2Bmob();

                } else {
                    ErrorCodeUtil.switchErrorCode(GoodDetailActivity.this, e.getErrorCode() + "");
                }
            }
        });
    }

    @Override
    public void updateUserCoinByObjectId(String sumCoin, String changeCoin, int flag) {
    }

    @Override
    public void queryUserCoinAndSixPwdByObjectId(final String userCoin, String sixPwd, String DonateCoin) {
        this.sixPwd = sixPwd;
        this.userCoin = userCoin;
        if (sixPwd.equals("0")) {
            mySqlManager = new MySqlManagerImpl(this, this, "设置新密码", "", "第一次");
            mySqlManager.showSixPwdOnFinishInput("", 0);
        } else {
            //用户公益币与保证金公益币差值
            final double dePoorCoin = Double.valueOf(userCoin) - depositCoin;
            final double bidPoorCoin = Double.valueOf(userCoin) - bidCoin;
            dePoorMoney = dePoorCoin / 10;
            bidPoorMoney = bidPoorCoin / 10;
            if (dePoorCoin < 0 || bidPoorCoin < 0) {

                if (mTvDeposit.getVisibility() == View.VISIBLE) {
                    new AlertDialog.Builder(GoodDetailActivity.this)
                            .setMessage("账号公益币不足" + depositCoin + "，是否立即充值？")
                            .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mySqlManager.pay(false,
                                            (-dePoorMoney) + (-dePoorMoney) * 0.05, Double.valueOf(userCoin) + depositCoin, depositCoin + "");
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else if (mTvBid.getVisibility() == View.VISIBLE) {
                    new AlertDialog.Builder(GoodDetailActivity.this)
                            .setMessage("账号公益币不足" + bidCoin + "，是否立即充值？")
                            .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mySqlManager.pay(false, (-bidPoorMoney) + (-bidPoorMoney) * 0.05, Double.valueOf(userCoin) + bidPoorCoin, +bidCoin + "");
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }

            } else {
                if (flag == 1) {
                    //交保证金
                    mySqlManager = new MySqlManagerImpl(this, this, "交保证金", depositCoin + "", "保证");
                    mySqlManager.showSixPwdOnFinishInput(sixPwd, 1);

                } else if (flag == 2) {
                    //出价
                    mySqlManager =
                            new MySqlManagerImpl(this, this, "出价", bidCoin + "", "出价");
                    mySqlManager
                            .showSixPwdOnFinishInput(sixPwd, 1);
                }
            }
        }
    }

    @Override
    public void updateUserSixPwdByObjectId(String sixPwd) {

    }

    private void saveBid2Bmob(final String goodId, final String bidCoin) {
        User_Profile user_profile = new User_Profile();
        user_profile.setObjectId(mUserId);
        Good good = new Good();
        good.setObjectId(goodId);

        Bid bid = new Bid();
        bid.setBid_User(user_profile);
        bid.setBid_Good(good);
        bid.setBid_Coin(bidCoin);
        bid.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    updateGood2Bmob(goodId, objectId);
                } else {
//                    Log.d("Kiuber_LOG", "done: " + e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void updateGood2Bmob(final String good_id, final String bid_id) {
        User_Profile user_profile = new User_Profile();
        user_profile.setObjectId(mUserId);
        Bid bid = new Bid();
        bid.setObjectId(bid_id);

        Good good = new Good();
        good.setGood_NowCoin(bidCoin + "");
        good.setGood_NowBidUser(user_profile);
        good.setGood_Bid(bid);
        good.setFirstDeposit(true);
        good.update(good_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    updateGood2MySql(good_id);
                } else {
//                    Log.d("Kiuber_LOG", "done: " + e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    private void updateGood2MySql(String goodId) {
        String webServiceIp = ((MyApplication) (mContext.getApplication())).getWebServiceIp();
        if (!(webServiceIp == null)) {
            String method = "BidGood";
            String URL = webServiceIp + method;
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("GoodObjectId", goodId)
                    .add("UserObjectId", mUserId)
                    .build();

            final Request request = new Request.Builder()
                    .url(URL)
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
                        @Override
                        public void run() {
                            if (result.contains("success")) {
                                Toast.makeText(mContext, "出价成功", Toast.LENGTH_SHORT).show();
                                getLastBidUpdatedAt();
                                mTvBid.setVisibility(View.GONE);
                                mLlCv.setVisibility(View.VISIBLE);
                                mTvNowCoin.setText(bidCoin + "");
                                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
                                String date = sDateFormat.format(new java.util.Date());
                                mTvLastTime.setText(date);
                                mTvLastTime.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("Kiuber_LOG", "fail: " + result);
                            }
                        }
                    });
                }
            });
        } else {
            MyApplication myApplication = (MyApplication) getApplication();
            myApplication.isLogin();
            Toast.makeText(this, "服务器地址获取失败，请重新试一次~", Toast.LENGTH_SHORT).show();
        }
    }
}
