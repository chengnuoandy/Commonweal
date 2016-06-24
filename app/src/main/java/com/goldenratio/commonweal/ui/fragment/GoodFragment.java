package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.activity.GoodActivity;


public class GoodFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_good, null);
        view.findViewById(R.id.add_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GoodActivity.class));
            }
        });
        return view;
    }
}
