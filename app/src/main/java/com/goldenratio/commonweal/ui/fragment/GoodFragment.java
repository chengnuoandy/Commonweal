package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodListViewAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.ui.activity.GoodActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class GoodFragment extends Fragment {


    private View view;
    private ListView mLvGood;
    private MyGoodListViewAdapter myGoodListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_good, null);
        view.findViewById(R.id.iv_add_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GoodActivity.class));
            }
        });

        initView();
        getGoodDataFromBmob();
        return view;
    }

    private void initView() {
        mLvGood = (ListView) view.findViewById(R.id.lv_good_all);
    }

    private void getGoodDataFromBmob() {
        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.order("-createdAt");
        goodBmobQuery.findObjects(getContext(), new FindListener<Good>() {
            @Override
            public void onSuccess(List<Good> list) {
                myGoodListViewAdapter = new MyGoodListViewAdapter(getContext(), list);
                mLvGood.setAdapter(myGoodListViewAdapter);
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }
}
