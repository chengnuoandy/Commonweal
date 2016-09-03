package com.goldenratio.commonweal.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Bid;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.BidRecordActivity;
import com.goldenratio.commonweal.ui.activity.GoodDetailActivity;
import com.goldenratio.commonweal.ui.activity.LoginActivity;
import com.goldenratio.commonweal.ui.activity.OrderActivity;
import com.goldenratio.commonweal.ui.activity.WalletActivity;
import com.goldenratio.commonweal.ui.activity.my.AttentionStarActivity;
import com.goldenratio.commonweal.ui.activity.my.DynamicActivity;
import com.goldenratio.commonweal.ui.activity.my.MessageActivity;
import com.goldenratio.commonweal.ui.activity.my.MySetActivity;
import com.goldenratio.commonweal.ui.activity.my.SellGoodActivity;
import com.goldenratio.commonweal.ui.activity.my.UserSettingsActivity;
import com.goldenratio.commonweal.util.BitmapUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;
import com.goldenratio.commonweal.util.NormalFontTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者：Created by 龙啸天 on 2016/6/29 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 */
public class MyFragment extends Fragment {
    @BindView(R.id.civ_avatar)
    CircleImageView mAvatar;
    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.iv_my_message)
    ImageView mIvMessage;
    @BindView(R.id.rl_background)
    RelativeLayout mRlBackground;
    @BindView(R.id.tv_my_attention)
    TextView mTvMyAttention;

    private boolean isLogin = false;
    private String userSex;
    private String userNickname;//用户昵称
    private String autograph; //个性签名
    private String userName;  //用户名
    private String avaUrl;  //用户高清头像
    private String avaMinUrl;//用户小头像
    public static String mUserID; //用户objectid


    private TextView mTextView;
    private TextView mTvOrder;
    private TextView mTvWallet;
    private View mTvSetting;
    private ImageView mIvIsV;
    private TextView mTvFans;
    public static String userWBid;
    private String mUserCoin;
    private TextView mBidRecord;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, null);

        ButterKnife.bind(this, view);

        if (isUserTableExist()) {
            getUserData();
            isLogin = true;
        }

        view.findViewById(R.id.tv_my_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getActivity(), SellGoodActivity.class);
                startActivity(intent3);
            }
        });
