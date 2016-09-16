package com.goldenratio.commonweal.iview;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.util.ArrayList;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by Administrator on 2016/8/17.
 */

public class CommentBase extends BaseActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private ListView mListView;
    private EditText edt_reply;
    private Button btn_reply;

    private String mStrObjectId;
    private ArrayList arrayList;

    private IComment mIComment;
    /**
     * 下拉刷新
     */
    private BGARefreshLayout mBGARefreshLayout;


    public void setData(IComment comment) {
        mIComment = comment;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        show();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void initView() {
        findViewById(R.id.ibtn_send).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv_comment_one);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        View view = View.inflate(getApplicationContext(), R.layout.item_comment_one, null);
        view.findViewById(R.id.tv_user_reply).setOnClickListener(this);

        mBGARefreshLayout = (BGARefreshLayout) findViewById(R.id.rl_BGA_refresh);
        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, false);
        // 设置下拉刷新和上拉加载更多的风格
        mBGARefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    //发送按钮
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_send:
                showDialog(-1);
                break;
        }
    }

    //弹出对话框
    protected Dialog onCreateDialog(final int id) {
        final Dialog customDialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.dialog_comment, null);
        edt_reply = (EditText) mView.findViewById(R.id.edt_comments);
        btn_reply = (Button) mView.findViewById(R.id.btn_send);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(mView);
        customDialog.show();
        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果评论内容不为空 就将数据添加到云端并由Adapter显示在 一级评论上
                if (!(edt_reply.getText().toString().trim().isEmpty())) {

//                    Toast.makeText(getApplication(), "" + edt_reply.getText(), Toast.LENGTH_SHORT).show();
                    //获取本地数据库
                    UserDao userDao = new UserDao(CommentBase.this);
                    Cursor cursor = userDao.query("select * from User_Profile");
                    while (cursor.moveToNext()) {
                        int nameColumnIndex = cursor.getColumnIndex("objectId");
                        mStrObjectId = cursor.getString(nameColumnIndex);
                    }
                    cursor.close();
                    //判断用户是否登录
                    if (mStrObjectId != null) {
                        //回调方法  发送评论
                        mIComment.upComment(mStrObjectId, edt_reply.getText().toString());
                        edt_reply.setText("");
                        customDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "先登陆~评论更加美味", Toast.LENGTH_SHORT).show();
                    }

                    show();
                } else {
                    Toast.makeText(getApplicationContext(), "您什么也没有评论呦~", Toast.LENGTH_SHORT).show();
                    edt_reply.setText("");
                    customDialog.dismiss();
                }
            }
        });

        return customDialog;
    }

    private void show() {
        //回调方法----拉取评论
        mIComment.Show(mListView, mBGARefreshLayout);
    }

    /**
     * 下拉刷新逻辑
     *
     * @param refreshLayout 刷新布局控件
     */
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //重新装填数据
        if (arrayList != null) {
            arrayList.clear();
            show();
        } else {
            show();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
