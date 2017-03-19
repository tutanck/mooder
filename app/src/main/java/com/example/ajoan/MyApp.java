package com.example.ajoan;

import android.app.Application;
import android.location.Location;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AJoan on 13/12/2016. */
public class MyApp extends Application{

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    private Map<String,JSONObject> eventsMap;
    public Map<String,JSONObject> getEventsMap(){return eventsMap;}
    public void setEventsMap(Map<String,JSONObject> eventsMap) {this.eventsMap = eventsMap;}

    public Date refreshDate = null;

    public Location mLastLocation;

    public int radius= 1000000; //1000km
    public int rows = 1500; // max nb events

    public int timeLaps = 15;

    //Pas de constructeur , le syteme va créer une instance singleton de l'application


}
