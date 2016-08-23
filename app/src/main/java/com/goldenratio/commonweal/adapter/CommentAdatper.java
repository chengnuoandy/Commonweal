package com.goldenratio.commonweal.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Comment;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.StarInfoActivity;
import com.goldenratio.commonweal.util.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/8/17.
 * 第一级评论
 */

public class CommentAdatper extends BaseAdapter {
    private static final String TAG = "lxc";
    private Context mContext;
    private ArrayList mArrayListOne;
    private EditText edt_reply;
    private Button btn_reply;
    private String mStrObjectId;
    private Help mHelp;

    private int post;

    public CommentAdatper(Help help, Context context, ArrayList arrayList) {
        this.mContext = context;
        this.mArrayListOne = arrayList;
        this.mHelp = help;

    }

    @Override
    public int getCount() {
        return mArrayListOne.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayListOne.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        post = position;
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = View.inflate(mContext, R.layout.item_comment_one, null);
        }
        ImageView icom = (ImageView) view.findViewById(R.id.iv_user_photo);
        TextView tv_comment = (TextView) view.findViewById(R.id.tv_user_comment);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
        TextView tv_reply = (TextView) view.findViewById(R.id.tv_reply);
        final Comment utils = (Comment) mArrayListOne.get(position);
        tv_comment.setText(utils.comment);
        tv_name.setText(utils.UserName);
        tv_reply.setText("回复：" + utils.reply);
        Picasso.with(mContext).load(utils.icom).into(icom);
        icom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!utils.getUserID().equals(mStrObjectId)) {
                    Log.i("55555", "onClick: " + "0.0.00.0执行");
                    Intent intent = new Intent(mContext, StarInfoActivity.class);
                    intent.putExtra("id", utils.getUserID());
                    intent.putExtra("nickName", utils.getUserName());
                    intent.putExtra("Avatar", utils.getIcom());
                    mContext.startActivity(intent);
                } else Toast.makeText(mContext, "哈哈哈哈" + "这里还没写", Toast.LENGTH_SHORT).show();

            }
        });
        //回复的点击事件
        view.findViewById(R.id.tv_user_reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用来判断id层数
                int s = 1 + position;
                //弹出对话框
                onCreateDialog(s).show();
            }
        });
        return view;
    }

    //弹出对话框
    protected Dialog onCreateDialog(final int s) {

        final Dialog customDialog = new Dialog(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.dialog_comment, null);
        edt_reply = (EditText) mView.findViewById(R.id.edt_comments);
        btn_reply = (Button) mView.findViewById(R.id.btn_send);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(mView);
        customDialog.show();

//        Toast.makeText(mContext, "" + s, Toast.LENGTH_SHORT).show();
        //btn_reply 的点击事件  将内容传到数据库
        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(edt_reply.getText().toString().trim().isEmpty())) {
                    getlocality();
                    //
                    if (mStrObjectId != null) {
                        BmobQuery<Help_Comment> bmobQuery = new BmobQuery<Help_Comment>();
                        String title = mHelp.getObjectId();
                        bmobQuery.addWhereEqualTo("objectid", title);
                        bmobQuery.findObjects(new FindListener<Help_Comment>() {
                            @Override
                            public void done(List<Help_Comment> list, BmobException e) {
                                if (e == null) {
                                    if (!(edt_reply.getText().toString().isEmpty())) {
                                        up(s, edt_reply.getText().toString());
                                        edt_reply.setText("");
                                        customDialog.dismiss();
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(mContext, "您什么也没有评论呦~", Toast.LENGTH_SHORT).show();
                    edt_reply.setText("");
                    customDialog.dismiss();
                }
            }
        });
        return customDialog;
    }

    //获取本地数据库
    private void getlocality() {
        //获取本地数据库
        UserDao userDao = new UserDao(mContext);
        Cursor cursor = userDao.query("select * from User_Profile");
        while (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex("objectId");
            mStrObjectId = cursor.getString(nameColumnIndex);
        }
        cursor.close();
    }

    //从服务器获取内容
    //参一  楼层Id   参二 回复内容
    private void up(int id, String ss) {
        int p = id - 1;
        Comment comment = (Comment) mArrayListOne.get(p);
        User_Profile u_famousP = new User_Profile();
        Help_Comment help_comment = new Help_Comment();
        //获得内容
        help_comment.setComment(ss);
        help_comment.setObjcetid(mHelp.getObjectId());
        help_comment.setReply(comment.getUserName());
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
}