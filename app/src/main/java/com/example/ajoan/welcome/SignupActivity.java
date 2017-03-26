package com.example.ajoan.welcome;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.ajoan.components.CustomInputFragment;
import com.example.ajoan.components.CustomSubmitFragment;
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.FragmentInjecter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME;

public class SignupActivity
        extends AppCompatActivity implements
        CustomInputFragment.Listener,
        CustomSubmitFragment.Listener {

    private Context meGod=this;

    private String inputListener = "http:localhost:8080/input/checkout";

    public final static String USERMAIL ="email";
    public final static String USERNAME ="username";

    private Map<String,EditText> mapInputsET = new HashMap<>();
    private Map<String,TextView> mapInputsMSGTV = new HashMap<>();

    private Map<String,Boolean> mapInputsTrafficLight = new HashMap<>();

    private Bundle usernameConfig = new Bundle();
    private Bundle emailConfig = new Bundle();

    private List<Fragment> myFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameConfig.putString("title", "Nom d'utilisateur");
        usernameConfig.putString("hint", "Choisis ton nom sur Mood");
        usernameConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME);
        usernameConfig.putString("url",inputListener);
        usernameConfig.putString("reqParamName",USERNAME);
        usernameConfig.putString("rule","((?=.*[a-z])^[a-zA-Z](\\w{2,}))");
        usernameConfig.putString("manual","Un nom d'utilisateur contient au moins 3 caractères et commence par une lettre");

        emailConfig.putString("title", "Email");
        emailConfig.putString("hint", "Entre ton adresse email");
        emailConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailConfig.putString("url",inputListener);
        emailConfig.putString("reqParamName",USERMAIL);
        emailConfig.putString("rule",".+@.+");

        String inputsBaseTag ="CustomInputFragment";
        List<Bundle> inputConfigs = Arrays.asList(emailConfig,usernameConfig); //order matters : order on screen

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
        submitConfig.putString("text", "Suivant");

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
        mapInputsMSGTV.get(reqParamName).setHeight(CustomInputFragment.getMSGTVHeight(getResources()));
        mapInputsMSGTV.get(reqParamName).setText("Response: " + response.toString());
    }

    @Override
    public void onInputRequestError(String reqParamName, Exception exception) {
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
        goNext();
    }


    private void goNext(){
        Intent intent=new Intent(meGod,SignupChoosePasswordActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(SignupChoosePasswordActivity.USERMAIL,mapInputsET.get("email").getText());
        intent.putExtra(SignupChoosePasswordActivity.USERNAME,mapInputsET.get("username").getText());
        startActivity(intent);
    }


    //class end
}
