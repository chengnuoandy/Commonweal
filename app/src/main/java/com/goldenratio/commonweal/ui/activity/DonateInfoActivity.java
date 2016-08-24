package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Donate_Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class DonateInfoActivity extends Activity {

    @BindView(R.id.iv_attention_back)
    ImageView mIvAttentionBack;
    @BindView(R.id.tv_attentionTitle)
    TextView mTvAttentionTitle;
    @BindView(R.id.civ_donate_avatar)
    CircleImageView mCivDonateAvatar;
    @BindView(R.id.tv_donate_name)
    TextView mTvDonateName;
    @BindView(R.id.tv_donate_coin)
    TextView mTvDonateCoin;
    @BindView(R.id.tv_ranking)
    TextView mTvRanking;
    @BindView(R.id.rl_donate)
    RelativeLayout mRlDonate;
    @BindView(R.id.lv_donate)
    ListView mLvDonate;

    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_info);
        ButterKnife.bind(this);

        getDonateInfoFromBmob();
    }

    private void getDonateInfoFromBmob() {
        showProgressDialog();
        BmobQuery<Donate_Info> query = new BmobQuery<Donate_Info>();
        query.sum(new String[]{"Donate_Coin"});    // 统计总得分
        query.groupby(new String[]{"User_ID"});// 按照时间分组
        query.order("-createdAt");                  // 降序排列
        query.setHasGroupCount(true);              // 统计每一天有多少个玩家的得分记录，默认不返回分组个数
        query.findStatistics(Donate_Info.class, new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    if (ary != null) {
                        int length = ary.length();
                        Log.i("ary", ary.length() + "done: " + ary);
                        try {
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = ary.getJSONObject(i);
                                int playscore = obj.getInt("_sumDonate_Coin");
                                String createDate = obj.getString("User_ID");
                                int count = obj.getInt("_count");       //setHasGroupCount设置为true时，返回的结果中含有"_count"字段
                                Log.i("jsondone", "done: " + "\"公益币总数总得分：\" + playscore + \",总共统计了\"\n" +
                                        +count + "条记录,统计时间 = \" + createDate");
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Toast.makeText(DonateInfoActivity.this, "查询成功，无数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("done", "done: " + e.getMessage());
                }
                closeProgressDialog();
            }
        });
    }

    @OnClick({R.id.iv_attention_back, R.id.rl_donate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_attention_back:
                finish();
                break;
            case R.id.rl_donate:
                break;
        }
    }

    private void closeProgressDialog() {
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
            mPd = null;
        }
    }

    private void showProgressDialog() {
        if (mPd == null) {
            mPd = new ProgressDialog(this);
            mPd.setMessage("加载中");
            mPd.setCancelable(false);
            mPd.show();
        }
    }
}
