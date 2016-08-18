package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.MySqlOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private List<MySqlOrder> mySqlOrders = new ArrayList<>();
    private Context mContext;
    private LinearLayout mLlItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        initData();
    }

    private void initView() {
        mContext = OrderActivity.this;
        mLvOrder = (ListView) findViewById(R.id.lv_order);
        mLvOrder.setOnItemClickListener(this);
    }

    private void initData() {
        queryOrderById();
    }

    private void queryOrderById() {
        String url = "http://123.206.89.67/WebService1.asmx/QueryOrderByObjectId";
        OkHttpClient okHttpClient = new OkHttpClient();
        String mStrObjectId = ((MyApplication) getApplication()).getObjectID();
        if (mStrObjectId != null) {
            RequestBody body = new FormBody.Builder()
                    .add("UserId", mStrObjectId)
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
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    mySqlOrder.setOrder_Name(jsonObject.getString("Order_Name"));
                                    mySqlOrder.setOrder_Coin(jsonObject.getString("Order_Coin"));
                                    mySqlOrder.setObject_Id(jsonObject.getString("Object_Id"));
                                    mySqlOrder.setOrder_Status(jsonObject.getString("Order_Status"));
                                    mySqlOrder.setOrder_PicURL(jsonObject.getString("Order_PicURL"));
                                    mySqlOrder.setOrder_Good(jsonObject.getString("Order_Good"));
                                    mySqlOrders.add(i, mySqlOrder);
                                }
                                mLvOrder.setAdapter(new MyOrderAdapter());
                                Log.d("Kiuber_LOG", "run: " + result);
                            } catch (JSONException e) {
                                Log.d("Kiuber_LOG", e.getMessage() + request);

                            }
                            Log.d("Kiuber_LOG", mySqlOrders.get(0).getOrder_Name() + ": " + "\n" + result);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, OrderDetailActivity.class);
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
                view = View.inflate(mContext, R.layout.item_listview_order, null);
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
                mTvName.setText(getItem(position).getOrder_Name());
                mTvCoin.setText(getItem(position).getOrder_Coin());
            }
        }
    }
}
