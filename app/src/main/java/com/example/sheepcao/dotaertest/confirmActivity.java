package com.example.sheepcao.dotaertest;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
    EditText validCodeField;
    Button codeImageButton;
    Button mEmailSignInButton;
    ImageView loadingImage;

    private View mProgressView;
    private View mLoginFormView;
    RequestQueue mQueue = null;
    ImageLoader imageLoaderOne;


    private String VIEWSTATEGENERATOR;
    private String VIEWSTATE;
    private String EVENTVALIDATION;
    private String yaoyaoURL;


    String requestCookie = "";
    String searchCookie = "";
    String redirectCookie = "";
    String ratingCookie = "";
    String finalCookie = "";





    String YYaccount = "";
    String YYpassword = "";
    String playerName = "";

    confirmActivity myConfirm = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.account_confirm);

        mPasswordView = (EditText) findViewById(R.id.password_confirm);

        validCodeField = (EditText) findViewById(R.id.code_field);
        codeImageButton = (Button) findViewById(R.id.code_image_btn);


        loadingImage = (ImageView)findViewById(R.id.loading_image);
//        loadingImage.setAnimation(rotateAnimation);
//        loadingImage.startAnimation(rotateAnimation);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button_confirm);
        codeImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                refreshYaoYaoCode(codeImageButton, mEmailSignInButton);

            }
        });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form_confirm);
        mProgressView = findViewById(R.id.login_progress_confirm);


        mQueue = Volley.newRequestQueue(this, new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                connection.setInstanceFollowRedirects(false);

                return connection;
            }
        });
        imageLoaderOne = VolleySingleton.getInstance().getImageLoaderOne();
        showValidCode();
        yaoyaoExtroInfo(codeImageButton,mEmailSignInButton);


        Bundle bundle = getIntent().getExtras();

        Log.v("playerName", (String) bundle
                .get("username"));

        playerName = (String) bundle
                .get("username");


        SharedPreferences mSharedPreferences = getSharedPreferences("dotaerSharedPreferences", 0);


        SharedPreferences.Editor mEditor = mSharedPreferences.edit();

        mEditor.putString("newRegister", "no");

        mEditor.commit();
    }


    public void showValidCode() {




        loadingImage.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation1 = new RotateAnimation(0f, 359f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation1.setInterpolator(new LinearInterpolator());
        rotateAnimation1.setDuration(1300);
        rotateAnimation1.setRepeatCount(Animation.INFINITE);
        rotateAnimation1.setFillAfter(true);

        loadingImage.startAnimation(rotateAnimation1);
//
//



    }



    public void yaoyaoExtroInfo(final Button codeButton, final Button submitButton) {

        CustomProgressBar.showProgressBar(confirmActivity.this, false, "获取中");


        RequestExtras extras = new RequestExtras(mQueue, imageLoaderOne);
        extras.setRequestCompelted(new RequestExtras.requestCallBack() {
            @Override
            public void onCompleteRequest(String theURL, String mVIEWSTATEGENERATOR, String mVIEWSTATE, String mEVENTVALIDATION) {

                Log.v("callBack!!", theURL + "----\n" + mVIEWSTATEGENERATOR + "----\n" + mVIEWSTATE + "----\n" + mEVENTVALIDATION + "----\n");
                VIEWSTATEGENERATOR = mVIEWSTATEGENERATOR;
                VIEWSTATE = mVIEWSTATE;
                EVENTVALIDATION = mEVENTVALIDATION;
                yaoyaoURL = theURL;

                if (theURL.equals("0")) {
                    CustomProgressBar.hideProgressBar();
                }
            }

            @Override
            public void onCompleteValidCode(Bitmap codeImage) {
                if (codeImage == null) {
                    codeButton.setText("刷新");
                } else {
                    submitButton.setEnabled(true);
                    codeButton.setBackgroundDrawable(new BitmapDrawable(codeImage));
                    codeButton.setText("");
                }
                CustomProgressBar.hideProgressBar();
                loadingImage.clearAnimation();
                loadingImage.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCompleteCookie(String cookies) {
                requestCookie = cookies;
            }
        });
        extras.startRequest();
    }


    public void refreshYaoYaoCode(final Button codeButton, final Button submitButton) {

        CustomProgressBar.showProgressBar(confirmActivity.this, false, "获取中");

        RequestExtras extras = new RequestExtras(mQueue, imageLoaderOne);
        extras.setRequestCompelted(new RequestExtras.requestCallBack() {
            @Override
            public void onCompleteRequest(String theURL, String mVIEWSTATEGENERATOR, String mVIEWSTATE, String mEVENTVALIDATION) {

            }

            @Override
            public void onCompleteValidCode(Bitmap codeImage) {
                if (codeImage == null) {
                    codeButton.setText("刷新");
                    codeButton.setBackgroundResource(R.drawable.nocolor);

                } else {
                    submitButton.setEnabled(true);
                    codeButton.setBackgroundDrawable(new BitmapDrawable(codeImage));
                    codeButton.setText("");
                }
                CustomProgressBar.hideProgressBar();
                loadingImage.clearAnimation();
                loadingImage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCompleteCookie(String cookies) {

            }
        });
        extras.startRequestValidCode();
    }



    public void submitYaoYaoLogin( String destURL, final String mVIEWSTATEGENERATOR, final String mVIEWSTATE, final String mEVENTVALIDATION, final String validCode, final String username, final String password) {



        final String infoURLstring = "http://passport.5211game.com" + destURL;

        if (destURL == null || destURL.equals("0") || mVIEWSTATEGENERATOR.equals("0") || mVIEWSTATE.equals("0") || mEVENTVALIDATION.equals("0")) {
            yaoyaoExtroInfo(codeImageButton, mEmailSignInButton);
            Toast.makeText(confirmActivity.this, "验证码错误，请重试", Toast.LENGTH_SHORT).show();

        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, infoURLstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("On11Login", response);
                        CustomProgressBar.hideProgressBar();

                        if (response.contains("密码错误"))
                        {
                            refreshYaoYaoCode(codeImageButton,mEmailSignInButton);
                            Toast.makeText(confirmActivity.this, "用户名或密码错误，请重试", Toast.LENGTH_SHORT).show();

                        }else
                        {


                            String gamerID = pickUserID(response);

                            Log.v("gamerID",gamerID);

                            if (gamerID.equals("123")) {

                                Toast.makeText(confirmActivity.this, "战绩获取失败，请重试", Toast.LENGTH_SHORT).show();
                                refreshYaoYaoCode(codeImageButton, mEmailSignInButton);

                            }
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                String location = "";
                String cookie_final = "";
                if (error.networkResponse == null) {
                    Toast.makeText(confirmActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    CustomProgressBar.hideProgressBar();

                    return;
                }

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
                            String[] subUrl = location.split("st=");

                            if (subUrl.length > 0) {
                                location = "http://app.5211game.com/sso/login/?returnurl=http%3a%2f%2fscore.5211game.com%2fRecordCenter%2f&st=" + subUrl[1];
                            }


                        }

                    }


                }

//
                cookie_final = searchCookie + ";" + cookie_final;
                searchCookie = cookie_final;

//
                redirectWithCookie(cookie_final, location, infoURLstring);


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                params.put("Referer", infoURLstring);
                params.put("Cache-Control", "max-age=0");
                params.put("Origin", "http://passport.5211game.com");
                params.put("Upgrade-Insecure-Requests", "1");
                params.put("Cookie", requestCookie);

                return params;
            }

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("__VIEWSTATE", mVIEWSTATE);
                map.put("__VIEWSTATEGENERATOR", mVIEWSTATEGENERATOR);
                map.put("__EVENTVALIDATION", mEVENTVALIDATION);
                map.put("txtAccountName", username);
                map.put("txtPassWord", password);
                map.put("txtValidateCode", validCode);
                map.put("ImgButtonLogin.x", "65");
                map.put("ImgButtonLogin.y", "13");


                return map;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };


        mQueue.add(stringRequest);
    }


    private void redirectWithCookie(final String cookie, final String loc, final String Referer) {
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


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG Redirect", error.getMessage(), error);
//                String hostString = "";
//                if (loc.startsWith("http://")) {
//                    hostString = "http://" + loc.substring(7).split("/")[0];
//                } else if (loc.startsWith("https://")) {
//                    hostString = "https://" + loc.substring(8).split("/")[0];
//                }


                String location = "";
                String cookie_final = "";

                if (error.networkResponse == null) {
                    Toast.makeText(confirmActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    CustomProgressBar.hideProgressBar();

                    return;
                }
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

                        }

                    }
                }
                redirectCookie = cookie_final;
                cookie_final = searchCookie + ";" + redirectCookie;
                finalCookie = cookie_final;
                Log.v("lkkkkk", finalCookie);




                requestUserID(finalCookie, location);



            }


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                params.put("Referer", Referer);


                Log.v("cookie redict", cookie);
                params.put("Cookie", cookie);

                return params;
            }

            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

        };

        mQueue.add(stringRequest);
    }


    private String pickUserID(String fullText) {


        String[] aa = fullText.split("YY.u='");

        if (aa.length > 1) {
            String[] bb = aa[1].split("'");
            return bb[0];
        }
        return "123";
    }

    private void requestUserID(final String cookie, String loc)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loc,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("On11Login222", response);

                        String gamerID = pickUserID(response);

                        if (gamerID.equals("")) {

                            Toast.makeText(confirmActivity.this, "战绩获取失败，请重试", Toast.LENGTH_SHORT).show();
                            refreshYaoYaoCode(codeImageButton, mEmailSignInButton);

                        } else {

                            requestScore(gamerID);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();
                Toast.makeText(confirmActivity.this, "战绩获取失败，请重试", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Cookie", cookie);

                return params;
            }


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };


        mQueue.add(stringRequest);
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
        String validCode = validCodeField.getText().toString();
