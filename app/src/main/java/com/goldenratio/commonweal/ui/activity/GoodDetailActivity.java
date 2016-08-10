package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.iwgang.countdownview.CountdownView;

public class GoodDetailActivity extends Activity {

    private CountdownView mCountdownView;
    private CountdownView mCountdownFive;
    private Long endTime;
    private Good mGood;
    private TextView mTvGoodName;
    private TextView mTvGoodDescription;
    private TextView mTvUserName;
    private GridView mGvPic;
    private TextView mTvBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_detail);
        initView();
        initData();
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
        mCountdownFive.start(endTime);

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
        mTvBuy = (TextView) findViewById(R.id.tv_auction);
        mTvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog mCameraDialog = new Dialog(GoodDetailActivity.this, R.style.my_dialog);
                LinearLayout root = (LinearLayout) LayoutInflater.from(GoodDetailActivity.this).inflate(
                        R.layout.dialog_good_auction, null);
                mCameraDialog.setContentView(root);

                Window dialogWindow = mCameraDialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画

                WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                lp.x = 0; // 新位置X坐标
                lp.y = -20; // 新位置Y坐标
                lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
                root.measure(0, 0);
                lp.height = root.getMeasuredHeight();
                lp.alpha = 9f; // 透明度
                dialogWindow.setAttributes(lp);
                mCameraDialog.show();

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                final EditText mEtCoin = (EditText) root.findViewById(R.id.et_coin);
                root.findViewById(R.id.tv_auction).setOnClickListener(new View.OnClickListener() {

                    private String mStrObjectId;

                    @Override
                    public void onClick(View v) {

                        UserDao userDao = new UserDao(GoodDetailActivity.this);
                        Cursor cursor = userDao.query("select * from User_Profile");
                        while (cursor.moveToNext()) {
                            int nameColumnIndex = cursor.getColumnIndex("objectId");
                            mStrObjectId = cursor.getString(nameColumnIndex);
                        }
                        cursor.close();

                        User_Profile user_profile = new User_Profile();
                        user_profile.setObjectId(mStrObjectId);

                        Good good = new Good();
                        good.setGood_NowCoin(Integer.parseInt(mEtCoin.getText().toString()));
                        good.setGood_LatestAucUser(user_profile);
                        good.update(GoodDetailActivity.this, mGood.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                User_Profile user_profile1 = new User_Profile();
                                user_profile1.setObjectId(mStrObjectId);
                                Good good1 = new Good();
                                good1.setObjectId(mGood.getObjectId());

                                Bid bid = new Bid();
                                bid.setBid_Coin(mEtCoin.getText().toString());
                                bid.setBid_User(user_profile1);
                                bid.setBid_Good(good1);
                                bid.save(GoodDetailActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(GoodDetailActivity.this, "出价成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
                    }
                });

            }
        });
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
