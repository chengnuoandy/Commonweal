package com.goldenratio.commonweal.ui.lib;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

/**
 * Created by lvxue on 2016/6/19 0019.
 */
public class GoodEditText extends EditText {

    private static final String TAG = "lxc";

    public GoodEditText(Context context) {
        super(context);
        this.setSelection(this.getText().length());
    }

    public GoodEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setSelection(this.getText().length());
    }


    public void clickNum(String mStr) {
        //设置点击的数字 并移动游标
        this.setSelection(this.getText().length());
        this.getText().insert(this.getSelectionStart(), mStr);
    }

    public void clickOther(int i) {
        //删除键
        if (i == 0) {
            if (this.getText().length() != 0) {
                this.setSelection(this.getText().length());
                this.getText().delete(this.getSelectionStart() - 1, this.getSelectionStart());
            }
        }
    }


}
