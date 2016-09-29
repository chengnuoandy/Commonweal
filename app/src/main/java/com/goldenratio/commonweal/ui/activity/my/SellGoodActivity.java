package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MySellGoodAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.ui.activity.GoodDetailActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class SellGoodActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.lv_sell_good)
    ListView mLvSellGood;
    @BindView(R.id.tv_no_order)
    TextView mTvNoOrder;

    private List<Good> mGoodList;
    private Long endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_good);
        ButterKnife.bind(this);

        initData();
        mLvSellGood.setOnItemClickListener(this);
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initData() {
        MyApplication myApplication = (MyApplication) getApplication();
        String uID = myApplication.getObjectID();
        if (uID.isEmpty()) {
            Toast.makeText(SellGoodActivity.this, "请先登陆！", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Good> query = new BmobQuery<>();
        User_Profile user = new User_Profile();
        user.setObjectId(uID);
        query.addWhereEqualTo("Good_User", user);
        query.order("-updatedAt");
        query.include("Good_User");
        query.findObjects(new FindListener<Good>() {
            @Override
            public void done(List<Good> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        mGoodList = list;
                        mLvSellGood.setAdapter(new MySellGoodAdapter(SellGoodActivity.this, list));
                    } else {
                        mTvNoOrder.setVisibility(View.VISIBLE);
                        Toast.makeText(SellGoodActivity.this, "未查询到相关订单", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mTvNoOrder.setVisibility(View.VISIBLE);
//                    Toast.makeText(SellGoodActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SellGoodActivity.this, GoodDetailActivity.class);
        intent.putExtra("objectId", mGoodList.get(position).getObjectId()  );
        startActivity(intent);
    }
}
