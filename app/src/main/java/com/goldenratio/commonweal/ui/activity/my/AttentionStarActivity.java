package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.AttentionStarListAdapter;
import com.goldenratio.commonweal.bean.U_Attention;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.ui.activity.StarInfoActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AttentionStarActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.iv_donate_back)
    ImageView mIvAttentionBack;
    @BindView(R.id.lv_attention)
    ListView mLvAttention;
    @BindView(R.id.tv_attentionTitle)
    TextView mTvAttentionTitle;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;

    private List<U_Attention> AttentionList;
    private ProgressDialog mPd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_star);
        ButterKnife.bind(this);

        AttentionList = new ArrayList<>();

        boolean isAttention = getIntent().getBooleanExtra("is_attention", false);
        if (!isAttention)
            mTvAttentionTitle.setText("我的粉丝");
        getAttentionInfoFromBmob(isAttention);
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void getAttentionInfoFromBmob(final boolean isAttention) {
        String where = isAttention ? "U_ID" : "Star_ID";
        String include = isAttention ? "Star_Info" : "User_Info";
        showProgressDialog();
        String objectID = ((MyApplication) getApplication()).getObjectID();
        BmobQuery<U_Attention> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.addWhereEqualTo(where, objectID);
        query.include(include);
        query.findObjects(new FindListener<U_Attention>() {
            @Override
            public void done(List<U_Attention> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        AttentionList = list;
                        Toast.makeText(AttentionStarActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
                        mLvAttention.setAdapter(new AttentionStarListAdapter(list, isAttention, AttentionStarActivity.this));
                    } else
                        mTvNoData.setVisibility(View.VISIBLE);
                    Log.i("查询信息成功", "done: " + list);
                } else {
                    mTvNoData.setVisibility(View.VISIBLE);
//                    Toast.makeText(AttentionStarActivity.this, "获取信息失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
                closeProgressDialog();
            }
        });
    }

    @OnClick(R.id.iv_donate_back)
    public void onClick() {
        finish();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyApplication myApplication = (MyApplication) ((Activity) this).getApplication();
        String mStrObjectId = myApplication.getObjectID();
        if (!AttentionList.get(position).getStar_Info().getObjectId().equals(mStrObjectId)) {
            List<String> attenList;
            attenList = AttentionList.get(position).getStar_Info().getUser_Attention();
            int isHas = -1;
            if (attenList != null)
                isHas = attenList.indexOf(AttentionList.get(position).getStar_Info().getObjectId());
            Intent intent = new Intent(this, StarInfoActivity.class);
            intent.putExtra("ishas", isHas != -1);
            intent.putExtra("id", AttentionList.get(position).getStar_Info().getObjectId());
            intent.putExtra("autograph", AttentionList.get(position).getStar_Info().getUser_Autograph());
            intent.putExtra("nickName", AttentionList.get(position).getStar_Info().getUser_Nickname());
            intent.putExtra("isv", AttentionList.get(position).getStar_Info().isUser_IsV());
            intent.putExtra("Avatar", AttentionList.get(position).getStar_Info().getUser_image_hd());
            this.startActivity(intent);
        } else {
            Toast.makeText(myApplication, "不要再点了，这里真的什么都没有~", Toast.LENGTH_SHORT).show();
        }
    }
}
