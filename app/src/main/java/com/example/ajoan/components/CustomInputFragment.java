package com.example.ajoan.components;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android./**support.v4.*/app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ajoan.maps.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Anagbla Joan */
public class CustomInputFragment extends Fragment {

    private Listener myListener;

    private TextView inputTitleTV;
    private EditText inputET;
    private TextView inputMsgTV;
    private Bundle config;


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
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //msg tv
        inputMsgTV = (TextView) view.findViewById(R.id.input_msg);

    }


    public static LinkedHashMap<Fragment,Bundle> installConfigs(
            List<Bundle> configs
    ){
        LinkedHashMap<Fragment,Bundle> map = new LinkedHashMap<>();
        for(Bundle bundle : configs)
            map.put(new CustomInputFragment(),bundle);
        return map;
    }


    public interface Listener { }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener)
            myListener = (Listener) context;
        else
            throw new RuntimeException(
                    context.toString()+" must implement Listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myListener = null;
    }

}