package com.goldenratio.commonweal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlOrder;
import com.goldenratio.commonweal.iview.IMySqlManager;
import com.goldenratio.commonweal.iview.impl.MySqlManagerImpl;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kiuber on 2016/8/17.
 */
public class OrderActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mLvOrder;
    private TextView mTvLoading;
    private String mUserId;
    List<MySqlOrder> mySqlOrders;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initView();
        initData();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initView() {
        mLvOrder = (ListView) findViewById(R.id.lv_order);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mLvOrder.setOnItemClickListener(this);
    }

    private void initData() {
        mContext = OrderActivity.this;
        mUserId = ((MyApplication) (getApplication())).getObjectID();
        if (TextUtils.isEmpty(mUserId)) {
            mTvLoading.setText("请先登录");
        } else {
            queryOrderById();
        }
    }

    private void queryOrderById() {
        String webServiceIp = ((MyApplication) (getApplication())).getWebServiceIp();

        if (!(webServiceIp == null)) {
            String method = "QueryOrderByObjectId";
            String URL = webServiceIp + method;
            OkHttpClient okHttpClient = new OkHttpClient();
            String mStrObjectId = ((MyApplication) getApplication()).getObjectID();
            if (mStrObjectId != null) {
                RequestBody body = new FormBody.Builder()
                        .add("UserId", mStrObjectId)
                        .build();

                final Request request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        final String e1 = e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (e1.contains("connect")) {
                                    mTvLoading.setText("连接服务器错误");
                                }
                                Toast.makeText(OrderActivity.this, e1, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mySqlOrders = new ArrayList<>();
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(result);
                                    if (jsonArray.length() == 0) {
                                        mTvLoading.setText("暂无订单");
                                    } else {
                                        mLvOrder.setVisibility(View.VISIBLE);
                                        mTvLoading.setVisibility(View.GONE);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            final MySqlOrder mySqlOrder = new MySqlOrder();
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            mySqlOrder.setObject_Id(jsonObject.getString("O_ID"));

                                            String id = jsonObject.getString("O_Good");

                                            mySqlOrder.setOrder_Code(jsonObject.getString("O_Code"));
                                            mySqlOrder.setOrder_Company(jsonObject.getString("O_Company"));
                                            mySqlOrders.add(i, mySqlOrder);

                                            BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
                                            goodBmobQuery.addWhereEqualTo("objectId", id);
                                            goodBmobQuery.include("Good_NowBidUser");
                                            goodBmobQuery.findObjects(new FindListener<Good>() {
                                                @Override
                                                public void done(List<Good> list, BmobException e) {
                                                    if (e == null) {
                                                        if (list.size() == 0) {
                                                            Toast.makeText(OrderActivity.this, "未找到物品信息", Toast.LENGTH_SHORT).show();
                                                        } else if (list.size() == 1) {
                                                            mySqlOrder.setOrder_Good(list.get(0));
                                                            mySqlOrder.setOrder_User(list.get(0).getGood_NowBidUser());
                                                            mLvOrder.setAdapter(new MyOrderAdapter());
                                                        }
                                                    } else {
                                                        ErrorCodeUtil.switchErrorCode(OrderActivity.this, e.getErrorCode() + "");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    Log.d("Kiuber_LOG", "run: " + result);
                                } catch (JSONException e) {
                                    Toast.makeText(OrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        } else {
            Toast.makeText(this, "Ip地址获取失败，请稍后重试！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderList", mySqlOrders.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    class MyOrderAdapter extends BaseAdapter {

        private View view;

        @Override
        public int getCount() {
            return mySqlOrders.size();
        }

        @Override
        public MySqlOrder getItem(int position) {
            return mySqlOrders.get(position);
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
                view = View.inflate(OrderActivity.this, R.layout.item_listview_order, null);
                convertView = view;
                viewHolder.initView(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.initData(position);

            return convertView;
        }

        class ViewHolder implements IMySqlManager {
            private TextView mTvName;
            private TextView mTvCoin;
            private TextView mTvReceive;
            private ImageView mIvGood;
            private int flag;
            private MySqlManagerImpl mySqlManager;

            private void initView(View view) {
                mIvGood = (ImageView) view.findViewById(R.id.iv_good);
                mTvName = (TextView) view.findViewById(R.id.tv_name);
                mTvCoin = (TextView) view.findViewById(R.id.tv_coin);
                mTvReceive = (TextView) view.findViewById(R.id.tv_receive);
            }

            private void initData(int position) {
                Good good = getItem(position).getOrder_Good();
                Glide.with(OrderActivity.this).load(good.getGood_Photos().get(0)).into(mIvGood);
                mTvName.setText(good.getGood_Name());
                mTvCoin.setText(good.getGood_NowCoin());
                if (good.getGood_Status().equals(3)) {
                    mTvReceive.setVisibility(View.VISIBLE);
                }
                setOnClickListener(position);
            }

            private void setOnClickListener(final int position) {
                mTvReceive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flag = position;
                        mySqlManager = new MySqlManagerImpl(OrderActivity.this, ViewHolder.this);
                        mySqlManager.queryUserCoinAndSixPwdByObjectId(null,null, null);
                    }
                });
                mIvGood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(OrderActivity.this, GoodDetailActivity.class);
                        intent1.putExtra("objectId", mySqlOrders.get(position).getOrder_Good().getObjectId());
                        startActivity(intent1);
                    }
                });
            }


            @Override
            public void pay(boolean alipayOrWechatPay, double price, double allCoin, String changeCoin) {
            }

            @Override
            public void showSixPwdOnFinishInput(String sixPwd, int event) {
                if (event == 1) {
                    confirmReceive();
                }
            }

            private void confirmReceive() {
                String webServiceIp = ((MyApplication) (mContext.getApplicationContext())).getWebServiceIp();
                if (!(webServiceIp == null)) {
                    String url = webServiceIp + "ConfirmReceive";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    final MySqlOrder mySqlOrder = mySqlOrders.get(flag);
                    int nowCoin = Integer.parseInt(mySqlOrder.getOrder_Good().getGood_NowCoin());
                    int donateCoin = (int) (nowCoin * mySqlOrder.getOrder_Good().getGood_DonationRate() * 0.01);
                    Toast.makeText(mContext, donateCoin + "", Toast.LENGTH_SHORT).show();
                    int userCoin = nowCoin - donateCoin;
                    RequestBody body = new FormBody.Builder()
                            .add("userId", mySqlOrder.getOrder_Good().getGood_User().getObjectId())
                            .add("goodId", mySqlOrder.getOrder_Good().getObjectId())
                            .add("userCoin", userCoin + "")
                            .add("donateCoin", donateCoin + "")
                            .build();

                    final Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            final String e1 = e.getMessage();
                            OrderActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, e1, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String result = response.body().string();
                            OrderActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //更新bmob的逻辑
                                    Good good = new Good();
                                    good.setGood_Status(4);
                                    good.update(mySqlOrder.getOrder_Good().getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(mContext, "确认收货成功", Toast.LENGTH_SHORT).show();
                                                mTvReceive.setVisibility(View.GONE);
                                            } else {
                                                //                                    Toast.makeText(mContext, "更新数据失败", Toast.LENGTH_SHORT).show();
                                                ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Ip地址获取失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void updateUserCoinByObjectId(String sumCoin, String changeCoin, int PRName) {
            }

            @Override
            public void queryUserCoinAndSixPwdByObjectId(String mStrUserCoin, String sixPwd, String DonateCoin) {
                mySqlManager = new MySqlManagerImpl(OrderActivity.this, ViewHolder.this, "确认收货", "", "收货");
                mySqlManager.showSixPwdOnFinishInput(sixPwd, 1);
            }

            @Override
            public void updateUserSixPwdByObjectId(String sixPwd) {

            }
        }
    }
}
