package com.example.sheepcao.dotaertest;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ericcao on 11/27/15.
 */
public class RequestExtras {

    RequestQueue mQueue = null;
    ImageLoader imageLoader = null;

    String firstCookie = "";
    String secondCookie = "";


    public interface requestCallBack {
        public void onCompleteRequest(String theURL, String VIEWSTATEGENERATOR, String VIEWSTATE, String EVENTVALIDATION);

        public void onCompleteValidCode(Bitmap codeImage);

        public void onCompleteCookie(String cookies);


    }

    /**
     * 初始化接口变量
     */
    requestCallBack callBack = null;

    /**
     * 自定义控件的自定义事件
     *
     * @param iBack 接口类型
     */
    public void setRequestCompelted(requestCallBack iBack) {
        callBack = iBack;
    }


    public RequestExtras(RequestQueue queue, ImageLoader loader) {

        mQueue = queue;
        imageLoader = loader;


    }

    public void startRequest() {


        Log.d("startRequest", "startRequest");


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://score.5211game.com/Ranking/ranking.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG2212", response);

                        String VIEWSTATEGENERATOR = pickVIEWSTATEGENERATOR(response);
                        String VIEWSTATE = pickVIEWSTATE(response);
                        String EVENTVALIDATION = pickEVENTVALIDATION(response);
                        String theURL = pickURL(response);

                        callBack.onCompleteRequest(theURL, VIEWSTATEGENERATOR, VIEWSTATE, EVENTVALIDATION);

                        startRequestValidCode();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();
//                callBack.onCompleteRequest("0", "0", "0", "0");
                String location = "";
                String cookie_final = "";
                if (error.networkResponse == null) {
                    CustomProgressBar.hideProgressBar();

                    return;
                }

                if (error.networkResponse.statusCode == 302 || error.networkResponse.statusCode == 301) {
                    for (int i = 0; i < error.networkResponse.apacheHeaders.length; i++) {

                        if (error.networkResponse.apacheHeaders[i].getName().equals("Set-Cookie")) {
                            String cookieTemp = error.networkResponse.apacheHeaders[i].getValue();

                            String[] cookieparts = cookieTemp.split(";");
                            for (int j = 0; j < cookieparts.length; j++) {
                                if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=")  &&!cookieparts[j].toLowerCase().contains("httponly")) {
                                    if (cookie_final.equals("")) {
                                        cookie_final = cookieparts[j];

                                    } else {
                                        cookie_final = cookie_final + ";" + cookieparts[j];
                                    }
                                }
                            }


                        }

                        if (error.networkResponse.apacheHeaders[i].getName().equals("Location")) {
                            location = error.networkResponse.apacheHeaders[i].getValue();
                            String[] subUrl = location.split("st=");

//                            if (subUrl.length>0)
//                            {
//                                location = "http://app.5211game.com/sso/login/?returnurl=http%3a%2f%2fscore.5211game.com%2fRecordCenter%2f&st="+subUrl[1];
//                            }

                        }

                    }


                }

//
                firstCookie = cookie_final;
                Log.v("firstCookie111",firstCookie);

//                SharedPreferences mSharedPreferences = MyApplication.getAppContext().getSharedPreferences("dotaerSharedPreferences", 0);
//                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
//                mEditor.putString("firstCookie", firstCookie);
//                mEditor.commit();

                redirectFirst(location);




            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

