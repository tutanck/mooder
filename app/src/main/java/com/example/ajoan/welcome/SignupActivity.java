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
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.AppRouting;
import com.example.ajoan.utils.FormManager;
import com.example.ajoan.utils.Utils;

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
    private Map<String, Boolean> formValidationMap = new HashMap<>();
    private Button submitBtn;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        queue = ((MyApp)getApplication()).queue;

        RelativeLayout title = (RelativeLayout)findViewById(R.id.title);
        RelativeLayout input1 = (RelativeLayout)findViewById(R.id.input1);
        RelativeLayout input2 = (RelativeLayout)findViewById(R.id.input2);
        RelativeLayout submit = (RelativeLayout)findViewById(R.id.submit);

        FormManager.initTextView((TextView) title.findViewById(R.id.pageTitle),"Comment te reconnaître ?");

        (submitBtn = FormManager.initButton((Button) submit.findViewById(R.id.submitBtn),"Suivant",false)
        ).setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View v) { submit(); }
        });

        try {
            inputsMap.put(USERMAIL, FormManager.initInput(
                    (TextView) input1.findViewById(R.id.inputTitle),
                    "Email",
                    (EditText) input1.findViewById(R.id.inputET),
                    "Entre ton adresse email",
                    TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                    (ProgressBar) input1.findViewById(R.id.inputPB),
                    (TextView) input1.findViewById(R.id.inputMSG)
                    ).put("url", AppRouting.serverAdr + AppRouting.emailChk)
                            .put("rule", ".+@.+")
            );

            inputsMap.put(USERNAME, FormManager.initInput(
                    (TextView) input2.findViewById(R.id.inputTitle),
                    "Nom d'utilisateur",
                    (EditText) input2.findViewById(R.id.inputET),
                    "Choisis ton nom sur Mood",
                    TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME,
                    (ProgressBar) input2.findViewById(R.id.inputPB),
                    (TextView) input2.findViewById(R.id.inputMSG)
                    ).put("url", AppRouting.serverAdr + AppRouting.usernameChk)
                            .put("rule", "((?=.*[a-z])^[a-zA-Z](\\w{2,}))")
                            .put("manual", "Un nom d'utilisateur contient au moins 3 caractères et commence par une lettre")
            );

            for(final Map.Entry<String,JSONObject> entry : inputsMap.entrySet() ) {

                final EditText inputET=((EditText) entry.getValue().get("input"));
                final TextView msgTV=((TextView) entry.getValue().get("msg"));
                final ProgressBar checkingPB=((ProgressBar) entry.getValue().get("pb"));

                formValidationMap.put(entry.getKey(),false);

                inputET.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                queue.cancelAll(entry.getKey()); //cancel all the previous this input related requests

                                try {
                                    if (!FormManager.validInput(formValidationMap,entry.getKey(),entry.getValue(),meGod,submitBtn))
                                        return; //warning message already displayed, cant go further

                                    FormManager.showProgressBar(checkingPB);
                                    FormManager.showMsgTV(msgTV,"Vérification en cours",getResources());

                                    String reqStr = Utils.compileRequestURL(
                                            entry.getValue().getString("url"),
                                            new String[]{entry.getKey() + "->" + s.toString()}
                                    );
                                    Log.i("SignupActivity", "/onTextChanged : Sending this request:\n  -->" + reqStr);

                                    queue.add((StringRequest) new StringRequest(
                                            Request.Method.GET,
                                            reqStr,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d("CustomInputFragment", "onErrorResponse : '" + response + "'");
                                                    FormManager.dropProgressBar(checkingPB);
                                                    FormManager.dropMsgTV(msgTV);
                                                    FormManager.validFormOnInputChange(formValidationMap,entry.getKey(),submitBtn);
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("CustomInputFragment", "onErrorResponse : '" + error + "'", error);

                                                    FormManager.dropProgressBar(checkingPB);
                                                    FormManager.dropMsgTV(msgTV);
                                                    Toast.makeText(meGod,Utils.msgOnNetworkError,Toast.LENGTH_LONG).show();

                                                    //todo rem
                                                    FormManager.validFormOnInputChange(formValidationMap,entry.getKey(),submitBtn);
                                                }
                                            }).setTag(entry.getKey())
                                    );
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
            }
        } catch (JSONException e) { throw new RuntimeException(e); }
    }



    private void submit() {
        try {

            Bundle b = new Bundle();

            b.putString(ChoosePasswordActivity.USERMAIL,
                    ((EditText) inputsMap.get("email").get("input")).getText().toString());

            b.putString(ChoosePasswordActivity.USERNAME,
                    ((EditText) inputsMap.get("username").get("input")).getText().toString());

            Utils.nextActivity(meGod, ChoosePasswordActivity.class, b, null, null, null, false);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //class end
}
