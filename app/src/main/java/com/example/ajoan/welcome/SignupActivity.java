package com.example.ajoan.welcome;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ajoan.components.CustomInputFragment;
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.FragmentInjecter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME;

public class SignupActivity extends AppCompatActivity implements CustomInputFragment.Listener {

    private String inputListener = "http:localhost:8080/input/checkout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Bundle username = new Bundle();
        username.putString("title", "Nom d'utilisateur");
        username.putString("hint", "Choisis ton nom sur Mood");
        username.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME);
        username.putString("url",inputListener);
        username.putString("reqParamName","username");

        Bundle email = new Bundle();
        email.putString("title", "Email");
        email.putString("hint", "Entre ton adresse email");
        email.putInt("type",TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.putString("url",inputListener);
        email.putString("reqParamName","email");

        List<Bundle> configs = Arrays.asList(email,username); //order matters : order on screen
        List<Fragment> fragments = new ArrayList<>();

        if (savedInstanceState != null)
            for(int i =0; i<configs.size();i++)
                fragments.add((CustomInputFragment) getFragmentManager()
                        .findFragmentByTag(this.getLocalClassName()+i));
        else
            FragmentInjecter.inject(
                    R.id.activity_signup,
                    getFragmentManager(),
                    CustomInputFragment.installConfigs(
                            configs
                    ),
                    this.getLocalClassName());
    }

}
