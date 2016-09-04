package com.goldenratio.commonweal.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.DynamicCommentAdapter;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.bean.Dynamic_Comment;
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
 * Created by 冰封承諾Andy on 2016/8/21 0021.
 * 我的动态页面的评论
 */
public class MyDynamicCommentActivity extends CommentBase implements IComment {

    private ListView mListView;
    private Dynamic mDynamic;
    private String mStrObjectId;
//    private ArrayList arrayList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDynamic = (Dynamic) getIntent().getSerializableExtra("dyc");
        setData(this);
        super.onCreate(savedInstanceState);
    }

    private void up(String ss) {
        User_Profile u_famousP = new User_Profile();
        Dynamic_Comment dynamic_comment = new Dynamic_Comment();
        //获得内容
        dynamic_comment.setComment(ss);
        dynamic_comment.setObjcetid(mDynamic.getObjectId());
        dynamic_comment.setReply("主题");
        u_famousP.setObjectId(mStrObjectId);
        dynamic_comment.setComment_user(u_famousP);
        //   Toast.makeText(getApplicationContext(),""+dynamic_comment.getComment_user(),Toast.LENGTH_SHORT).show();
        dynamic_comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(MyDynamicCommentActivity.this, "发送成功！", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(MyDynamicCommentActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
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
        final BmobQuery<Dynamic_Comment> bmobQuery = new BmobQuery<>();
        final String title = mDynamic.getObjectId();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.findObjects(new FindListener<Dynamic_Comment>() {
            @Override
            public void done(List<Dynamic_Comment> list, BmobException e) {
                if (e == null) {
                    if (!(str.trim().isEmpty())) {
                        up(str);
                    }
                } else {
//                    Toast.makeText(MyDynamicCommentActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                    ErrorCodeUtil.switchErrorCode(getApplicationContext(), e.getErrorCode() + "");
                }
            }
        });
    }

    @Override
    public void Show(ListView listView, final BGARefreshLayout refreshLayout) {
        mListView = listView;

//        //从服务器端获取评论内容
        final String title = mDynamic.getObjectId();
        BmobQuery<Dynamic_Comment> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objcetid", title);
        bmobQuery.include("comment_user");
        bmobQuery.findObjects(new FindListener<Dynamic_Comment>() {
            @Override
            public void done(List<Dynamic_Comment> list, BmobException e) {
                if (e == null) {

                    DynamicCommentAdapter commentAdatper = new DynamicCommentAdapter(list, MyDynamicCommentActivity.this, mDynamic);
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
