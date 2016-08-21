package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.CommentAdatper;
import com.goldenratio.commonweal.adapter.GoodCommentAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.Good_Comment;

import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.util.Comment;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/8/20.
 */

public class GoodDetailCommentActivity extends Activity implements View.OnClickListener,BGARefreshLayout.BGARefreshLayoutDelegate{
    private ListView mListView;
    private EditText edt_reply;
    private Button btn_reply;
    private Good mGood;
    private String mStrObjectId;
    private ArrayList arrayList;
    /**
     * 下拉刷新
     */
    private BGARefreshLayout mBGARefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        mGood = (Good) getIntent().getSerializableExtra("Good");
        show();
    }

    private void initView() {
        findViewById(R.id.ibtn_send).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv_comment_one);
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

    /**
     * 弹出 输入框
     * @param id  传递数据的位置
     * @return
     */
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
                if(! (edt_reply.getText().toString().isEmpty()) ){

                    //获取本地数据库
                    UserDao userDao = new UserDao(GoodDetailCommentActivity.this);
                    Cursor cursor = userDao.query("select * from User_Profile");
                    while (cursor.moveToNext()) {
                        int nameColumnIndex = cursor.getColumnIndex("objectId");
                        mStrObjectId = cursor.getString(nameColumnIndex);
                    }
                    cursor.close();
                    //判断用户是否登录
                    if (mStrObjectId != null) {
                        final BmobQuery<Good_Comment> bmobQuery = new BmobQuery();
                        final String title = mGood.getObjectId().toString();
                        bmobQuery.addWhereEqualTo("objcetid", title);
                        bmobQuery.findObjects(new FindListener<Good_Comment>() {
                            @Override
                            public void done(List<Good_Comment> list, BmobException e) {
                                BmobQuery<Good_Comment> bmobQuery1 = new BmobQuery<Good_Comment>();
                                bmobQuery1.addWhereEqualTo("reply", title);
                                bmobQuery1.findObjects(new FindListener<Good_Comment>() {
                                    @Override
                                    public void done(List<Good_Comment> list, BmobException e) {
                                        up(list.size() + 1, edt_reply.getText().toString());
                                        edt_reply.setText("");
                                        customDialog.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "先登陆~评论更加美味", Toast.LENGTH_SHORT).show();
                    }
                    show();
                }else {
                    Toast.makeText(getApplicationContext(),"您什么也没有评论呦~",Toast.LENGTH_SHORT).show();
                    edt_reply.setText("");
                    customDialog.dismiss();
                }
            }
        });
        return customDialog;
    }

    /**
     * 将输入的内容传递到服务器中
     * @param id 获得位置
     * @param ss 输入的内容
     */
    private void up(int id, String ss) {
        User_Profile u_famousP = new User_Profile();
        Good_Comment help_comment = new Good_Comment();
        //获得内容
        help_comment.setComment(ss);
        help_comment.setObjcetid(mGood.getObjectId());
        help_comment.setReply("主题");
        help_comment.setId(id);
        u_famousP.setObjectId(mStrObjectId);
        help_comment.setComment_user(u_famousP);
        help_comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
                    Log.d("CN", "onFailure: " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 从服务器读取数据并以ArrayList的形式传递到Adapter中 从而添加到ListView
     */
    private void show() {
        arrayList = new ArrayList();
//        //从服务器端获取评论内容
        final String title = mGood.getObjectId().toString();
        BmobQuery<Good_Comment> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.include("comment_user");
        bmobQuery.findObjects(new FindListener<Good_Comment>() {
            @Override
            public void done(List<Good_Comment> list, BmobException e) {
                if (e == null) {
                    for (Good_Comment comment : list) {
                        Comment utils = new Comment();
                        //                    评论内容
                        utils.comment = comment.getComment();
                        //评论用户
                        utils.UserName = comment.getComment_user().getUser_Name();
                        //评论用户头像地址
                        utils.icom = comment.getComment_user().getUser_image_hd();
                        utils.reply = comment.getReply();
                        //封装到list集合中
                            arrayList.add(utils);
                            GoodCommentAdapter commentAdatper = new GoodCommentAdapter(mGood ,GoodDetailCommentActivity.this, arrayList);
                            mListView.setAdapter(commentAdatper);
                    }
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                } else {
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                    Log.d("错误", "done: " + e);
                }
            }
        });

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
        }else {
            show();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
