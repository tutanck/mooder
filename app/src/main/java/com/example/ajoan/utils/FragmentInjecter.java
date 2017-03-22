package com.example.ajoan.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.ajoan.components.CustomInputFragment;
import com.example.ajoan.maps.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joan on 21/03/2017.
 */

public class FragmentInjecter {

    public static void inject(
            int activityID,
            FragmentManager fragmentManager,
            LinkedHashMap<Fragment,Bundle> fragmentConfigs,
            String activityName
    ){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        int i=0; //i unique for an injection session
        for(Map.Entry<Fragment,Bundle> entry : fragmentConfigs.entrySet()) {
            entry.getKey().setArguments(entry.getValue());
            fragmentTransaction.add(activityID, entry.getKey(),activityName+i++);
        }

        fragmentTransaction.commit();
    }
}
