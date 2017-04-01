package com.example.ajoan.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
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

        boolean respected = matcher.matches();
        Log.i("Utils.matchRule", s + " respects "+ rule +" : "+ respected);
        return respected;
    }

    public static Intent intent(
            Intent intent,
            String action,
            String type
    ){
        intent.setAction(action!=null?action:Intent.ACTION_SEND);

        intent.setType(type!=null?type:"text/plain");

        return intent;
    }




    public static String compileRequestURL(
            String url,
            Map<String,String> params
    ){
        String reqStr = url;
        if(params!=null && params.size()>0)
            reqStr+="?";
        int i=0;
        for(Map.Entry<String,String> entry : params.entrySet()) {
            reqStr += entry.getKey() + "=" + entry.getValue();
            if()
        }
        return reqStr;
    }

    public static String compileRequestURL(
            String url,
            String... params
    ){
        HashMap<String,String> paramsMap = new HashMap<>();
        for(String str : params) {
            if (!str.contains("->"))
                throw new RuntimeException("compileRequestURL : bad string param... abort url compilation");
            String[]entry = str.split("->");
            paramsMap.put(entry[0],entry[1]); //no performance here
        }
        return compileRequestURL(url,paramsMap);
    }


    public static void displayMSGOnNetworkError(Context context){
        displayMSG(context,Messages.msgOnNetworkError);
    }

    public static void displayMSGOnError(Context context){
        displayMSG(context,Messages.msgOnError);
    }


    public static void displayMSG(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
