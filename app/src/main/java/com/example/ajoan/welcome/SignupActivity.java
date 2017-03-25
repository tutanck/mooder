package com.example.ajoan.welcome;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

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

public class SignupActivity extends AppCompatActivity implements
        CustomInputFragment.Listener,CustomSubmitFragment.Listener {

    private String inputListener = "http:localhost:8080/input/checkout";

    private Map<String,EditText> mapInputsET = new HashMap<>();
    private Map<String,TextView> mapInputsTitleTV = new HashMap<>();
    private Map<String,TextView> mapInputsMSGTV = new HashMap<>();

    private List<Fragment> myFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Bundle usernameConfig = new Bundle();
        usernameConfig.putString("title", "Nom d'utilisateur");
        usernameConfig.putString("hint", "Choisis ton nom sur Mood");
        usernameConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME);
        usernameConfig.putString("url",inputListener);
        usernameConfig.putString("reqParamName","username");
        usernameConfig.putString("rule","((?=.*[a-z])^[a-zA-Z](\\w{2,}))");
        usernameConfig.putString("manual","Un nom d'utilisateur contient au moins 3 caractères et commence par une lettre");

        Bundle emailConfig = new Bundle();
        emailConfig.putString("title", "Email");
        emailConfig.putString("hint", "Entre ton adresse email");
        emailConfig.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailConfig.putString("url",inputListener);
        emailConfig.putString("reqParamName","email");
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
        submitConfig.putString("url",inputListener);//todo change listener

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
    public void setInputET(String reqParamName, EditText input) {
        mapInputsET.put(reqParamName,input);
    }

    @Override
    public void setTitleTV(String reqParamName, TextView tv) {
        mapInputsTitleTV.put(reqParamName,tv);
    }

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

    public Map<String, EditText> getMapInputsET() {
        return mapInputsET;
    }
}
