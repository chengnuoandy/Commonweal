package com.goldenratio.commonweal.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Deposit;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.bean.User_Profile;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * 输入支付密码
 *
 * @author lining
 */
public class PopEnterPassword extends PopupWindow {

    private PasswordView pwdView;

    private View mMenuView;

    private Activity mContext;
    private final TextView mTvCoin;
    private final TextView mTvSXF;
    private final TextView mTvType;


    public PopEnterPassword(final Activity context, String type, String coin, final int flag, final String goodObjectId, final String orderId) {

        super(context);

        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMenuView = inflater.inflate(R.layout.pop_enter_password, null);

        pwdView = (PasswordView) mMenuView.findViewById(R.id.pwd_view);


        mTvType = (TextView) mMenuView.findViewById(R.id.textType);
        mTvType.setText(type);
        mTvCoin = (TextView) mMenuView.findViewById(R.id.textAmount);
        mTvCoin.setText(coin);
        mTvSXF = (TextView) mMenuView.findViewById(R.id.textShouxuFei);
        double coin1 = Double.valueOf(coin);
        double sxf = coin1 * 0.05;

        mTvSXF.setText("额外扣除¥" + sxf + "手续费");

        //添加密码输入完成的响应
        pwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String password) {

                switch (flag) {
                    //收取保证金
                    case 0:
                        String mStrObjectId = ((MyApplication) mContext.getApplication()).getObjectID();
                        User_Profile user_profile = new User_Profile();
                        user_profile.setObjectId(mStrObjectId);

                        Deposit deposit = new Deposit();
                        deposit.setD_User(user_profile);
                        deposit.setD_GoodId(goodObjectId);
                        deposit.setD_Coin("0.01");
                        deposit.setOrderId(orderId);
                        deposit.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(mContext, "保证金收取成功", Toast.LENGTH_SHORT).show();
                                    mContext.finish();
                                    mContext.startActivity(mContext.getIntent());
                                } else {
                                    Toast.makeText(mContext, "保证金支付失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    //支付物品
                    case 1:
                        break;
                }
                dismiss();

                Toast.makeText(mContext, "支付成功，密码为：" + password, Toast.LENGTH_SHORT).show();
            }
        });

        // 监听X关闭按钮
        pwdView.getImgCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 监听键盘上方的返回
        pwdView.getVirtualKeyboardView().getLayoutBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_add_ainm);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x66000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }
}
