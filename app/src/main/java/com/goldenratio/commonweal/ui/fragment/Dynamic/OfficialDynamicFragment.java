package com.goldenratio.commonweal.ui.fragment.Dynamic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenratio.commonweal.R;

/**
 * Created by 龙啸天 on 2016/6/21 0021.
 */
public class OfficialDynamicFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic_official, null);
        return view;
    }
}
