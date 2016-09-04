package com.goldenratio.commonweal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.GoodCommentAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.Good_Comment;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.iview.CommentBase;
import com.goldenratio.commonweal.iview.IComment;
import com.goldenratio.commonweal.util.ErrorCodeUtil;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/8/20.
 */

public class GoodDetailCommentActivity extends CommentBase implements IComment {
    private ListView mListView;
    private Good mGood;
    private String mStrObjectId;
//    private ArrayList arrayList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       Intent intent = this.getIntent();
        mGood = (Good) intent.getSerializableExtra("Good");
//        mGood = (Good) getIntent().getSerializableExtra("Good");
        setData(this);
        super.onCreate(savedInstanceState);
    }

    //用于回调
    @Override
    public void setData(IComment comment) {
        super.setData(comment);
    }

    /**
     * 将输入的内容传递到服务器中
     *
     * @param ss 输入的内容
     */
    private void up(String ss) {
        User_Profile u_famousP = new User_Profile();
        Good_Comment help_comment = new Good_Comment();
        //获得内容
        help_comment.setComment(ss);
        help_comment.setObjcetid(mGood.getObjectId());
        help_comment.setReply("主题");
        u_famousP.setObjectId(mStrObjectId);
        help_comment.setComment_user(u_famousP);
        help_comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
//                    Log.d("CN", "onFailure: " + e.getMessage() + e.getErrorCode());
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    @Override
    public void upComment(String objID, String content) {
        final String str = content;
        mStrObjectId = objID;

        final BmobQuery<Good_Comment> bmobQuery = new BmobQuery();
        final String title = mGood.getObjectId();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.findObjects(new FindListener<Good_Comment>() {
            @Override
            public void done(List<Good_Comment> list, BmobException e) {
                if (e==null) {
                    if (!(str.trim().isEmpty())) {
                        up(str);
                    }
                } else {
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    /**
     * 从服务器读取数据并以ArrayList的形式传递到Adapter中 从而添加到ListView
     */
    @Override
    public void Show(ListView listView, final BGARefreshLayout refreshLayout) {
        mListView = listView;
        //从服务器端获取评论内容
        final String title = mGood.getObjectId();
        BmobQuery<Good_Comment> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.include("comment_user");
        bmobQuery.findObjects(new FindListener<Good_Comment>() {
            @Override
            public void done(List<Good_Comment> list, BmobException e) {
                if (e == null) {
                    GoodCommentAdapter commentAdatper = new GoodCommentAdapter(mGood, GoodDetailCommentActivity.this, list);
                    mListView.setAdapter(commentAdatper);
                    //结束刷新
                    refreshLayout.endRefreshing();
                    mListView.setEmptyView(findViewById(R.id.tv_emty));
                } else {
                    //结束刷新
                    refreshLayout.endRefreshing();
//                    Log.d("错误", "done: " + e);
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }
}
