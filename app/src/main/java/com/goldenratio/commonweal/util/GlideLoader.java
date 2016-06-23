package com.goldenratio.commonweal.util;

/**
 * Created by Kiuber on 2016/6/22.
 */

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideLoader implements com.yancy.imageselector.ImageLoader {

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                .centerCrop()
                .into(imageView);
    }
}