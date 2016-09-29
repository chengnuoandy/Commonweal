package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goldenratio.commonweal.bean.Help_Top;
import com.goldenratio.commonweal.ui.activity.HelpTopDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 两个人 on 2016-06-14.
 */
public class HelpViewPagerAdapter extends PagerAdapter implements View.OnClickListener {

    private Context mContext;
    private List<Help_Top> list;
//    private Object[] imgUrl;

    public HelpViewPagerAdapter(Context context, List<Help_Top> mList) {
        super();
        mContext = context;
        list = mList;
    }

    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //初始化布局
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = new ImageView(mContext);
        view.setScaleType(ImageView.ScaleType.FIT_XY);// 设置图片缩放方式, 宽高填充父控件
        //String url = list.get(position).getHelp_Top_pic();
        String url = list.get(position).getHelp_Top_Pic();
        final String HelpDetailUrl = list.get(position).getHelp_Top_Url();

        Log.d(HelpDetailUrl, "instantiateItem: " + HelpDetailUrl);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HelpTopDetailActivity.class);
                intent.putExtra("flag", true);
                intent.putExtra("TopUrl", HelpDetailUrl);
                mContext.startActivity(intent);
            }
        });
        // Log.d("TAGGGGG", "getView: " + imgUrl[0]);
        Picasso.with(mContext).load(url).into(view);
        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onClick(View v) {

    }
}

