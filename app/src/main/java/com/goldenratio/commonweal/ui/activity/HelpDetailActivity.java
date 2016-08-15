package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.HelpCommentAdapter;
import com.goldenratio.commonweal.util.ShareUtils;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Comment;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.util.CommentUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/6/22.
 */

public class HelpDetailActivity extends Activity implements View.OnClickListener {


    private Help mHelp;
    private ImageView mIvTop;
    private TextView mTvTitle;
    private TextView mTvInitiator;
    private TextView mTvContent;
    private TextView mTvSmile;
    private TextView mTvAll;
    private EditText mEtSpeak;
    private LinearLayout mLayout;
    private TextView mTvExecute;
    private ListView mLvSpeak;

    private String mStrObjectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mHelp = (Help) getIntent().getSerializableExtra("HelpList");
        DisplayMetrics dm = new DisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;

        initView();
        shouComment();


//        Picasso.with(HelpDetailActivity.this).load(mHelp.getHelp_Top_pic()).into(mIvTop);//.override(screenWidth,200)
        Glide.with(this).load(mHelp.getHelp_Pic()).into(mIvTop);
        mTvTitle.setText(mHelp.getHelp_Title());
        mTvInitiator.setText(mHelp.getHelp_Initiator());
        mTvExecute.setText(mHelp.getHelp_Execute());
        mTvSmile.setText(mHelp.getHelp_Smile());
        mTvContent.setText(mHelp.getHelp_Content());
        Close();

    }

    //失去焦点
    private void Close() {
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭软键盘？？
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(HelpDetailActivity.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_rank).setOnClickListener(this);
        mIvTop = (ImageView) findViewById(R.id.iv_top);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvInitiator = (TextView) findViewById(R.id.tv_initiator);
        mTvExecute = (TextView) findViewById(R.id.tv_execute);
        mTvSmile = (TextView) findViewById(R.id.smile);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mTvAll = (TextView) findViewById(R.id.tv_all);
        mEtSpeak = (EditText) findViewById(R.id.et_speak);
        mLayout = (LinearLayout) findViewById(R.id.help_layout);
//        mBten = (Button) findViewById(R.id.btn_speak);
        mLvSpeak = (ListView) findViewById(R.id.lv_helpspeak);


        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_comment).setOnClickListener(this);
        findViewById(R.id.tv_donate).setOnClickListener(this);
        findViewById(R.id.btn_speak).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_back:
                finish();
                break;

            //奖杯图标 查看排名
            case R.id.iv_rank:
                break;

            //分享
            case R.id.iv_share:
                ShareUtils shareUtils = new ShareUtils();
                shareUtils.showShare(this);
                break;

            //评论
            case R.id.iv_comment:
                mEtSpeak.requestFocus();
                mEtSpeak.setFocusable(true);
                mEtSpeak.setFocusableInTouchMode(true);
                break;

            //捐赠
            case R.id.tv_donate:
                Intent i = new Intent(HelpDetailActivity.this, HelpDonateActivity.class);
                startActivity(i);
                break;


            //发送按钮
            case R.id.btn_speak:
                //    Toast.makeText(HelpDetailActivity.this, "" +  mEtSpeak.getText().toString(), Toast.LENGTH_SHORT).show();
                //获取发送信息，和发送人信息
                comment();
                //发送结束后EditText中的内容删除
                mEtSpeak.setText("");
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(HelpDetailActivity.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
        }
    }


    /**
     * 评论功能
     * CharmNight
     */
    private void comment() {

        //获取本地数据库
        UserDao userDao = new UserDao(HelpDetailActivity.this);
        Cursor cursor = userDao.query("select * from User_Profile");
        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex("objectId");
            mStrObjectId = cursor.getString(nameColumnIndex);
        }
        cursor.close();

        if (mStrObjectId != null) {
            User_Profile u_famousP = new User_Profile();
            Help_Comment help_comment = new Help_Comment();
            //获得内容
            help_comment.setComment(mEtSpeak.getText().toString());
            help_comment.setObjcetid(mHelp.getHelp_Title().toString());

            u_famousP.setObjectId(mStrObjectId);
            help_comment.setComment_user(u_famousP);
            //   Toast.makeText(getApplicationContext(),""+help_comment.getComment_user(),Toast.LENGTH_SHORT).show();
            help_comment.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        //  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        shouComment();
                    } else {
                        //  Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                        Log.d("CN", "onFailure: " + e.getMessage() + e.getErrorCode());
                    }
                }


            });
        } else {
            Toast.makeText(getApplicationContext(), "先登陆后评论，评论更美味", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 评论显示功能
     */
    private void shouComment() {

        final ArrayList arrayList = new ArrayList();
        //从服务器端获取评论内容


        String title = mHelp.getHelp_Title().toString();
        Toast.makeText(getApplicationContext(), "title" + title, Toast.LENGTH_SHORT).show();
        BmobQuery<Help_Comment> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.include("comment_user");
        bmobQuery.findObjects(new FindListener<Help_Comment>() {
            @Override
            public void done(List<Help_Comment> list, BmobException e) {
                if (e == null) {
                    for (Help_Comment comment : list) {
                        Toast.makeText(HelpDetailActivity.this, comment.getComment_user().getUser_Nickname(), Toast.LENGTH_SHORT).show();

                        /**
                         * 找到数据后封装为ArrayList
                         */
                        CommentUtils utils = new CommentUtils();
                //                    评论内容
                        utils.comment = comment.getComment();
                        //评论用户
                        utils.UserName = comment.getComment_user().getUser_Name();
                        //评论时间
                        utils.times = comment.getCreatedAt();
                        //评论用户头像地址
                        utils.icom = comment.getComment_user().getUser_image_min();
                        arrayList.add(utils);
                        Log.d("TAG", arrayList.size() + "onSuccess: " + utils.UserName + "在" + comment.getCreatedAt() + "点，说" + comment.getComment());
                        HelpCommentAdapter helpCommentAdapter = new HelpCommentAdapter(getApplicationContext(), arrayList);
                        mLvSpeak.setAdapter(helpCommentAdapter);
                        setListViewHeightBasedOnChildren(mLvSpeak);
                    }
                    if (arrayList.size() < 1) {
                        TextView tv_comment = (TextView) findViewById(R.id.tv_comment);
                        tv_comment.setVisibility(View.VISIBLE);
                    } else {
                        TextView tv_comment = (TextView) findViewById(R.id.tv_comment);
                        tv_comment.setVisibility(View.GONE);
                    }
                } else {
                    Log.d("Cn", "onError: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Error" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * 解决ListView和ScrollView滑动冲突
     *
     * @param listView
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

}