                return params;
            }

            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                String cookie = "";

                String cookieTemp = response.headers.get("Set-Cookie");
                String[] cookieparts = cookieTemp.split(";");
                for (int j = 0; j < cookieparts.length; j++) {
                    if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=") && !cookieparts[j].toLowerCase().contains("httponly")) {
                        if (cookie.equals("")) {
                            cookie = cookieparts[j];

                        } else {
                            cookie = cookie + ";" + cookieparts[j];
                        }
                    }
                }

                callBack.onCompleteCookie(cookie);


                return super.parseNetworkResponse(response);
            }


        };


        mQueue.add(stringRequest);
    }



    private void redirectFirst(String location)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, location,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG2212", response);

                        String VIEWSTATEGENERATOR = pickVIEWSTATEGENERATOR(response);
                        String VIEWSTATE = pickVIEWSTATE(response);
                        String EVENTVALIDATION = pickEVENTVALIDATION(response);
                        String theURL = pickURL(response);

                        callBack.onCompleteRequest(theURL, VIEWSTATEGENERATOR, VIEWSTATE, EVENTVALIDATION);

                        startRequestValidCode();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();
                String location = "";
                String cookie_final = "";
                if (error.networkResponse == null) {
                    CustomProgressBar.hideProgressBar();

                    return;
                }

                if (error.networkResponse.statusCode == 302 || error.networkResponse.statusCode == 301) {
                    for (int i = 0; i < error.networkResponse.apacheHeaders.length; i++) {

                        if (error.networkResponse.apacheHeaders[i].getName().equals("Set-Cookie")) {
                            String cookieTemp = error.networkResponse.apacheHeaders[i].getValue();

                            String[] cookieparts = cookieTemp.split(";");
                            for (int j = 0; j < cookieparts.length; j++) {
                                if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=")  &&!cookieparts[j].toLowerCase().contains("httponly")) {
                                    if (cookie_final.equals("")) {
                                        cookie_final = cookieparts[j];

                                    } else {
                                        cookie_final = cookie_final + ";" + cookieparts[j];
                                    }
                                }
                            }


                        }

                        if (error.networkResponse.apacheHeaders[i].getName().equals("Location")) {
                            location = error.networkResponse.apacheHeaders[i].getValue();

                        }

                    }


                }

//
                secondCookie = cookie_final;
                Log.v("secondCookie",secondCookie);

