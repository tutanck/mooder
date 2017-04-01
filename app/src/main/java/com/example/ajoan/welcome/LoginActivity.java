package com.example.ajoan.welcome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ajoan.MyApp;
import com.example.ajoan.maps.MapsActivity;
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.AppRouting;
import com.example.ajoan.utils.FormManager;
import com.example.ajoan.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME;

public class LoginActivity extends AppCompatActivity {

    private Context meGod = this;

    private RequestQueue queue;
    private boolean onTheFly = false;
    private String submitListener = AppRouting.serverAdr+AppRouting.signin;

    public final static String DID ="did";
    private String android_id;

    //external inputs
    public final static String USERNAME = "username";
    private final static String USERPASS ="pass";

    private final static String NON_EMPTY_RULE = ".+";


    private Map<String, JSONObject> inputsMap = new HashMap<>();
    private Map<String, Boolean> formValidationMap = new HashMap<>();
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        queue = ((MyApp)getApplication()).queue;

        RelativeLayout title = (RelativeLayout)findViewById(R.id.title);
        RelativeLayout input1 = (RelativeLayout)findViewById(R.id.input1);
        RelativeLayout input2 = (RelativeLayout)findViewById(R.id.input2);
        RelativeLayout submit = (RelativeLayout)findViewById(R.id.submit);
        LinearLayout linksLayout = (LinearLayout)findViewById(R.id.linksLayout);

        FormManager.initTextView((TextView) title.findViewById(R.id.pageTitle),"Bienvenu");

        (submitBtn = FormManager.initButton((Button) submit.findViewById(R.id.submitBtn),"Let's mood",false)
        ).setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View v) { submit(); }
        });


        TextView link1 = new TextView(meGod);
        link1.setText("Nouveau sur Mood");
        link1.setTextColor(ContextCompat.getColor(meGod, R.color.colorAccent));
        link1.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
        link1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                Utils.intent(new Intent(meGod, SignupActivity.class), null, null)
                        );
                        finish();
                    }
                }
        );
        linksLayout.addView(link1);

        TextView link2 = new TextView(meGod);
        link2.setText("  |  ");
        linksLayout.addView(link2);

        TextView link3 = new TextView(meGod);
        link3.setText("Mot de passe oublié");
        link3.setTextColor(ContextCompat.getColor(meGod, R.color.colorAccent));
        link3.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
        link3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                Utils.intent(new Intent(meGod, SignupActivity.class), null, null)
                        );
                        finish();
                    }
                }
        );
        linksLayout.addView(link3);

        try {
            inputsMap.put(USERNAME, FormManager.initInput(
                    (TextView) input1.findViewById(R.id.inputTitle),
                    "Identifiant",
                    (EditText) input1.findViewById(R.id.inputET),
                    "Nom d'utilisateur ou email",
                    TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME,
                    (ProgressBar) input1.findViewById(R.id.inputPB),
                    (TextView) input1.findViewById(R.id.inputMSG)
                    ).put("rule",NON_EMPTY_RULE)
            );

            inputsMap.put(USERPASS, FormManager.initInput(
                    (TextView) input2.findViewById(R.id.inputTitle),
                    "Mot de passe",
                    (EditText) input2.findViewById(R.id.inputET),
                    "Entre ton mot de passe",
                    TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD,
                    (ProgressBar) input2.findViewById(R.id.inputPB),
                    (TextView) input2.findViewById(R.id.inputMSG)
                    ).put("rule",NON_EMPTY_RULE)
            );

            String storedUname = getIntent().getStringExtra(USERNAME);
            if(storedUname!=null) {
                (((EditText) inputsMap.get(USERNAME).get("input"))).setText(storedUname);
                formValidationMap.put(USERNAME,true);
            }

            formValidationMap.put(USERPASS,false);

            for(final Map.Entry<String,JSONObject> entry : inputsMap.entrySet() ) {

                ((EditText) entry.getValue().get("input")).addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (!FormManager.validInput(null, entry.getKey(), entry.getValue(), meGod, submitBtn))
                                        return; //warning message already displayed, cant go further

                                    FormManager.validFormOnInputChange(formValidationMap,entry.getKey(),submitBtn);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
            }

            android_id = Settings.Secure.getString(meGod.getContentResolver(),Settings.Secure.ANDROID_ID);

        } catch (JSONException e) { throw new RuntimeException(e); }
    }



    private void submit() {
        if (!onTheFly){
            FormManager.disableButton(submitBtn);

            try {
                String reqStr = Utils.compileRequestURL(submitListener,
                        USERNAME+"->"+((EditText)inputsMap.get(USERNAME).get("input")),
                        USERPASS+"->"+((EditText)inputsMap.get(USERPASS).get("input")).getText(),
                        DID+"->"+android_id
                );

                Log.i("LoginActivity", "/submit : Sending this request:\n  -->" + reqStr);

                queue.add(new StringRequest(
                        Request.Method.POST,
                        reqStr,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                onTheFly = false;
                                Log.i("VolleyCallResponse","response : "+response);
                                startActivity(
                                        Utils.intent(new Intent(meGod, MapsActivity.class), null, null)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                );

                                //todo verifier qu il clear bien les previous task ::later
                                //todo store user id and response content username

                                //todo finish all prev
                                FormManager.enableButton(submitBtn);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onTheFly = false;
                                Toast.makeText(meGod,Utils.msgOnNetworkError,Toast.LENGTH_LONG).show();
                                FormManager.enableButton(submitBtn);

                                //todo rem
                                startActivity(
                                        Utils.intent(new Intent(meGod, MapsActivity.class), null, null)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                );
                            }
                        }));
                onTheFly = true;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //class end
}
