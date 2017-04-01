package com.example.ajoan.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joan on 01/04/2017.
 */

/**
 * The Black butler pop the wine bottle then serve it to the clients
 */
public class Butler {

    /**
     * Decapsulates the answer and serve its content to the client in the right glass (method)
     * @param mc
     * @param response */
    public static void popNserve(Context context,MoodClient mc, String response){
        try {
            JSONObject json = new JSONObject(response);

            switch (json.getInt("status")) {
                case -1: mc.onIssue(json.getInt("iscode"));
                    break;
                case 0:  mc.onReply(
                        json.getInt("rpcode"),json.optString("message"),json.optJSONObject("result"));
                    break;
                default:
                    Log.e("Butler/popNserve","Unknown response's nature. Can't process it... nan mais Allo");
                    Utils.displayMSGOnError(context);
                    break;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
}
