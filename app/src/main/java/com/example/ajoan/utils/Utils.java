package com.example.ajoan.utils;

import android.content.res.Resources;
import android.util.TypedValue;


/**
 * Created by AJoan on 24/12/2016.
 */

public class Utils {

    public static float pixelsInDP(int dp,Resources resources){
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return pixels;
    }

}
