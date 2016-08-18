package com.goldenratio.commonweal.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.U_Attention;
import com.goldenratio.commonweal.bean.User_Profile;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class StarInfoActivity extends AppCompatActivity {

    @BindView(R.id.civ_star_avatar)
    CircleImageView mCivStarAvatar;
    @BindView(R.id.tv_star_name)
    TextView mTvStarName;
    @BindView(R.id.tv_attention)
    TextView mTvAttention;

    private String starID;

    private boolean isHasAttention;
    private U_Attention mUAttention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_info);
        ButterKnife.bind(this);

        getData();
        isHasAttention = getIntent().getBooleanExtra("ishas", false);
        starID = getIntent().getStringExtra("id");
        String nickName = getIntent().getStringExtra("nickName");
        String avatar = getIntent().getStringExtra("Avatar");
        mTvStarName.setText(nickName);
        Picasso.with(this).load(avatar).into(mCivStarAvatar);
    }

    @OnClick({R.id.civ_star_avatar, R.id.tv_attention})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_star_avatar:
                break;
            case R.id.tv_attention:
                if (isHasAttention) {
                    removeDataToBmob();
                } else
                    addDataToBmob();
                break;
        }

    }


    private void getData() {
        BmobQuery<U_Attention> query = new BmobQuery<U_Attention>();
        User_Profile u = new User_Profile();
        u.setObjectId(starID);
        query.addQueryKeys("Star_ID");
        query.addWhereEqualTo("Star_ID", u);
        query.findObjects(new FindListener<U_Attention>() {

            @Override
            public void done(List<U_Attention> object, BmobException e) {
                if (e == null && object != null) {
                    isHasAttention = true;
                    Log.i("bmob", "查询成功：共" + object.size() + "条数据。");
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }

        });
    }

   /* private void addDataToBmob() {
        String ID = ((MyApplication) getApplication()).getObjectID();
        User_Profile u = new User_Profile();
        u.setObjectId(ID);
        u.addUnique("User_Attention", starID);
        u.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(StarInfoActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    mTvAttention.setText("已关注");
                } else {
                    Toast.makeText(StarInfoActivity.this, "关注失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    private void addDataToBmob() {
        mUAttention = new U_Attention();
        String ID = ((MyApplication) getApplication()).getObjectID();
        mUAttention.setU_ID(ID);
        User_Profile u = new User_Profile();
        u.setObjectId(starID);
        mUAttention.setStar_ID(u);
        mUAttention.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(StarInfoActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    isHasAttention = true;
                } else
                    Toast.makeText(StarInfoActivity.this, "关注失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeDataToBmob() {
        mUAttention.remove(mUAttention.getObjectId());
        mUAttention.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(StarInfoActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
                    isHasAttention = false;
                    mTvAttention.setText("关注");
                } else {
                    Toast.makeText(StarInfoActivity.this, "取消关注失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
