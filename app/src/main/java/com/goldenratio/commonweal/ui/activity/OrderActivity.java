package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlOrder;
import com.goldenratio.commonweal.bean.User_Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kiuber on 2016/8/17.
 */
public class OrderActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView mLvOrder;
    private List<Good> mGood;
    private TextView mTvLoading;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        initData();
    }

    private void initView() {
        mLvOrder = (ListView) findViewById(R.id.lv_order);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mLvOrder.setOnItemClickListener(this);
    }

    private void initData() {
//        queryOrderById();
        mUserId = ((MyApplication) (getApplication())).getObjectID();
        if (TextUtils.isEmpty(mUserId)) {
            mTvLoading.setText("请先登录");
        } else {
            User_Profile user_profile = new User_Profile();
            user_profile.setObjectId(mUserId);
            BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
            goodBmobQuery.addWhereEqualTo("Good_NowBidUser", mUserId);
            goodBmobQuery.findObjects(new FindListener<Good>() {
                @Override
                public void done(List<Good> list, BmobException e) {
                    if (list.size() != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            Good good = list.get(i);
                            Log.d("Kiuber_LOG", "done: " + good.getGood_UpDateM() + "-->" + System.currentTimeMillis());
                            if (good.getGood_UpDateM() > System.currentTimeMillis()) {
                                list.remove(i);
                            }
                        }
                        if (list.size() == 0) {
                            mTvLoading.setText("暂无关于您的订单！！");
                        } else if (list.size() != 0) {
                            mLvOrder.setVisibility(View.VISIBLE);
                            mTvLoading.setVisibility(View.GONE);
                            mGood = list;
                            mLvOrder.setAdapter(new MyOrderAdapter());
                        } else {
                            mTvLoading.setText("未知错误，请稍后再试！");
                        }
                    } else {
                        mTvLoading.setText("暂无关于您的订单！");
                    }
                }
            });
        }
    }

    private void queryOrderById() {
        String root = "http://123.206.89.67/WebService1.asmx/";
        String method = "QueryOrderByObjectId";
        String URL = root + method;
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
            call.enqueue(new okhttp3.Callback() {
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
                            JSONArray jsonArray = null;
                            MySqlOrder mySqlOrder = new MySqlOrder();
                            try {
                                jsonArray = new JSONArray(result);
                                if (jsonArray.length() == 0) {
                                    mTvLoading.setText("暂无订单");
                                } else {
                                    mLvOrder.setVisibility(View.VISIBLE);
                                    mTvLoading.setVisibility(View.GONE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        mySqlOrder.setOrder_Name(jsonObject.getString("Order_Name"));
                                        mySqlOrder.setOrder_Coin(jsonObject.getString("Order_Coin"));
                                        mySqlOrder.setObject_Id(jsonObject.getString("Object_Id"));
                                        mySqlOrder.setOrder_Status(jsonObject.getString("Order_Status"));
                                        mySqlOrder.setOrder_PicURL(jsonObject.getString("Order_PicURL"));
                                        mySqlOrder.setOrder_Good(jsonObject.getString("Order_Good"));
//                                        mySqlOrders.add(i, mySqlOrder);
                                    }

                                    mLvOrder.setAdapter(new MyOrderAdapter());
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderList", mGood.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyOrderAdapter extends BaseAdapter {

        private View view;

        @Override
        public int getCount() {
            return mGood.size();
        }

        @Override
        public Good getItem(int position) {
            return mGood.get(position);
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

        class ViewHolder {
            private TextView mTvName;
            private TextView mTvCoin;

            private void initView(View view) {
                mTvName = (TextView) view.findViewById(R.id.tv_name);
                mTvCoin = (TextView) view.findViewById(R.id.tv_coin);
            }

            private void initData(int position) {
                mTvName.setText(getItem(position).getGood_Name());
                mTvCoin.setText(getItem(position).getGood_NowCoin());
            }
        }
    }
}
