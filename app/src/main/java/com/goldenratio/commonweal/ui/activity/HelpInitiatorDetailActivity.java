package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.HelpInitiatorListAdapter;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Initiator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HelpInitiatorDetailActivity extends Activity implements AdapterView.OnItemClickListener{

    private List<Help> mHelpList;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.iv_pic)
    ImageView mIvPic;
    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.tv_no)
    TextView mTvNo;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.lv_helplist)
    ListView mLvHelplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_initiator_detail);
        ButterKnife.bind(this);

        mLvHelplist.setOnItemClickListener(this);

        initData();
    }

    private void initData() {
        String str = getIntent().getStringExtra("name");
        mTvName.setText(str);
        BmobQuery<Help> query = new BmobQuery<>();
        query.addWhereEqualTo("Help_Initiator", str);
        query.order("-updatedAt");
        query.include("InitiatorInfo");
        query.findObjects(new FindListener<Help>() {
            @Override
            public void done(List<Help> list, BmobException e) {
                if (e == null && (list.size() > 0)) {
                    mHelpList = list;
                    //获取发起者详细信息
                    Help_Initiator initiator = list.get(0).getInitiatorInfo();
                    mTvDesc.setText(initiator.getInitiator_desc());
                    mTvNo.setText("共发起" + list.size() + "个项目");
                    Glide.with(HelpInitiatorDetailActivity.this)
                            .load(initiator.getUser_img())
                            .into(mIvPic);
                    mLvHelplist.setAdapter(new HelpInitiatorListAdapter(HelpInitiatorDetailActivity.this, list));
                    //解决冲突问题
                    setListViewHeightBasedOnChildren(mLvHelplist);
                } else {
                    Toast.makeText(HelpInitiatorDetailActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    /**
     * 解决listview和scroll冲突
     * @param listView listview控件
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    /**
     * 发起者所有项目的list的点击事件
     * 跳转到相应的详情页
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, HelpDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("HelpList", mHelpList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
