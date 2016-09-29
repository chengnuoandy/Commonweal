package com.goldenratio.commonweal.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/22.
 */

public class NormalFontTextView extends TextView {
    public NormalFontTextView(Context context) {
        super(context);
    }

    public NormalFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NormalFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        AssetManager assetManager = context.getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/Gotham-Light.otf");
        setTypeface(typeface);
    }
}
