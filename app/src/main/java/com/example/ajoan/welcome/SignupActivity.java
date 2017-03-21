package com.example.ajoan.welcome;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ajoan.components.CustomInputFragment;
import com.example.ajoan.maps.R;
import com.example.ajoan.utils.FragmentInjecter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements CustomInputFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Bundle conf1 = new Bundle();
        conf1.putString("title", "title1");
        conf1.putString("hint", "hint1");

        Bundle conf2 = new Bundle();
        conf2.putString("title", "title2");
        conf2.putString("hint", "hint2");

        List<Bundle> configs = Arrays.asList(conf1, conf2);
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
