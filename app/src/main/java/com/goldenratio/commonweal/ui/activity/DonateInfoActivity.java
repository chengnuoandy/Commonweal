package com.goldenratio.commonweal.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.DonateInfoListAdapter;
import com.goldenratio.commonweal.bean.Donate_Info;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class DonateInfoActivity extends BaseActivity {


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
    @BindView(R.id.no_ranking)
    TextView mNoRanking;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_info);
        ButterKnife.bind(this);

        String helpID = getIntent().getStringExtra("help_id");
        getDonateInfoFromBmob(helpID);

        new ImmersiveUtil(this, R.color.white, true);
    }

    private void getDonateInfoFromBmob(String helpID) {
        final String ID = ((MyApplication) getApplication()).getObjectID();
        showProgressDialog();
        BmobQuery<Donate_Info> query = new BmobQuery<Donate_Info>();
        query.addWhereEqualTo("Help_ID", helpID);
        query.include("User_Info");
        query.order("-Donate_Coin");
        query.findObjects(new FindListener<Donate_Info>() {
            @Override
            public void done(List<Donate_Info> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        User_Profile u = null;
                        int i;
                        if (!ID.equals("")) {
                            for (i = 0; i < list.size(); i++) {
                                u = list.get(i).getUser_Info();
                                if ((u.getObjectId()).equals(ID)) {
                                    mTvMyRanking.setText(String.valueOf("第" + (i + 1) + "名"));
                                    break;
                                } else {
                                    mTvMyRanking.setText("无排名");
                                }
                            }
                            if (u != null) {
                                Picasso.with(getApplicationContext()).load(u.getUser_image_hd()).into(mCivDonateAvatar);
                                mTvDonateName.setText(u.getUser_Nickname());
                                mTvDonateCoin.setText("捐赠" + list.get(i).getDonate_Coin() + "公益币");
                            }
                            mRlDonate.setVisibility(View.VISIBLE);
                        }
                        mLvDonate.setAdapter(new DonateInfoListAdapter(list, getApplicationContext()));
                    } else {
                        mNoRanking.setVisibility(View.VISIBLE);
                    }
                    setItemClick(list);
                } else {
                    Log.i("lxt", "done: " + e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
                closeProgressDialog();
            }
        });
    }

    private void setItemClick(final List<Donate_Info> donateinfosList) {
        mLvDonate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User_Profile userList = donateinfosList.get(position).getUser_Info();
                MyApplication myApplication = (MyApplication) getApplication();
                String mStrObjectId = myApplication.getObjectID();
                if (!userList.getObjectId().equals(mStrObjectId)) {
                    List<String> attenList;
                    attenList = userList.getUser_Attention();
                    int isHas = -1;
                    if (attenList != null)
                        isHas = attenList.indexOf(userList.getObjectId());
                    Intent intent = new Intent(getApplication(), StarInfoActivity.class);
                    intent.putExtra("ishas", isHas != -1);
                    intent.putExtra("id", userList.getObjectId());
                    intent.putExtra("autograph", userList.getUser_Autograph());
                    intent.putExtra("nickName", userList.getUser_Nickname());
                    intent.putExtra("isv", userList.isUser_IsV());
                    intent.putExtra("Avatar", userList.getUser_image_hd());
                    startActivity(intent);
                } else {
                    Toast.makeText(myApplication, "不要再点了，这里真的什么都没有~", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.iv_donate_back, R.id.rl_donate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_donate_back:
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
            mPd.setCancelable(true);
            mPd.show();
        }
    }

    @OnClick(R.id.iv_donate_back)
    public void onClick() {
        finish();
    }
}
