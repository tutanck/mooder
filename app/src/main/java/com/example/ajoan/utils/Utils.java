package com.example.ajoan.utils;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by AJoan on 24/12/2016.
 */

public class Utils {

    public static float pixelsInDP(int dp,Resources resources){
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return pixels;
    }

    public static boolean matchRule(String rule,String s){
        Pattern pattern = Pattern.compile(rule);
        Matcher matcher = pattern.matcher(s);

        boolean alors = matcher.matches();
        Log.i("Utils.matchRule", s + " respects "+ rule +" : "+alors);
        return alors;
    }

    public static int getMSGTVHeight(Resources resources){
        return (int)Utils.pixelsInDP(36,resources);
    }

}