//        mTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), LogisticsInformation.class);
//                startActivity(intent);
//            }
//        });
        mIvIsV = (ImageView) view.findViewById(R.id.iv_v);
        initView(view);
        return view;
    }


    private void initView(View view) {
        mTvOrder = (TextView) view.findViewById(R.id.tv_my_order);
        mTvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OrderActivity.class));
            }
        });
        mTvWallet = (TextView) view.findViewById(R.id.tv_my_wallet);
        mTvWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentW = new Intent(getContext(), WalletActivity.class);
                intentW.putExtra("coin", mUserCoin);
                startActivity(intentW);
            }
        });
        mTvSetting = view.findViewById(R.id.tv_settings);
        mTvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), MySetActivity.class);
                intent1.putExtra("islogin", isLogin);
                startActivityForResult(intent1, 2);
            }
        });
        mTvFans = (TextView) view.findViewById(R.id.tv_my_fans);
        mTvFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getActivity(), AttentionStarActivity.class);
                startActivity(intent3);
            }
        });
        mBidRecord = (TextView) view.findViewById(R.id.tv_my_bid);
        mBidRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), BidRecordActivity.class);
                intent1.putExtra("flag", 1);
                startActivity(intent1);
            }
        });
    }


    @OnClick({R.id.civ_avatar, R.id.tv_name, R.id.iv_my_message, R.id.tv_my_attention, R.id.tv_my_dynamic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_avatar:
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), UserSettingsActivity.class);
                    intent.putExtra("user_sex", userSex);
                    intent.putExtra("user_nickname", userNickname);
                    intent.putExtra("user_name", userName);
                    intent.putExtra("autograph", autograph);
                    intent.putExtra("avaUrl", avaUrl);
                    intent.putExtra("wbid", userWBid);
                    intent.putExtra("avaMinUrl", avaMinUrl);
                    startActivityForResult(intent, 3);
                    break;
                }
            case R.id.tv_name:
                if (!isUserTableExist()) {
                    if (!isLogin) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    Toast.makeText(getActivity(), "用户已经登陆", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_my_message:
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_my_attention:
                Intent intent2 = new Intent(getActivity(), AttentionStarActivity.class);
                intent2.putExtra("is_attention", true);
                startActivity(intent2);
                break;
            case R.id.tv_my_dynamic:
                Intent intent1 = new Intent(getActivity(), DynamicActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //登陆界面返回数据
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    mUserID = data.getStringExtra("objectId");
                    isLogin = true;
                    getUserData();
                    ((MyApplication) getActivity().getApplication()).setObjectID(mUserID);
                    Log.i("lxc", "onActivityResult: " + mUserID);
                }
                break;
            //应用设置界面返回数据
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    isLogin = false;
                    Log.i("settings", "设置返回");
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());
                }
                break;
            case 3:
                getUserData();
                break;
        }
    }

    /**
     * 判断本地用户表是否存在
     *
     * @return
     */
    private boolean isUserTableExist() {
        boolean isTableExist = true;
        String sqlCmd = "SELECT count(User_Avatar) FROM User_Profile ";
        UserDao ud = new UserDao(getActivity());
        Cursor c = ud.query(sqlCmd);
        if (c.moveToNext()) {
            if (c.getInt(0) == 0) {
                isTableExist = false;
            }
        }
        c.close();
        return isTableExist;
    }

    /**
     * 读取本地数据库数据 （用户默认头像和签名）
     * <p/>
     * 用户唯一id（objectid）
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getUserData() {
        String sqlCmd = "SELECT * FROM User_Profile ";
        UserDao ud = new UserDao(getActivity());
        Cursor cursor = ud.query(sqlCmd);
        userName = "";
        userNickname = "";
        avaUrl = "";
        autograph = "";
        if (cursor.moveToFirst()) {
            mUserID = cursor.getString(cursor.getColumnIndex("objectId"));
            userSex = cursor.getString(cursor.getColumnIndex("User_sex"));
            userName = cursor.getString(cursor.getColumnIndex("User_Name"));
            userNickname = cursor.getString(cursor.getColumnIndex("User_Nickname"));
            autograph = cursor.getString(cursor.getColumnIndex("User_Autograph"));
            avaUrl = cursor.getString(cursor.getColumnIndex("User_Avatar"));
            avaMinUrl = cursor.getString(cursor.getColumnIndex("User_image_min"));
            userWBid = cursor.getString(cursor.getColumnIndex("User_weiboID"));
            //   Log.i("ud", avaUrl);
        }
        cursor.close();
        mTvName.setTextColor(getResources().getColor(R.color.colorPrimary));
        mTvName.setBackground(null);
        mTvName.setText(userNickname);
        Picasso.with(getActivity()).load(avaUrl).into(mAvatar);

        //将头像进行高斯模糊
        Glide.with(getContext()).load(avaUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                Bitmap blurBitmap = BitmapUtil.createBlurBitmap(resource, 100);
                Drawable drawable = new BitmapDrawable(blurBitmap);
                mRlBackground.setBackground(drawable);
            }
        });
        Log.d("Kiuber_LOG", "getUserData: " + mUserID);
        if (!TextUtils.isEmpty(mUserID)) {
            BmobQuery<User_Profile> user_profileBmobQuery = new BmobQuery<>();
            user_profileBmobQuery.addWhereEqualTo("objectId", mUserID);
            user_profileBmobQuery.findObjects(new FindListener<User_Profile>() {
                @Override
                public void done(List<User_Profile> list, BmobException e) {
                    if (e == null) {
                        if (list.size() == 1) {
                            if (list.get(0).isUser_IsV()) {
                                mIvIsV.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("Kiuber_LOG", "done: " + list.get(0).isUser_IsV() + "");
                            }
                        } else {
                            Log.d("Kiuber_LOG", "done: " + list.size());
                        }
                    } else {
                        Log.d("Kiuber_LOG", "done: " + e.getMessage());
                    }
                }
            });
        }
    }
}