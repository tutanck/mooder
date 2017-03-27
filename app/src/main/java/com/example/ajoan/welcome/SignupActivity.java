package com.example.ajoan.welcome;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.AppRouting;
import com.example.ajoan.utils.Utils;
import com.example.ajoan.utils.WelcomeCodeTank;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME;

public class SignupActivity extends AppCompatActivity {

    private Context meGod = this;

    private final static String USERMAIL = "email";
    private final static String USERNAME = "username";

    private Map<String, JSONObject> inputsMap = new HashMap<>();
    private Button submit;

    private int unlocked = 0;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        WelcomeCodeTank.initTextView((TextView) findViewById(R.id.pageTitle),"Comment te reconnaître ?");

        (submit = WelcomeCodeTank.initButton((Button) findViewById(R.id.submit),"Suivant",false)
        ).setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View v) { submit(); }
        });

        try {
            inputsMap.put(USERMAIL, WelcomeCodeTank.initInput(
                    (TextView) findViewById(R.id.input_title1),
                    "Email",
                    (EditText) findViewById(R.id.input_et1),
                    "Entre ton adresse email",
                    TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                    (TextView) findViewById(R.id.input_msg1)
                    ).put("url", AppRouting.serverAdr + AppRouting.usernameChk)
                            .put("rule", "((?=.*[a-z])^[a-zA-Z](\\w{2,}))")
                            .put("manual", "Un nom d'utilisateur contient au moins 3 caractères et commence par une lettre")
            );
            inputsMap.put(USERNAME, WelcomeCodeTank.initInput(
                    (TextView) findViewById(R.id.input_title2),
                    "Nom d'utilisateur",
                    (EditText) findViewById(R.id.input_et2),
                    "Choisis ton nom sur Mood",
                    TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME,
                    (TextView) findViewById(R.id.input_msg2)
                    ).put("url", AppRouting.serverAdr + AppRouting.emailChk)
                            .put("rule", ".+@.+")
            );

            for(final Map.Entry<String,JSONObject> entry : inputsMap.entrySet() )
                ((EditText)entry.getValue().get("input")).addTextChangedListener(
                        new TextWatcher() {
                            @Override  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if(WelcomeCodeTank.validInput(entry.getValue(),meGod))
                                        unlocked++;
                                    
                                    queue.cancelAll(entry.getValue().getString("tag")); //cancel all the previous requests

                                    String reqStr = Utils.compileRequestURL(
                                            entry.getValue().getString("url"),
                                            new String[]{ entry.getKey()+"->"+s.toString() }
                                    );
                                    Log.i("SignupActivity", "/onTextChanged : Sending this request:\n  -->" + reqStr);

                                    queue.add((StringRequest) new StringRequest(
                                            Request.Method.GET,
                                            reqStr,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d("CustomInputFragment", "onErrorResponse : '" + response + "'");

                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("CustomInputFragment", "onErrorResponse : '" + error + "'", error);

                                                }
                                            }).setTag(entry.getKey())
                                    );
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override public void afterTextChanged(Editable s) {}
                        });

        } catch (JSONException e) { throw new RuntimeException(e); }
    }



    private void submit() {
        Bundle b = new Bundle();
        try {
            b.putString(ChoosePasswordActivity.USERMAIL,
                    ((EditText) inputsMap.get("email").get("input")).getText().toString());
            b.putString(ChoosePasswordActivity.USERNAME,
                    ((EditText) inputsMap.get("username").get("input")).getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Utils.nextActivity(meGod, ChoosePasswordActivity.class, b, null, null, false);
    }

    //class end
}
