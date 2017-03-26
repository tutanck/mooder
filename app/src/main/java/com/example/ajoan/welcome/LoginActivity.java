package com.example.ajoan.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ajoan.components.CustomInputFragment;
import com.example.ajoan.components.CustomSubmitFragment;
import com.example.ajoan.maps.MapsActivity;
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.FragmentInjecter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME;

public class LoginActivity
        extends AppCompatActivity implements
        CustomInputFragment.Listener,
        CustomSubmitFragment.Listener {

    private Context meGod=this;

    private String pageTitleText ="Connexion";

    public final static String USERNAME ="username";
    public final static String USERPASS ="pass";

    private final static String PASS_RULE = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})";

    private Map<String,EditText> mapInputsET = new HashMap<>();
    private Map<String,TextView> mapInputsMSGTV = new HashMap<>();

    private Map<String,Boolean> mapInputsTrafficLight = new HashMap<>();

    private String inputListener = "http:localhost:8080/input/checkout";
    private String submitListener = "http:localhost:8080/input/checkout";
    private RequestQueue queue;
    private boolean onTheFly = false;

    private Bundle usernameConfig = new Bundle();
    private Bundle passConfig = new Bundle();

    private List<Fragment> myFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
        pageTitle.setText(pageTitleText);

        usernameConfig.putString("title", "Identifiant");
        usernameConfig.putString("hint", "Nom d'utilisateur ou email");
        usernameConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME);
        usernameConfig.putString("url",inputListener);
        usernameConfig.putString("reqParamName",USERNAME);

        passConfig.putString("title", "Mot de passe");
        passConfig.putString("hint", "Entre ton mot de passe");
        passConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
        passConfig.putString("reqParamName",USERPASS);//mandatory if we want the submitBtn to recognize it for submission
        passConfig.putString("rule",PASS_RULE);
        passConfig.putString("manual","Il faut au moins 8 caractères dont au moins un chiffre, une Majuscule et une minuscule");


        String inputsBaseTag ="CustomInputFragment";
        List<Bundle> inputConfigs = Arrays.asList(usernameConfig,passConfig); //order matters : order on screen

        if (savedInstanceState != null)
            for(int i =0; i<inputConfigs.size();i++)
                myFragments.add(getSupportFragmentManager().findFragmentByTag(inputsBaseTag+i));
        else
            FragmentInjecter.inject(
                    R.id.activity_signup,
                    getSupportFragmentManager(),
                    CustomInputFragment.installConfigs(inputConfigs),
                    inputsBaseTag
            );


        Bundle submitConfig = new Bundle();
        submitConfig.putString("text", "Let's mood");

        String submitBaseTag ="CustomSubmitFragment";
        List<Bundle> submitConfigs = Arrays.asList(submitConfig); //order matters : order on screen

        if (savedInstanceState != null)
            for(int i =0; i<submitConfigs.size();i++)
                myFragments.add(getSupportFragmentManager().findFragmentByTag(submitBaseTag+i));
        else
            FragmentInjecter.inject(
                    R.id.activity_signup,
                    getSupportFragmentManager(),
                    CustomSubmitFragment.installConfigs(submitConfigs),
                    submitBaseTag
            );

        queue= Volley.newRequestQueue(meGod);
    }



    @Override
    public void setInputET(String reqParamName, EditText input) { mapInputsET.put(reqParamName,input); }

    @Override
    public void setMsgTV(String reqParamName, TextView tv) {
        mapInputsMSGTV.put(reqParamName,tv);
    }

    @Override
    public void setMapInputsTrafficLight(String reqParamName, boolean light) {
        mapInputsTrafficLight.put(reqParamName,light);
    }


    @Override
    public void onInputRequestResponse(String reqParamName, JSONObject response) {
        setMapInputsTrafficLight(reqParamName,true);
        mapInputsMSGTV.get(reqParamName).setHeight(0);
        mapInputsMSGTV.get(reqParamName).setHeight(CustomInputFragment.getMSGTVHeight(getResources()));
        mapInputsMSGTV.get(reqParamName).setText("Response: " + response.toString());
    }

    @Override
    public void onInputRequestError(String reqParamName, Exception exception) {
        setMapInputsTrafficLight(reqParamName,true);
        mapInputsMSGTV.get(reqParamName).setHeight(0);
        mapInputsMSGTV.get(reqParamName).setHeight(CustomInputFragment.getMSGTVHeight(getResources()));
        mapInputsMSGTV.get(reqParamName).setText("Response: " + exception.toString());//debug todo comment
    }

    @Override
    public void submit() {
        for(Boolean ok : mapInputsTrafficLight.values())
            if(!ok) {
                Toast.makeText(meGod, "Au moins un champ est mal rempli", Toast.LENGTH_SHORT).show();
                return;
            }

        JSONObject requestParameters = new JSONObject();
        if(!onTheFly)
            try {
                requestParameters.put(USERNAME,mapInputsET.get(USERNAME).getText());
                requestParameters.put(USERPASS,mapInputsET.get(USERPASS).getText());

                Log.i("CustomInputFragment", "Sending request to " + submitListener + " with params "+requestParameters);

                queue.add((JsonObjectRequest)
                        new JsonObjectRequest(
                                Request.Method.POST,
                                submitListener,
                                requestParameters,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        onTheFly = false;
                                        goNext();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        onTheFly = false;
                                        Log.d("CustomSubmitFragment","onErrorResponse",error);
                                        Toast.makeText(meGod,"Impossible de joindre le serveur", Toast.LENGTH_SHORT).show();
                                        goNext();//todo rem
                                    }
                                })
                );

                onTheFly = true;

            } catch (JSONException e) {
                //TODO REPLACE BY E.getMYStacktrace and my own logger
                Log.i("CustomInputFragment", "/onTextChanged", e);
            }
    }



    private void goNext(){
        Intent intent=new Intent(meGod,MapsActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //todo instal id and username in file
        startActivity(intent);
        finish(); //finish this activity
    }


    //class end
}