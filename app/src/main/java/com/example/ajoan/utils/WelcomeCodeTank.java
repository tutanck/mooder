package com.example.ajoan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joan on 26/03/2017.
 */

public class WelcomeCodeTank {


    public static JSONObject initInput(
            TextView titleTV,
            String title,
            EditText inputET,
            String hint,
            int type,
            TextView msgTV
    ) throws JSONException {
        titleTV.setText(title);
        inputET.setHint(hint);
        inputET.setInputType(type);
        msgTV.setText("");
        msgTV.setHeight(0);
        return new JSONObject().put("input",inputET).put("title",titleTV).put("msg",msgTV);
    }


    public static TextView initTextView(
            TextView textView,
            String text
    ){
        textView.setText(text);
        return textView;
    }

    public static Button initButton(
            Button button,
            String text
    ){
        button.setText(text);
        return button;
    }


    public static void goNext(
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

}
