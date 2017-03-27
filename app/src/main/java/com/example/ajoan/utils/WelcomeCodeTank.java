package com.example.ajoan.utils;

import android.content.Context;
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
            String text,
            boolean enabled
    ){
        button.setText(text);
        button.setEnabled(enabled);
        return button;
    }


    public static boolean validInput(
            JSONObject inputConf,
            Context context
    ) throws JSONException {
        if(inputConf.has("rule")) {
            TextView msgTV = (TextView) inputConf.get("msg");
            EditText inputET = (EditText) inputConf.get("input");
            if (!Utils.matchRule(inputConf.getString("rule"), inputET.getText().toString())) {
                if (inputConf.has("manual")) {
                    msgTV.setHeight(Utils.getMSGTVHeight(context.getResources()));
                    msgTV.setText(inputConf.getString("manual"));
                }
                return false;
            }
            msgTV.setHeight(0);
            msgTV.setText(""); //Reset/clear warning message if it passes the rule
        }
        return true;
    }




}
