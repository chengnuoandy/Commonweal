package com.goldenratio.commonweal.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
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
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.Good_Comment;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.StarInfoActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/8/20.
 */

public class GoodCommentAdapter extends BaseAdapter {
    private Context mContext;
    private List<Good_Comment> mArrayListOne;
    private EditText edt_reply;
    private Button btn_reply;
    private String mStrObjectId;
    private Good mGood;
    private int post;
    private User_Profile mUserProfile;

    /**
     * @param good      传递过来的实例
     * @param context   上下文对象
     * @param arrayList 传递过来的含有数据的集合
     */
    public GoodCommentAdapter(Good good, Context context, List<Good_Comment> arrayList) {
        this.mContext = context;
        this.mArrayListOne = arrayList;
        this.mGood = good;
        MyApplication myApplication = (MyApplication) ((Activity) context).getApplication();
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
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        post = position;
        mUserProfile = mArrayListOne.get(position).getComment_user();
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = View.inflate(mContext, R.layout.item_comment_one, null);
        }
        ImageView icom = (ImageView) view.findViewById(R.id.iv_user_photo);
        icom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication myApplication = (MyApplication) ((Activity) mContext).getApplication();
                String mStrObjectId = myApplication.getObjectID();
                if (!mArrayListOne.get(position).getComment_user().getObjectId().equals(mStrObjectId)) {
                    List<String> attenList;
                    attenList = mArrayListOne.get(position).getComment_user().getUser_Attention();
                    int isHas = -1;
                    if (attenList != null)
                        isHas = attenList.indexOf(mArrayListOne.get(position).getComment_user().getObjectId());
                    Intent intent = new Intent(mContext, StarInfoActivity.class);
                    intent.putExtra("ishas", isHas != -1);
                    intent.putExtra("id", mArrayListOne.get(position).getComment_user().getObjectId());
                    intent.putExtra("autograph", mArrayListOne.get(position).getComment_user().getUser_Autograph());
                    intent.putExtra("nickName", mArrayListOne.get(position).getComment_user().getUser_Nickname());
                    intent.putExtra("isv", mArrayListOne.get(position).getComment_user().isUser_IsV());
                    intent.putExtra("Avatar", mArrayListOne.get(position).getComment_user().getUser_image_hd());
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(myApplication, "不要再点了，这里真的什么都没有~", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView tv_comment = (TextView) view.findViewById(R.id.tv_user_comment);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
        TextView tv_reply = (TextView) view.findViewById(R.id.tv_reply);
//        final Comment utils = (Comment) mArrayListOne.get(position);
        tv_comment.setText(mArrayListOne.get(position).getComment());
        tv_name.setText(mUserProfile.getUser_Name());
        tv_reply.setText("回复：" + Html.fromHtml("<font color='#0000FF'>" + mArrayListOne.get(position).getReply() + "</font> "));
        Picasso.with(mContext).load(mUserProfile.getUser_image_hd()).into(icom);


        Picasso.with(mContext).load(mUserProfile.getUser_image_hd()).into(icom);
        icom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUserProfile.getObjectId().equals(mStrObjectId)) {
                    Log.i("55555", "onClick: " + "0.0.00.0执行");
                    Intent intent = new Intent(mContext, StarInfoActivity.class);
                    intent.putExtra("id", mUserProfile.getObjectId());
                    intent.putExtra("isv", mUserProfile.isUser_IsV());
                    intent.putExtra("nickName", mUserProfile.getUser_Nickname());
                    intent.putExtra("autograph", mUserProfile.getUser_Autograph());
                    intent.putExtra("Avatar", mUserProfile.getUser_image_hd());
                    mContext.startActivity(intent);
                }
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

    /**
     * 弹出 输入框
     *
     * @param s 传递数据的位置
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
                if (!(edt_reply.getText().toString().trim().isEmpty())) {
                    getlocality();
                    //
                    if (mStrObjectId != null) {
                        BmobQuery<Good_Comment> bmobQuery = new BmobQuery<Good_Comment>();
                        String title = mGood.getObjectId();
                        bmobQuery.addWhereEqualTo("objectid", title);
                        bmobQuery.findObjects(new FindListener<Good_Comment>() {
                            @Override
                            public void done(List<Good_Comment> list, BmobException e) {
                                if (e == null) {
                                    up(s, edt_reply.getText().toString());
                                    edt_reply.setText("");
                                    customDialog.dismiss();
                                } else {
                                    ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
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

    /**
     * 获取本地登录的数据
     */
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

    /**
     * 将输入的内容传递到服务器中
     *
     * @param id 获得位置
     * @param ss 输入的内容
     */
    //参一  楼层Id   参二 回复内容
    private void up(int id, String ss) {
        int p = id - 1;
//        Comment comment = (Comment) mArrayListOne.get(p);
        User_Profile u_famousP = new User_Profile();
        Good_Comment good_comment = new Good_Comment();
        //获得内容
        good_comment.setComment(ss);
        good_comment.setObjcetid(mGood.getObjectId());
        good_comment.setReply(mArrayListOne.get(p).getComment_user().getUser_Nickname());
        u_famousP.setObjectId(mStrObjectId);
        good_comment.setComment_user(u_famousP);
        //   Toast.makeText(getApplicationContext(),""+help_comment.getComment_user(),Toast.LENGTH_SHORT).show();
        good_comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
//                    Log.d("CN", "onFailure: " + e.getMessage() + e.getErrorCode());
                    ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
                }
            }
        });
    }
}
