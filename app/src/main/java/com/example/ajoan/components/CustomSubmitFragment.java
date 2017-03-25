package com.example.ajoan.components;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.Map;


/**
 * Anagbla Joan */
public class CustomSubmitFragment extends Fragment {

    private Listener myListener;
    private Bundle config;

    private Button submitBtn;

    private RequestQueue queue; //private bus driver
    private boolean onTheFly = false;


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
            public void onClick(View v) {
                if(config.getString("url") != null && !onTheFly) {
                        JSONObject requestParameters = new JSONObject();
                        try {
                            for(Map.Entry<String,EditText> entry : myListener.getMapInputsET().entrySet())
                                requestParameters.put(entry.getKey(),entry.getValue().getText());
                        } catch (JSONException e) {
                            //TODO REPLACE BY E.getMYStacktrace and my own logger
                            Log.i("CustomInputFragment", "/onTextChanged", e);
                        }

                        Log.i("CustomInputFragment", "Sending request to " + config.getString("url") +
                                " with params "+requestParameters);
                        queue.add((JsonObjectRequest)
                                new JsonObjectRequest( //todo parametrize the http send methode later
                                        Request.Method.POST,
                                        config.getString("url"),
                                        requestParameters,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                onTheFly = false;
                                                Toast.makeText(getContext(),response.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                onTheFly = false;
                                                Log.d("CustomSubmitFragment","onErrorResponse",error);
                                                Toast.makeText(getContext(),"Impossible de joindre le serveur", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                        );
                        onTheFly = true;
                    }
            }
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
        Map<String,EditText> getMapInputsET();
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

}