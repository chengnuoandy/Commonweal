package com.goldenratio.commonweal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Kiuber on 2016/6/17.
 */

public class FileUtils {

    public static Bitmap getLocalBitmap(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