//        final String account = "不是故意咯";
//        final String password = "xuechan99";


        YYaccount = account;
        YYpassword = password;
        showProgress();


        submitYaoYaoLogin(yaoyaoURL, VIEWSTATEGENERATOR, VIEWSTATE, EVENTVALIDATION,validCode,account, password);


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
        CustomProgressBar.showProgressBar(this, false, "认证中");


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
        if (id == android.R.id.home) {

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


    private void requestScore(final String userID) {


        String urlString = "http://score.5211game.com/RecordCenter/request/record";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        for (int i = 0; i < response.length(); i += 1024) {
                            if (i + 1024 < response.length())
                                Log.d("Json string>>>>>", response.substring(i, i + 1024));
                            else
                                Log.d("Json string>>>>>", response.substring(i, response.length()));
                        }

//
//                        JSONObject jObject = new JSONObject(response);
//
//                        if (!jObject.getString("mjInfos").equals("null")) {
//                            mjInfos = jObject.getJSONObject("mjInfos");
//                            mjScore = mjInfos.getString("MingJiang");
//
//                        }
//                        if (!jObject.getString("ttInfos").equals("null")) {
//                            ttInfos = jObject.getJSONObject("ttInfos");
//
//                        }
//                        if (!jObject.getString("jjcInfos").equals("null")) {
//                            jjcInfos = jObject.getJSONObject("jjcInfos");
//                        }
//
//                        ttScore = jObject.getInt("rating") + "";
//
//                        jjcScore = jObject.getInt("jjcRating") + "";

                        submitLevel(userID);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                params.put("Cookie", finalCookie);

                return params;
            }


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("method", "getrecord");
                map.put("u", userID);
                map.put("t", "10001");


                return map;
            }


        };
        mQueue.add(stringRequest);
    }


    private void submitLevel(final String userID) {

        SharedPreferences mSharedPreferences = getApplication().getSharedPreferences("dotaerSharedPreferences", 0);
        final String username = mSharedPreferences.getString("username", "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/confirmLevel.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        for (int i = 0; i < response.length(); i += 1024) {
                            if (i + 1024 < response.length())
                                Log.d("submitLevel string>>>>>", response.substring(i, i + 1024));
                            else
                                Log.d("submitLevel string>>>>>", response.substring(i, response.length()));
                        }


                        CustomProgressBar.hideProgressBar();
                        Toast.makeText(confirmActivity.this, "恭喜,战绩绑定成功!", Toast.LENGTH_SHORT).show();

                        MobclickAgent.onEvent(confirmActivity.this, "confirm_score");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent();

                                myConfirm.setResult(RESULT_OK, intent);
                                myConfirm.finish();
                            }
                        }, 1000);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG", error.getMessage(), error);
            }
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "confirmLevel");
                map.put("username", playerName);
                map.put("gameID", userID);
                map.put("gameName", YYaccount);
                map.put("password", YYpassword);


                return map;
            }
        };
        mQueue.add(stringRequest);
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}

