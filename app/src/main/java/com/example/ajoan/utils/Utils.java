package com.example.ajoan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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

    public final static String msgOnNetworkError= "Impossible de joindre le serveur! " +
            "Merci de vérifier votre connexion internet ou essayez plus tard";


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

    public static int getMSGTVHeight(Resources resources){
        return (int)Utils.pixelsInDP(36,resources);
    }

    public static void nextActivity(
            Context context,
            Class activityClass,
            Bundle bundle,
            String action,
            String type,
            boolean finish
    ){
        Intent intent=new Intent(context,activityClass);

        intent.setAction(action!=null?action:Intent.ACTION_SEND);

        intent.setType(type!=null?type:"text/plain");

        if(bundle!=null)
            intent.putExtras(bundle);

        context.startActivity(intent);

        if(finish && context instanceof Activity)
            ((Activity)context).finish();
    }

    public static String compileRequestURL(
            String url,
            Map<String,String> params
    ){
        String reqStr = url;
        if(params!=null && params.size()>0)
            reqStr+="?";
        for(Map.Entry<String,String> entry : params.entrySet())
            reqStr+=entry.getKey()+"="+entry.getValue();
        return reqStr;
    }

    public static String compileRequestURL(
            String url,
            String[] params
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
        Toast.makeText(context, msgOnNetworkError, Toast.LENGTH_LONG).show();
    }

}
