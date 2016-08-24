package com.goldenratio.commonweal.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.CommentAdatper;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Comment;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.iview.CommentBase;
import com.goldenratio.commonweal.iview.IComment;
import com.goldenratio.commonweal.util.Comment;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/8/17.
 */

public class HelpCommentActivity extends CommentBase implements IComment {

    private ListView mListView;

    private Help mHelp;
    private String mStrObjectId;
    private ArrayList arrayList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHelp = (Help) getIntent().getSerializableExtra("Help");
        setData(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setData(IComment comment) {
        super.setData(comment);
    }

    private void up(String ss) {
        User_Profile u_famousP = new User_Profile();
        Help_Comment help_comment = new Help_Comment();
        //获得内容
        help_comment.setComment(ss);
        help_comment.setObjcetid(mHelp.getObjectId());
        help_comment.setReply("主题");
        u_famousP.setObjectId(mStrObjectId);
        help_comment.setComment_user(u_famousP);
        //   Toast.makeText(getApplicationContext(),""+help_comment.getComment_user(),Toast.LENGTH_SHORT).show();
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

    @Override
    public void upComment(String objID, String content) {
        final String str = content;
        mStrObjectId = objID;
        final BmobQuery<Help_Comment> bmobQuery = new BmobQuery();
        final String title = mHelp.getObjectId();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.findObjects(new FindListener<Help_Comment>() {
            @Override
            public void done(List<Help_Comment> list, BmobException e) {
                if (!(str.trim().isEmpty())) {
                    up(str);
                }
            }
        });
    }

    @Override
    public void Show(ListView listView, final BGARefreshLayout refreshLayout) {
        mListView = listView;
        if (arrayList.size() > 0 || arrayList != null)
            arrayList.clear();

//        //从服务器端获取评论内容
        final String title = mHelp.getObjectId();
        BmobQuery<Help_Comment> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.include("comment_user");
        bmobQuery.findObjects(new FindListener<Help_Comment>() {
            @Override
            public void done(List<Help_Comment> list, BmobException e) {
                if (e == null) {
                    for (Help_Comment comment : list) {
                        Comment utils = new Comment();
                        //                    评论内容
                        utils.setUserID(comment.getComment_user().getObjectId());
                        utils.comment = comment.getComment();
                        //评论用户
                        utils.UserName = comment.getComment_user().getUser_Nickname();
                        //评论用户头像地址
                        utils.icom = comment.getComment_user().getUser_image_hd();
                        utils.reply = comment.getReply();
                        //封装到list集合中

                        arrayList.add(utils);
                        CommentAdatper commentAdatper = new CommentAdatper(mHelp, HelpCommentActivity.this, arrayList);
                        mListView.setAdapter(commentAdatper);
                    }
                    //结束刷新
                    refreshLayout.endRefreshing();
                    mListView.setEmptyView(findViewById(R.id.tv_emty));
                } else {
                    //结束刷新
                    refreshLayout.endRefreshing();
                    Log.d("错误", "done: " + e);
                }
            }
        });
    }
}
