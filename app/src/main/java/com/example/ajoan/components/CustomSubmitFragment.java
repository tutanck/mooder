package com.example.ajoan.components;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.ajoan.maps.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Anagbla Joan */
public class CustomSubmitFragment extends Fragment {

    private Listener myListener;
    private Bundle config;

    private Button submitBtn;

    public CustomSubmitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_custom_submit, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        config = getArguments();

        submitBtn = (Button) view.findViewById(R.id.submit);
        if (config.getString("text") != null)
            submitBtn.setText(config.getString("text"));

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { myListener.submit(); }
        });
    }


    public static LinkedHashMap<Fragment,Bundle> installConfigs(
            List<Bundle> configs
    ){
        LinkedHashMap<Fragment,Bundle> map = new LinkedHashMap<>();
        for(Bundle bundle : configs)
            map.put(new CustomSubmitFragment(),bundle);
        return map;
    }


    public interface Listener {
        void submit();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener)
            myListener = (Listener) context;
        else
            throw new RuntimeException(context.toString()+" must implement Listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myListener = null;
    }

}