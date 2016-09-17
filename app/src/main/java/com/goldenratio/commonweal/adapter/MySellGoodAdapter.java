package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.util.ErrorCodeUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 冰封承諾Andy on 2016/8/19 0019.
 * 卖出物品列表的适配器
 */
public class MySellGoodAdapter extends BaseAdapter {

    private List<Good> mList;
    private Context mContext;
    private View mView;
    private Spinner mSpinner;
    private EditText mText; //单号
    private List<String> mStrings;
    private boolean isDel;
    private String shipperCode;

    public MySellGoodAdapter(Context context, List<Good> list) {
        mContext = context;
        mList = list;
        //对话框控件相关
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_sellgood_dialog, null);
        mSpinner = (Spinner) mView.findViewById(R.id.sr_source);
        mText = (EditText) mView.findViewById(R.id.et_number);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_sellgood_item, null);
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.initData(position);
        return convertView;
    }

    private class ViewHolder {

        private ImageView mIvPic;
        private TextView mTvTitle;
        private TextView mTvStatus;
        private TextView mBtnShip;
        private int mPos;

        private void initView(View view) {
            mIvPic = (ImageView) view.findViewById(R.id.iv_pic);
            mTvTitle = (TextView) view.findViewById(R.id.tv_title);
            mTvStatus = (TextView) view.findViewById(R.id.tv_status);
            mBtnShip = (TextView) view.findViewById(R.id.btn_ship);

            //设置对话框内的列表监听事件
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    shipperCode = mStrings.get(position);
//                    Toast.makeText(mContext, mStrings.get(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //确认发货按钮监听事件
            //弹出对话框
            mBtnShip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDel) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("提示");
                        builder.setMessage("你确定要删除这件物品吗？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除此物品
                                final Good good = new Good();
                                good.setObjectId(mList.get(mPos).getObjectId());
                                good.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            rwMySql(good.getObjectId());
                                            Toast.makeText(mContext, "删除成功！", Toast.LENGTH_SHORT).show();
                                            mList.remove(mPos);
                                            notifyDataSetChanged();
                                        } else {
//                                            Toast.makeText(mContext, "删除失败！", Toast.LENGTH_SHORT).show();
                                            ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        ship();
                    }
                }
            });
        }

        private void rwMySql(String objectId) {
            RequestParams params = new RequestParams("http://119.29.21.253/WebService1.asmx?op=DelGood");
            params.addBodyParameter("objectId", objectId);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(x.app(), "更新数据成功！", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFinished() {

                }
            });
        }

        private void initData(int pos) {
            mPos = pos;
            String str = "未知";
            int flag = mList.get(pos).getGood_Status();
            if (flag == 0) {
                str = "物品拍卖失败！";
            } else if (flag == 1) {
                str = "正在拍卖";
            } else if (flag == 2) {
                str = "拍卖成功，等待发货";
                addStrings();
                mBtnShip.setVisibility(View.VISIBLE);
            } else if (flag == 3) {
                str = "已发货，等待签收";
                addStrings();
                mBtnShip.setText("修改物流信息");
                mBtnShip.setVisibility(View.VISIBLE);
            } else if (flag == 4) {
                str = "已签收，订单完成！";
            }
            mTvTitle.setText(mList.get(pos).getGood_Name() + "\n" + mList.get(pos).getGood_Description());
            mTvStatus.setText("状态：" + str + "(现在价格:" + mList.get(pos).getGood_NowCoin() + ")");
            if (!mList.get(pos).getFirstDeposit()) {
                mBtnShip.setText("删除物品");
                isDel = true;
                mBtnShip.setVisibility(View.VISIBLE);
            }

            if (mList.get(pos).getGood_Photos() != null) {
                Glide.with(mContext)
                        .load(mList.get(pos).getGood_Photos().get(0))
                        .thumbnail(0.4f)
                        .into(mIvPic);
            }
        }

        /**
         * 上传发货信息
         */
        private void saveData() {
            if (shipperCode != null && !mText.getText().toString().isEmpty()) {
                String webServiceIp = ((MyApplication) (mContext.getApplicationContext())).getWebServiceIp();
                if (!(webServiceIp == null)) {
                    String URL = webServiceIp + "UploadLogisticCode";
                    //更新mysql数据库逻辑
                    RequestParams params = new RequestParams(URL);
                    params.addBodyParameter("OrderGood", mList.get(mPos).getObjectId());
                    params.addBodyParameter("Code", mText.getText().toString());
                    params.addBodyParameter("Company", shipperCode);
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            //更新bmob的逻辑
                            Good good = new Good();
                            good.setGood_Status(3);
                            good.update(mList.get(mPos).getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(mContext, "数据更新完毕", Toast.LENGTH_SHORT).show();
                                        mBtnShip.setVisibility(View.GONE);
                                    } else {
                                        //                                    Toast.makeText(mContext, "更新数据失败", Toast.LENGTH_SHORT).show();
                                        ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(mContext, "数据更新失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            Toast.makeText(mContext, "数据更新失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                } else {
                    Toast.makeText(mContext, "Ip地址获取失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * 发货按钮点击
         */
        private void ship() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("确认发货");
            builder.setView(mView);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ViewGroup) mView.getParent()).removeView(mView);
                    if (mText.getText().toString().isEmpty()) {
                        Toast.makeText(mContext, "运单编号不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        saveData();
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //防止父布局冲突，移除子view
                    ((ViewGroup) mView.getParent()).removeView(mView);
                }
            });
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    /**
     * 填充物流公司信息
     */
    private void addStrings() {
        mStrings = new ArrayList<>();
        mStrings.add("ANE");
        mStrings.add("AXD");
        mStrings.add("BFDF");
        mStrings.add("BQXHM");
        mStrings.add("CCES");
        mStrings.add("CITY100");
        mStrings.add("COE");
        mStrings.add("CSCY");
        mStrings.add("DBL");
        mStrings.add("DHL");
        mStrings.add("DSWL");
        mStrings.add("DTWL");
        mStrings.add("EMS");
        mStrings.add("FAST");
        mStrings.add("FEDEX");
        mStrings.add("FKD");
        mStrings.add("GDEMS");
        mStrings.add("GSD");
        mStrings.add("GTO");
        mStrings.add("GTSD");
        mStrings.add("HFWL");
        mStrings.add("HHTT");
        mStrings.add("HLWL");
        mStrings.add("HOAU");
        mStrings.add("hq568");
        mStrings.add("HTKY");
        mStrings.add("HXLWL");
        mStrings.add("HYLSD");
        mStrings.add("JD");
        mStrings.add("JGSD");
        mStrings.add("JJKY");
        mStrings.add("JTKD");
        mStrings.add("JXD");
        mStrings.add("JYKD");
        mStrings.add("JYM");
        mStrings.add("JYWL");
        mStrings.add("LB");
        mStrings.add("LHT");
        mStrings.add("MHKD");
        mStrings.add("MLWL");
        mStrings.add("NEDA");
        mStrings.add("QCKD");
        mStrings.add("QFKD");
        mStrings.add("QRT");
        mStrings.add("SAWL");
        mStrings.add("SDWL");
        mStrings.add("SF");
        mStrings.add("SFWL");
        mStrings.add("SHWL");
        mStrings.add("ST");
        mStrings.add("STO");
        mStrings.add("SURE");
        mStrings.add("TSSTO");
        mStrings.add("UAPEX");
        mStrings.add("UC");
        mStrings.add("WJWL");
        mStrings.add("WXWL");
        mStrings.add("XBWL");
        mStrings.add("XFEX");
        mStrings.add("XYT");
        mStrings.add("YADEX");
        mStrings.add("YCWL");
        mStrings.add("YD");
        mStrings.add("YFEX");
        mStrings.add("YFHEX");
        mStrings.add("YFSD");
        mStrings.add("YTKD");
        mStrings.add("YTO");
        mStrings.add("YZPY");
        mStrings.add("ZENY");
        mStrings.add("ZHQKD");
        mStrings.add("ZJS");
        mStrings.add("ZTE");
        mStrings.add("ZTKY");
        mStrings.add("ZTO");
        mStrings.add("ZTWL");
        mStrings.add("ZYWL");
    }
}
