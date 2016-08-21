package com.goldenratio.commonweal.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.bean.Dynamic_Comment;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.util.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 冰封承諾Andy on 2016/8/21 0021.
 * 动态列表的评论适配器
 */
public class DynamicCommentAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList mArrayListOne;
    private EditText edt_reply;
    private Button btn_reply;
    private String mStrObjectId;
    private Dynamic mDynamic;

    public DynamicCommentAdapter(ArrayList arrayListOne, Context context, Dynamic dynamic) {
        mArrayListOne = arrayListOne;
        mContext = context;
        mDynamic = dynamic;
        MyApplication myApplication = (MyApplication) ((Activity)context).getApplication();
        mStrObjectId = myApplication.getObjectID();
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment_one, null);
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.initData(position);

        return convertView;
    }

    private class ViewHolder {

        private ImageView icom;
        private TextView tv_comment;
        private TextView tv_name;
        private TextView tv_reply;
        private TextView tv_user_reply;
        private int pos;

        private void initView(View view) {
            icom = (ImageView) view.findViewById(R.id.iv_user_photo);
            tv_comment = (TextView) view.findViewById(R.id.tv_user_comment);
            tv_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_reply = (TextView) view.findViewById(R.id.tv_reply);
            tv_user_reply = (TextView) view.findViewById(R.id.tv_user_reply);

            //回复的点击事件
            tv_user_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //用来判断id层数
                    int s = 1 + pos;
                    //弹出对话框
                    onCreateDialog(s).show();
                }
            });
        }

        public void initData(int position) {
            pos = position;
            Comment utils = (Comment) mArrayListOne.get(position);
            tv_comment.setText(utils.comment);
            tv_name.setText(utils.UserName);
            tv_reply.setText("回复：" + utils.reply);

            if (utils.icom != null) {
                Picasso.with(mContext).load(utils.icom).into(icom);
            }
        }
    }

    /**
     * 弹出 输入框
     * @param s  传递数据的位置
     * @return
     */
    protected Dialog onCreateDialog(final int s) {

        final Dialog customDialog = new Dialog(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.dialog_comment, null);
        edt_reply = (EditText) mView.findViewById(R.id.edt_comments);
        btn_reply = (Button) mView.findViewById(R.id.btn_send);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(mView);
        customDialog.show();

        Toast.makeText(mContext, "" + s, Toast.LENGTH_SHORT).show();
        //btn_reply 的点击事件  将内容传到数据库
        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(edt_reply.getText().toString().trim() .isEmpty())) {
                    if (mStrObjectId != null) {
                        BmobQuery<Dynamic_Comment> bmobQuery = new BmobQuery<Dynamic_Comment>();
                        String title = mDynamic.getObjectId();
                        bmobQuery.addWhereEqualTo("objectid", title);
                        bmobQuery.findObjects(new FindListener<Dynamic_Comment>() {
                            @Override
                            public void done(List<Dynamic_Comment> list, BmobException e) {
                                if (e == null) {
                                    up(s, edt_reply.getText().toString());
                                    edt_reply.setText("");
                                    customDialog.dismiss();
                                }
                            }
                        });
                    }
                }else {
                    Toast.makeText(mContext,"您什么也没有评论呦~",Toast.LENGTH_SHORT).show();
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
    //参一  楼层Id   参二 回复内容
    private void up(int id, String ss) {
        int p = id - 1;
        Comment comment = (Comment) mArrayListOne.get(p);
        User_Profile u_famousP = new User_Profile();
        Dynamic_Comment dynamic_comment = new Dynamic_Comment();
        //获得内容
        dynamic_comment.setComment(ss);
        dynamic_comment.setObjcetid(mDynamic.getObjectId());
        dynamic_comment.setReply(comment.getUserName());
        u_famousP.setObjectId(mStrObjectId);
        dynamic_comment.setComment_user(u_famousP);
        //   Toast.makeText(getApplicationContext(),""+help_comment.getComment_user(),Toast.LENGTH_SHORT).show();
        dynamic_comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(mContext, "发送成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("CN", "onFailure: " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
