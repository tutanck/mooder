package com.example.ajoan.components;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ajoan.maps.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Anagbla Joan */
public class CustomInputFragment extends Fragment {

    private Listener myListener;
    private Bundle config;

    private TextView inputTitleTV;
    private EditText inputET;
    private TextView inputMsgTV;

    private RequestQueue queue; //private bus driver
    private int requestCounter = 0;


    public CustomInputFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_custom_input, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        config =  getArguments();

        //title tv
        inputTitleTV = (TextView) view.findViewById(R.id.input_title);
        if (config.getString("title") != null)
            inputTitleTV.setText(config.getString("title"));

        //et
        inputET=(EditText)view.findViewById(R.id.input_et);
        if (config.getString("hint") != null)
            inputET.setHint(config.getString("hint"));
        if (config.getInt("type") !=0)
            inputET.setRawInputType(config.getInt("type"));

        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                myListener.setMapInputsTrafficLight(determineReqParamName(),false);

                String rule = config.getString("rule"); //rule says whether the charseq is sendable or not
                if(rule!=null){
                    Pattern pattern = Pattern.compile(rule);
                    Matcher matcher = pattern.matcher(s.toString());
                    if(!matcher.matches()) {
                        Log.i("CustomInputFragment","no sendable input : "+rule+"#"+s.toString());
                        String manual = config.getString("manual");
                        if(manual!=null)
                            inputMsgTV.setText(manual);
                        return; //exit from onTextChanged without sending the charseq
                    }
                }

                myListener.setMapInputsTrafficLight(determineReqParamName(),true);
                inputMsgTV.setText(""); //Reset/clear warning message if it passes the rule

                if (config.getString("url") != null)
                    try {
                        Log.i("CustomInputFragment", "Sending request to " + config.getString("url") +
                                " with param {" +  determineReqParamName() + ":" + s.toString() + "}");
                        queue.cancelAll(requestCounter); //cancel the previous request
                        queue.add((JsonObjectRequest)
                                new JsonObjectRequest(
                                        Request.Method.GET, config.getString("url"),
                                        new JSONObject().put(
                                                determineReqParamName()
                                                , s.toString()
                                        ),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("CustomInputFragment","onErrorResponse : '"+response+"'");
                                                myListener.onInputRequestResponse(determineReqParamName(),response);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("CustomInputFragment","onErrorResponse : '"+error+"'",error);
                                                myListener.onInputRequestError(determineReqParamName(),error);
                                            }
                                        }).setTag(requestCounter++)
                        );
                    } catch (JSONException e) {
                        //TODO REPLACE BY E.getMYStacktrace and my own logger
                        Log.i("CustomInputFragment", "/onTextChanged", e);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //msg tv
        inputMsgTV = (TextView) view.findViewById(R.id.input_msg);
        inputMsgTV.setText("");//reset message to no message (default)

        //And finally share the created views with his listener
        myListener.setInputET(determineReqParamName(),inputET);
        myListener.setMsgTV(determineReqParamName(),inputMsgTV);
        myListener.setMapInputsTrafficLight(determineReqParamName(),false);
    }


    public static LinkedHashMap<Fragment,Bundle> installConfigs(
            List<Bundle> configs
    ){
        LinkedHashMap<Fragment,Bundle> map = new LinkedHashMap<>();
        for(Bundle bundle : configs)
            map.put(new CustomInputFragment(),bundle);
        return map;
    }


    /**
     * To extends the fragment's listener requirement  */
    public interface Listener {
        void setInputET(String reqParamName, EditText input);
        void setMsgTV(String reqParamName, TextView tv);

        void setMapInputsTrafficLight(String reqParamName, boolean light);

        void onInputRequestResponse(String reqParamName, JSONObject response);
        void onInputRequestError(String reqParamName,  Exception exception);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            myListener = (Listener) context;
            queue = Volley.newRequestQueue(context);
        }else
            throw new RuntimeException(context.toString()+" must implement Listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myListener = null;
    }

    //todo find better if possible than returning ""
    private String determineReqParamName(){
        return config.getString("reqParamName")!=null ?
                config.getString("reqParamName") :
                config.getString("title")!=null ?
                        config.getString("title") : "";//""->not a request parameter
    }

}