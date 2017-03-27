package com.example.ajoan.utils;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Joan on 27/03/2017.
 */

public class PostMan {


    private static RequestQueue queue; //init que with app


    public static RequestQueue sendRequest(
            final PostManClient client,
            String url,
            Map<String,String> params,
            StringRequest stringRequest
    ){
        String reqStr = url;
        if(params.size()>0)
            reqStr+="?";
        for(Map.Entry<String,String> entry : params.entrySet())
            reqStr+=entry.getKey()+"="+entry.getValue();

        Log.i("CustomInputFragment", "/submit : Sending this request:\n  -->" + reqStr);

        queue.add(stringRequest);

        (StringRequest) new StringRequest(
                method,
                reqStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CustomInputFragment", "onErrorResponse : '" + response + "'");
                        client.onResquestResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CustomInputFragment", "onErrorResponse : '" + error + "'", error);
                        client.onResquestError(error);
                    }
                }).setTag(requestsTAG)

        return queue;
    }

    public interface PostManClient{
        void onResquestResponse(String response);
        void onResquestError(VolleyError error);
    }

}
