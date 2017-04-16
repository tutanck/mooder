package com.example.ajoan.utils.reqstr;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joan on 16/04/2017.
 */

public class ReQstr {
    private String serverAdr;
    private String appRouterUrl;
    private RequestQueue requestQueue;
    private JSONObject router;
    private ReQstr me =this;

    public ReQstr(
            String serverRootUrl,
            String appRouterUrl,
            RequestQueue requestQueue,
            Response.ErrorListener errorListener
    ){
        this.serverAdr = serverRootUrl;
        this.appRouterUrl = appRouterUrl;
        this.requestQueue = requestQueue;

        requestQueue.add(
                new StringRequest(
                        com.android.volley.Request.Method.GET,
                        appRouterUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    me.router = new JSONObject(response);
                                } catch (Exception e) {router=null;}
                            }
                        },
                        errorListener
                )
        );
    }

    private boolean configured(){return router!=null;}


    /**
     * @param serviceID the service name on client side
     * @param urlParams */
    public void send(
            String serviceID,
            Response.Listener<String> responseListener,
            Response.ErrorListener errorListener,
            Object tag,
            Map<String,String> urlParams
    ) throws AppRouterNotLoadedException, JSONException, InvalidWebServiceDescriptionException {
        if(!configured())
            throw new AppRouterNotLoadedException("Unable to send any request while the webapp's router is not defined");

        JSONObject serviceInfos = router.getJSONObject(serviceID);

        validParams(serviceInfos,urlParams);

        String reqStr = compileRequestURL(getMainPath(serviceID,serviceInfos),urlParams);

        Log.i("ReQstr"," Sending this request:\n  -->" + reqStr+ " to '"+serverAdr+"' @("+serviceID+")");

        StringRequest strReq = new StringRequest(
                getHTTPM(serviceID,serviceInfos),
                reqStr ,
                responseListener,errorListener);
        if(tag!=null)
            strReq.setTag(tag);

        this.requestQueue.add(strReq);
    }


    /**
     * @param serviceID the service name on client side
     * @param urlParams */
    public void send(
            String serviceID,
            Response.Listener<String> responseListener,
            Response.ErrorListener errorListener,
            Object tag,
            String... urlParams
    ) throws AppRouterNotLoadedException, JSONException, InvalidWebServiceDescriptionException {
        send(serviceID,responseListener,errorListener,tag,strTabToMap(urlParams));
    }



    private int getHTTPM(
            String serviceID,
            JSONObject serviceInfos
    ) throws JSONException, InvalidWebServiceDescriptionException {
        if (serviceInfos.has("httpM"))
            throw new InvalidWebServiceDescriptionException("Undefined HTTP Method for the WebService '"+serviceID+"'");;

        int httpMethod=serviceInfos.getInt("httpM");
        switch(httpMethod) {
            case 0 : //GET
                httpMethod = Request.Method.GET;break;
            case 1 : //POST
                httpMethod = Request.Method.POST;break;
            default : throw new InvalidWebServiceDescriptionException("Unknown HTTP Method for the WebService '"+serviceID+"'");
        }
        return httpMethod;
    }


    private String getMainPath(
            String serviceID,
            JSONObject serviceInfos
    ) throws JSONException, InvalidWebServiceDescriptionException {
        String [] paths = (String[]) serviceInfos.get("paths");
        if(paths.length==0)
            throw new InvalidWebServiceDescriptionException("Undefined URL for the WebService '"+serviceID+"'");;
        return paths[0];
    }


    private Map<String,String> strTabToMap(
            String... params
    ){
        HashMap<String,String> paramsMap = new HashMap<>();
        for(String str : params) {
            if (!str.contains("->"))
                throw new RuntimeException("compileRequestURL : bad string param.. abort url compilation");
            String[]entry = str.split("->");
            paramsMap.put(entry[0],entry[1]); //no performance here
        }
        return paramsMap;
    }


    public String compileRequestURL(//todo private .m
                                    String url,
                                    Map<String,String> params
    ){
        String reqStr = url;
        if(params!=null && params.size()>0)
            reqStr+="?";
        int i=1;
        for(Map.Entry<String,String> entry : params.entrySet()) {
            reqStr += entry.getKey() + "=" + entry.getValue();
            if(i++ <params.size())
                reqStr+="&";
        }
        return reqStr;
    }



    private boolean validParams(
            JSONObject serviceInfos,
            Map<String,String> params

    ){
        //todo throws exc if not valid type + name
        return true;
    }

}
