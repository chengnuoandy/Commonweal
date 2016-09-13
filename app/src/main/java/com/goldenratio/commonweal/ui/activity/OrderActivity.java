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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.MySqlOrder;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
public class OrderActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mLvOrder;
    private TextView mTvLoading;
    private String mUserId;
    List<MySqlOrder> mySqlOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
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
                                            mySqlOrder.setOrder_Status(jsonObject.getString("O_Status"));
                                            mySqlOrder.setOrder_Good(jsonObject.getString("O_Good"));
                                            mySqlOrders.add(i, mySqlOrder);

                                            BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
                                            goodBmobQuery.addWhereEqualTo("objectId", mySqlOrder.getOrder_Good());
                                            goodBmobQuery.findObjects(new FindListener<Good>() {
                                                @Override
                                                public void done(List<Good> list, BmobException e) {
                                                    if (e == null) {
                                                        if (list.size() == 0) {
                                                            Toast.makeText(OrderActivity.this, "未找到物品信息", Toast.LENGTH_SHORT).show();
                                                        } else if (list.size() == 1) {
                                                            mySqlOrder.setOrder_Coin(list.get(0).getGood_NowCoin());
                                                            mySqlOrder.setOrder_PicURL(list.get(0).getGood_Photos());
                                                            mySqlOrder.setOrder_Name(list.get(0).getGood_Name());
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

        class ViewHolder {
            private ImageView mIvGood;
            private TextView mTvName;
            private TextView mTvCoin;

            private void initView(View view) {
                mIvGood = (ImageView) view.findViewById(R.id.iv_good);
                mTvName = (TextView) view.findViewById(R.id.tv_name);
                mTvCoin = (TextView) view.findViewById(R.id.tv_coin);
            }

            private void initData(int position) {
                Glide.with(OrderActivity.this).load(mySqlOrders.get(position).getOrder_PicURL().get(0)).into(mIvGood);
                mTvName.setText(getItem(position).getOrder_Name());
                mTvCoin.setText(getItem(position).getOrder_Coin());
            }
        }
    }
}
