package com.example.sheepcao.dotaertest;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class confirmActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */



    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    RequestQueue mQueue = null;


    String requestCookie = "";
    String searchCookie = "";
    String redirectCookie = "";
    String ratingCookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.account_confirm);

        mPasswordView = (EditText) findViewById(R.id.password_confirm);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button_confirm);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form_confirm);
        mProgressView = findViewById(R.id.login_progress_confirm);

        mQueue = Volley.newRequestQueue(this);

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String account = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();



        showProgress();


        requestExtroInfoWithUser(account,password);



    }

    public boolean isAccountValid(String account) {
        Pattern p = Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5]+$");
        Matcher m = p.matcher(account);
        return m.matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress() {
        CustomProgressBar.showProgressBar(this, false, "正在登录");

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                CustomProgressBar.hideProgressBar();
//            }
//        }, 3000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        restoreActionBar();

        return true;
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("战绩认证");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        Log.v("option", id + "----home id:" + android.R.id.home);
        //noinspection SimplifiableIfStatement
        if ( id == android.R.id.home) {

            Log.v("back", "menu back-----------");



            Intent intent = new Intent();
            //把返回数据存入Intent
//        intent.putExtra(BACK_CODE, BACK_CODE_NO);
            //设置返回数据

            this.setResult(RESULT_CANCELED, intent);

            this.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // write your code here

            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //数据是使用Intent返回
        super.onBackPressed();
        Log.v("back", "back!!!!!");


        //关闭Activity
        super.onBackPressed();


    }





    // request from 11....




    private void requestExtroInfoWithUser(final String username , final String password) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

//                        Log.d("TAG", response);

                        String VIEWSTATEGENERATOR = pickVIEWSTATEGENERATOR(response);
                        String VIEWSTATE = pickVIEWSTATE(response);
                        String EVENTVALIDATION = pickEVENTVALIDATION(response);

                        requestUserID(VIEWSTATE, VIEWSTATEGENERATOR, EVENTVALIDATION, username ,password);
//                        requestUserID_e(VIEWSTATE, VIEWSTATEGENERATOR, EVENTVALIDATION, "不是故意咯", "xuechan99", username);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, com.android.volley.toolbox.HttpHeaderParser.parseCharset(response.headers));
                    String cookie = response.headers.get("Set-Cookie");
                    String cookie_final = "";


                    Pattern p = Pattern.compile("ASP.NET_SessionId=(.+)");
                    Matcher m = p.matcher(cookie);

                    if (m.find()) { //注意这里，是while不是if
                        String xxx = m.group();
//                        Log.v("cookiexxx", xxx);
                        String[] aa = xxx.split(";");
                        cookie_final = aa[0];
//                        Log.v("cookiexxx1111", cookie);


                    }
                    requestCookie = cookie_final;


                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, com.android.volley.toolbox.HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        mQueue.add(stringRequest);
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

    private void requestUserID(final String VIEWSTATE, final String VIEWSTATEGENERATOR, final String EVENTVALIDATION, final String username, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG<<<<<<<<<<", response);
//                        requestSearchUserID(SearchName);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                CustomProgressBar.hideProgressBar();

//                Log.e("TAG=========123=", error.networkResponse.headers.get("Set-Cookie"), error);

                String location = "";
                String cookie_final = "";


                if (error.networkResponse.statusCode == 302 || error.networkResponse.statusCode == 301) {
                    for (int i = 0; i < error.networkResponse.apacheHeaders.length; i++) {
//                        Log.d("my header", error.networkResponse.apacheHeaders[i].getName() + " - " + error.networkResponse.apacheHeaders[i].getValue());

                        if (error.networkResponse.apacheHeaders[i].getName().equals("Set-Cookie")) {
                            String cookieTemp = error.networkResponse.apacheHeaders[i].getValue();

                            String[] cookieparts = cookieTemp.split(";");
                            for (int j = 0; j < cookieparts.length; j++) {
                                if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=")) {
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
                            if (!location.startsWith("http")) {
                                location = "http://passport.5211game.com" + location;
                            }
                        }

                    }


                }


                searchCookie = cookie_final;

                redirectWithCookie(cookie_final, location, username);


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                Log.v("getHeaders", "getHeaders");

                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                params.put("Referer", "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=");
                params.put("Accept-Encoding", "gzip, deflate");


// SharedPreferences mSharedPreferences = getApplication().getSharedPreferences("dotaerSharedPreferences", 0);
//                String cookie = mSharedPreferences.getString("requestCookie","");
                Log.v("cookie2", requestCookie);
                params.put("Cookie", requestCookie);


                return params;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("__VIEWSTATE", VIEWSTATE);
                map.put("__VIEWSTATEGENERATOR", VIEWSTATEGENERATOR);
                map.put("__EVENTVALIDATION", EVENTVALIDATION);

                map.put("txtUser", username);
                map.put("txtPassWord", password);
                map.put("butLogin", "登录");


                return map;
            }


            @Override


            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, com.android.volley.toolbox.HttpHeaderParser.parseCharset(response.headers));
                    Log.v("cookieqqq", "cookieqqq");

                    String cookie_final = "";

                    String cookie = response.headers.get("Set-Cookie");
                    String[] cookieparts = cookie.split(";");
                    for (int j = 0; j < cookieparts.length; j++) {
                        if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=") && !cookieparts[j].toLowerCase().contains("httponly")) {
                            if (cookie_final.equals("")) {
                                cookie_final = cookieparts[j];

                            } else {
                                cookie_final = cookie_final + ";" + cookieparts[j];
                            }
                        }
                    }
