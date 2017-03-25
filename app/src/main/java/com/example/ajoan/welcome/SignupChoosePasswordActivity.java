package com.example.ajoan.welcome;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SignupChoosePasswordActivity
        extends AppCompatActivity implements
        CustomInputFragment.Listener,
        CustomSubmitFragment.Listener{

    private Context meGod=this;

    public final static String USERMAIL ="email";
    public final static String USERNAME ="username";
    public final static String USERPASS ="pass";
    public final static String USER_PASS_CHK ="confirm";

    private final static String PASS_RULE = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})";

    private Map<String,EditText> mapInputsET = new HashMap<>();
    private Map<String,TextView> mapInputsMSGTV = new HashMap<>();

    private Map<String,Boolean> mapInputsTrafficLight = new HashMap<>();

    private String submitListener = "http:localhost:8080/input/checkout";
    private RequestQueue queue;
    private boolean onTheFly = false;

    private List<Fragment> myFragments = new ArrayList<>();

    private Bundle passConfig = new Bundle();
    private Bundle confirmConfig = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_choose_password);

        passConfig.putString("title", "Mot de passe");
        passConfig.putString("hint", "Choisis ton mot de passe");
        passConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
        passConfig.putString("reqParamName",USERPASS);//mandatory if we want the submitBtn to recognize it for submission
        passConfig.putString("rule",PASS_RULE);
        passConfig.putString("manual","Un mot de passe contient au moins 8 caractères dont au moins un chiffre, une lettre Majuscule et une lettre minuscule");
        mapInputsTrafficLight.put(USERPASS,false);

        confirmConfig.putString("title", "Vérification");
        confirmConfig.putString("hint", "Vérification du mot de passe");
        confirmConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
        confirmConfig.putString("reqParamName",USER_PASS_CHK); //mandatory if we want the submitBtn to recognize it for submission
        confirmConfig.putString("rule",PASS_RULE);
        mapInputsTrafficLight.put(USER_PASS_CHK,false);

        String inputsBaseTag ="CustomInputFragment";
        List<Bundle> inputConfigs = Arrays.asList(passConfig,confirmConfig); //order matters : order on screen

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
        submitConfig.putString("text", "Inscription");

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
    public void onInputRequestResponse(String reqParamName, JSONObject response) {
        mapInputsMSGTV.get(reqParamName).setText("Response: " + response.toString());
    }

    @Override
    public void onInputRequestError(String reqParamName, Exception exception) {
        mapInputsMSGTV.get(reqParamName).setText("Response: " + exception.toString());//debug todo comment
    }

    @Override
    public void setMapInputsTrafficLight(String reqParamName, boolean light) {
        mapInputsTrafficLight.put(reqParamName,light);
    }


    @Override
    public void submit() {
        for(Boolean ok : mapInputsTrafficLight.values())
            if(!ok) {
                Toast.makeText(meGod, "Au moins un champ est mal rempli", Toast.LENGTH_SHORT).show();
                return;
            }

        if(! (mapInputsET.get(USERPASS).getText().toString())               .equals
                ((mapInputsET.get(USER_PASS_CHK).getText()).toString())
                ) {
            mapInputsMSGTV.get(USER_PASS_CHK).setText("La vérification ne correspond pas au mot de passe");
            return;
        }

        JSONObject requestParameters = new JSONObject();
        if(!onTheFly)
            try {
                requestParameters.put(USERMAIL,getIntent().getStringExtra(USERMAIL));
                requestParameters.put(USERNAME,getIntent().getStringExtra(USERNAME));
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
        Intent intent=new Intent(meGod,SignupChoosePasswordActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(USERMAIL,getIntent().getStringExtra(USERMAIL));
        intent.putExtra(USERNAME,getIntent().getStringExtra(USERNAME));
        startActivity(intent);
    }


    //class end
}
