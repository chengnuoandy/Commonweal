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
import com.goldenratio.commonweal.adapter.DonateInfoListAdapter;
import com.goldenratio.commonweal.bean.Donate_Info;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
    @BindView(R.id.tv_my_ranking)
    TextView mTvMyRanking;
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

        String helpID = getIntent().getStringExtra("help_id");
        getDonateInfoFromBmob(helpID);
    }

    private void getDonateInfoFromBmob(String helpID) {
        showProgressDialog();
        BmobQuery<Donate_Info> query = new BmobQuery<Donate_Info>();
        query.addWhereEqualTo("Help_ID", helpID);
        query.order("-Donate_Coin");
        query.findObjects(new FindListener<Donate_Info>() {
            @Override
            public void done(List<Donate_Info> list, BmobException e) {
                Log.i("lxt", "done: " + list.get(0).getObjectId());
                if (e == null) {
                    mLvDonate.setAdapter(new DonateInfoListAdapter(list, getApplicationContext()));
                } else
                    Toast.makeText(DonateInfoActivity.this, "获取捐赠信息失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