//
                    ratingCookie = cookie_final;
//

                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, com.android.volley.toolbox.HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        mQueue.add(stringRequest);
    }


    private void redirectWithCookie(final String cookie, final String loc, final String SearchName) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loc,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("redirect done", response);

                        for (int i = 0; i < response.length(); i += 1024) {
                            if (i + 1024 < response.length())
                                Log.d("redirect done", response.substring(i, i + 1024));
                            else
                                Log.d("redirect done", response.substring(i, response.length()));
                        }

//                        requestSearchUserID(SearchName);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG Redirect", error.getMessage(), error);
                String hostString = "";
                if (loc.startsWith("http://")) {
                    hostString = "http://" + loc.substring(7).split("/")[0];
                } else if (loc.startsWith("https://")) {
                    hostString = "https://" + loc.substring(8).split("/")[0];
                }


                String location = "";
                String cookie_final = "";
                if (error.networkResponse.statusCode == 302 || error.networkResponse.statusCode == 301) {
                    for (int i = 0; i < error.networkResponse.apacheHeaders.length; i++) {
//                        Log.d("my header", error.networkResponse.apacheHeaders[i].getName() + " - " + error.networkResponse.apacheHeaders[i].getValue());
                        if (error.networkResponse.apacheHeaders[i].getName().equals("Set-Cookie")) {
                            String cookieTemp = error.networkResponse.apacheHeaders[i].getValue();
                            String[] cookieparts = cookieTemp.split(";");
                            for (int j = 0; j < cookieparts.length; j++) {
                                if (!cookieparts[j].toLowerCase().contains("domain=") && !cookieparts[j].toLowerCase().contains("path=") && !cookieparts[j].toLowerCase().contains("expires=") && !cookieparts[j].toLowerCase().contains("httponly")) {
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
                            if (!location.startsWith("http")) {
                                location = hostString + location;
                            }
                        }
                    }
                }
                redirectCookie = cookie_final;
                cookie_final = searchCookie + ";" + redirectCookie;
                ratingCookie = cookie_final;
                redirectWithCookie(cookie_final, location, SearchName);

            }


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                params.put("Referer", "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=");


                Log.v("cookie redict", cookie);
                params.put("Cookie", cookie);

                return params;
            }


        };

        mQueue.add(stringRequest);
    }


//    private void requestSearchUserID(String searchName) {
//        String infoURLstring = "";
//        try {
//            String strUTF8 = URLEncoder.encode(searchName, "UTF-8");
//            infoURLstring = "http://i.5211game.com/Rating/Ladder?u=" + strUTF8;
//            Log.v("strUTF8", strUTF8);
//
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, infoURLstring,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) throws JSONException {
//
//
//                        String userID = "";
//
//
//                        Pattern p = Pattern.compile("YY.d.u = (.+)");
//                        Matcher m = p.matcher(response);
//
//                        if (m.find()) { //注意这里，是while不是if
//                            String xxx = m.group();
////                            Log.v("Before score", xxx);
//
//                            String[] resultArray = xxx.split("YY.d.j = ");
//                            if (resultArray.length > 1) {
//                                userID = resultArray[1].split(",YY.d.k")[0];
//                                if (userID.equals("YY.d.u")) {
//                                    userID = "443732422";
//                                }
//                            }
//
//                        }
//
//                        requestScore(userID);
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Log.e("TAG", error.getMessage(), error);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
//                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//                params.put("Referer", "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=");
//
//
//                Log.v("ratingCookie", ratingCookie);
//                params.put("Cookie", ratingCookie);
//
//                return params;
//            }
//
//
//        };
//
//        mQueue.add(stringRequest);
//    }
//
//    private void requestScore(final String userID) {
//        Long tsLong = System.currentTimeMillis();
//        String ts = tsLong.toString();
//
//        String urlString = "http://i.5211game.com/request/rating/?r=" + ts;
//
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) throws JSONException {
//
////                        Log.d("Json string>>>>>", response);
//
//
//                        JSONObject jObject = new JSONObject(response);
//
//
//                        mjInfos = jObject.getJSONObject("mjInfos");
//                        ttInfos = jObject.getJSONObject("ttInfos");
//                        jjcInfos = jObject.getJSONObject("jjcInfos");
//
//                        mjScore = mjInfos.getString("MingJiang");
//                        ttScore = jObject.getInt("rating") + "";
//                        jjcScore = jObject.getInt("jjcRating") + "";
//
//
//                        setUpScorePage(selectedButton);
//
//                        CustomProgressBar.hideProgressBar();
//
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Log.e("TAG", error.getMessage(), error);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//
//
//                params.put("Cookie", ratingCookie);
//
//                return params;
//            }
//
//
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("method", "getrating");
//                map.put("u", userID);
//                map.put("t", "10001");
//
//
//                return map;
//            }
//
//
//        };
//        mQueue.add(stringRequest);
//    }


}
