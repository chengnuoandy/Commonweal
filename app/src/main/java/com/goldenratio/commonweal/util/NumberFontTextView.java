package com.goldenratio.commonweal.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Kiuber on 2016/6/29.
 */

public class NumberFontTextView extends TextView {
    public NumberFontTextView(Context context) {
        super(context);
    }

    public NumberFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NumberFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        AssetManager assetManager = context.getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/pt_din_condensed_cyrillic.ttf");
        setTypeface(typeface);
    }
}