//                SharedPreferences mSharedPreferences = MyApplication.getAppContext().getSharedPreferences("dotaerSharedPreferences", 0);
//                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
//                mEditor.putString("secondCookie", secondCookie);
//                mEditor.commit();


                redirectfinal(location);


            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

                return params;
            }

            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                String cookie = "";

                String cookieTemp = response.headers.get("Set-Cookie");
                String[] cookieparts = cookieTemp.split(";");
                for (int j = 0; j < cookieparts.length; j++) {
                    if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=") && !cookieparts[j].toLowerCase().contains("httponly")) {
                        if (cookie.equals("")) {
                            cookie = cookieparts[j];

                        } else {
                            cookie = cookie + ";" + cookieparts[j];
                        }
                    }
                }

                callBack.onCompleteCookie(cookie);


                return super.parseNetworkResponse(response);
            }


        };


        mQueue.add(stringRequest);
    }



    private void redirectfinal(String location)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, location,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG2212", response);

                        String VIEWSTATEGENERATOR = pickVIEWSTATEGENERATOR(response);
                        String VIEWSTATE = pickVIEWSTATE(response);
                        String EVENTVALIDATION = pickEVENTVALIDATION(response);
                        String theURL = pickURL(response);

                        callBack.onCompleteRequest(theURL, VIEWSTATEGENERATOR, VIEWSTATE, EVENTVALIDATION);

                        startRequestValidCode();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();
                callBack.onCompleteRequest("0", "0", "0", "0");



            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

                return params;
            }

            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                String cookie = "";

                String cookieTemp = response.headers.get("Set-Cookie");
                String[] cookieparts = cookieTemp.split(";");
                for (int j = 0; j < cookieparts.length; j++) {
                    if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=") && !cookieparts[j].toLowerCase().contains("httponly")) {
                        if (cookie.equals("")) {
                            cookie = cookieparts[j];

                        } else {
                            cookie = cookie + ";" + cookieparts[j];
                        }
                    }
                }

                cookie = cookie+";"+firstCookie+";"+secondCookie;
                Log.v("cookieFirst",cookie);

                SharedPreferences mSharedPreferences = MyApplication.getAppContext().getSharedPreferences("dotaerSharedPreferences", 0);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString("firstCookie", cookie);
                mEditor.commit();

                callBack.onCompleteCookie(cookie);


                return super.parseNetworkResponse(response);
            }


        };


        mQueue.add(stringRequest);
    }


    public void startRequestValidCode() {


        String nameURLstring = "http://passport.5211game.com/ValidateCode.aspx";

        imageLoader.get(nameURLstring, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Image Load", "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {


                if (response.getBitmap() != null) {


                    Bitmap bmp = response.getBitmap();
                    callBack.onCompleteValidCode(bmp);


                } else {

                    callBack.onCompleteValidCode(null);
                }
            }
        });

    }


    private String pickVIEWSTATEGENERATOR(String fullText) {
        String VIEWSTATEGENERATOR = "";
        Pattern p = Pattern.compile("id=\"__VIEWSTATEGENERATOR\" value=\"(.+)\"");
        Matcher m = p.matcher(fullText);

        if (m.find()) { //注意这里，是while不是if
            String xxx = m.group();
//            Log.v("VIEWSTATEGENERATOR", xxx);


            String[] aa = xxx.split("value=\"");
//            Log.v("VIEWSTATEGENERATOR", aa[0]);

            if (aa.length > 1) {
//                Log.v("VIEWSTATEGENERATOR", aa[1]);

                String[] bb = aa[1].split("\"");
                VIEWSTATEGENERATOR = bb[0];
//                Log.v("VIEWSTATEGENERATOR---->", VIEWSTATEGENERATOR);

            }

        }
        return VIEWSTATEGENERATOR;
    }


    private String pickVIEWSTATE(String fullText) {
        String VIEWSTATE = "";
        Pattern p = Pattern.compile("id=\"__VIEWSTATE\" value=\"(.+)\"");
        Matcher m = p.matcher(fullText);

        if (m.find()) { //注意这里，是while不是if
            String xxx = m.group();
//            Log.v("VIEWSTATE", xxx);


            String[] aa = xxx.split("value=\"");
//            Log.v("VIEWSTATE", aa[0]);

            if (aa.length > 1) {
//                Log.v("VIEWSTATE", aa[1]);

                String[] bb = aa[1].split("\"");
                VIEWSTATE = bb[0];
//                Log.v("VIEWSTATE---->", VIEWSTATE);

            }

        }
        return VIEWSTATE;
    }

    private String pickEVENTVALIDATION(String fullText) {
        String EVENTVALIDATION = "";
        Pattern p = Pattern.compile("id=\"__EVENTVALIDATION\" value=\"(.+)\"");
        Matcher m = p.matcher(fullText);

        if (m.find()) { //注意这里，是while不是if
            String xxx = m.group();
//            Log.v("EVENTVALIDATION", xxx);


            String[] aa = xxx.split("value=\"");
//            Log.v("EVENTVALIDATION", aa[0]);

            if (aa.length > 1) {
//                Log.v("EVENTVALIDATION", aa[1]);

                String[] bb = aa[1].split("\"");
                EVENTVALIDATION = bb[0];
//                Log.v("EVENTVALIDATION---->", EVENTVALIDATION);

            }

        }
        return EVENTVALIDATION;
    }


    private String pickURL(String fullText) {
        String theURL = "";
        Pattern p = Pattern.compile("form method=\"post\" action=\"(.+)\"");
        Matcher m = p.matcher(fullText);

        if (m.find()) { //注意这里，是while不是if
            String xxx = m.group();


            String[] aa = xxx.split("action=\".");

            if (aa.length > 1) {

                String[] bb = aa[1].split("\"");
                theURL = bb[0];
//                Log.v("EVENTVALIDATION---->", EVENTVALIDATION);

            }

        }
        return theURL;
    }

}
