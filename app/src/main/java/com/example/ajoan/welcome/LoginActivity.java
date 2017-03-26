package com.example.ajoan.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ajoan.components.CustomInputFragment;
import com.example.ajoan.components.CustomSubmitFragment;
import com.example.ajoan.maps.MapsActivity;
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.AppRouting;
import com.example.ajoan.utils.FragmentInjecter;

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

    public final static String DID ="did";
    public final static String USERNAME ="username";
    public final static String USERPASS ="pass";

    private final static String UNAME_RULE = "((?=.*[a-zA-Z0-9]).{3,})";
    private final static String PASS_RULE = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})";

    private String android_id;

    private Map<String,EditText> mapInputsET = new HashMap<>();
    private Map<String,TextView> mapInputsMSGTV = new HashMap<>();

    private Map<String,Boolean> mapInputsTrafficLight = new HashMap<>();

    private String submitListener = AppRouting.serverAdr+AppRouting.signin;
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
        usernameConfig.putString("reqParamName",USERNAME);
        usernameConfig.putString("rule",UNAME_RULE); //fictive rule to get unblocked by the rule checker of cif(customInputFragment)

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

        android_id = Settings.Secure.getString(meGod.getContentResolver(),Settings.Secure.ANDROID_ID);
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
    public void onInputRequestResponse(String reqParamName, String response) { }

    @Override
    public void onInputRequestError(String reqParamName, Exception exception) { }

    @Override
    public void submit() {
        for(Boolean ok : mapInputsTrafficLight.values())
            if(!ok) {
                Toast.makeText(meGod, "Au moins un champ est mal rempli", Toast.LENGTH_SHORT).show();
                return;
            }

        if(!onTheFly){
                String reqStr = submitListener + "?"
                        + USERNAME + "=" + mapInputsET.get(USERNAME).getText()
                        +"&"+ USERPASS + "=" + mapInputsET.get(USERPASS).getText()
                        +"&"+ DID + "=" + android_id;

                Log.i("ChoosePasswordActivity", "/submit : Sending this request:\n  -->" + reqStr);

                queue.add(new StringRequest(
                        Request.Method.POST,
                        reqStr,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                onTheFly = false;
                                Log.i("VolleyCallResponse","response : "+response);
                                goNext();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onTheFly = false;
                                Toast.makeText(meGod, "Impossible de joindre le serveur! " +
                                                "Merci de vérifier votre connexion internet ou essayez plus tard",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }));
                onTheFly = true;
            }
    }



    private void goNext(){
        Intent intent=new Intent(meGod,MapsActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //todo instal id and username in file
        startActivity(intent);
        //finish(); //finish this activity
    }


    //class end
}
