package com.example.sheepcao.dotaertest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class myPage extends AppCompatActivity {

    RequestQueue mQueue = null;

    String requestCookie = "";
    String searchCookie = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);



        mQueue = Volley.newRequestQueue(this);


        requestBasicInfo("小奴");

    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("个人主页");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_page, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        Log.v("option", id + "----home id:" + android.R.id.home);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_binding) {
            return true;
        } else if (id == android.R.id.home) {
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
        Intent intent = new Intent();

        //关闭Activity
        super.onBackPressed();


    }


    //request score procedure.........


    private void requestBasicInfo(final String username) {

        CustomProgressBar.showProgressBar(this, false, "战绩读取...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/playerInfo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);
                        JSONObject jObject = new JSONObject(response);


                        String age = jObject.getString("age");
                        String sex = jObject.getString("sex");
                        String gameName = jObject.getString("gameName");
                        String signature = jObject.getString("content");
                        String isReviewed = jObject.getString("isReviewed");


                        TextView age_label = (TextView) findViewById(R.id.age_label);
                        TextView signature_label = (TextView) findViewById(R.id.signature_label);
                        TextView game_name = (TextView) findViewById(R.id.game_name);
                        ImageView gender_img = (ImageView) findViewById(R.id.gender_img);
                        TextView norecord_label = (TextView) findViewById(R.id.noRecord_label);


                        age_label.setText(age);
                        signature_label.setText(signature);
                        game_name.setText(gameName);

                        if (sex.equals("male")) {
                            gender_img.setImageResource(R.drawable.male);

                        } else if (sex.equals("female")) {
                            gender_img.setImageResource(R.drawable.female);

                        }


                        if (isReviewed.equals("no")) {
                            norecord_label.setVisibility(View.VISIBLE);
                        } else {
                            norecord_label.setVisibility(View.GONE);
                        }


                        requestExtroInfoWithUser(username);


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
                CustomProgressBar.hideProgressBar();

            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "playerInfo");
                map.put("name", username);

                return map;
            }


        };

        mQueue.add(stringRequest);
    }


    private void requestExtroInfoWithUser(final String username) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);

                        String VIEWSTATEGENERATOR = pickVIEWSTATEGENERATOR(response);
                        String VIEWSTATE = pickVIEWSTATE(response);
                        String EVENTVALIDATION = pickEVENTVALIDATION(response);

                        requestUserID(VIEWSTATE, VIEWSTATEGENERATOR, EVENTVALIDATION, "不是故意咯", "xuechan99", username);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                CustomProgressBar.hideProgressBar();

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
                        Log.v("cookiexxx", xxx);
                        String[] aa = xxx.split(";");
                        cookie_final = aa[0];
                        Log.v("cookiexxx1111", cookie);


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
            Log.v("VIEWSTATEGENERATOR", xxx);


            String[] aa = xxx.split("value=\"");
            Log.v("VIEWSTATEGENERATOR", aa[0]);

            if (aa.length > 1) {
                Log.v("VIEWSTATEGENERATOR", aa[1]);

                String[] bb = aa[1].split("\"");
                VIEWSTATEGENERATOR = bb[0];
                Log.v("VIEWSTATEGENERATOR---->", VIEWSTATEGENERATOR);

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
            Log.v("VIEWSTATE", xxx);


            String[] aa = xxx.split("value=\"");
            Log.v("VIEWSTATE", aa[0]);

            if (aa.length > 1) {
                Log.v("VIEWSTATE", aa[1]);

                String[] bb = aa[1].split("\"");
                VIEWSTATE = bb[0];
                Log.v("VIEWSTATE---->", VIEWSTATE);

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
            Log.v("EVENTVALIDATION", xxx);


            String[] aa = xxx.split("value=\"");
            Log.v("EVENTVALIDATION", aa[0]);

            if (aa.length > 1) {
                Log.v("EVENTVALIDATION", aa[1]);

                String[] bb = aa[1].split("\"");
                EVENTVALIDATION = bb[0];
                Log.v("EVENTVALIDATION---->", EVENTVALIDATION);

            }

        }
        return EVENTVALIDATION;
    }

    private void requestUserID(final String VIEWSTATE, final String VIEWSTATEGENERATOR, final String EVENTVALIDATION, final String username, final String password, final String SearchName) {
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

                Log.e("TAG=========================", error.getMessage(), error);
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
                    Log.v("cookieqqq","cookieqqq");

//                    String cookie = response.headers.get("Set-Cookie");
//
//                    Log.v("cookieqqq",cookie);
//                    String cookie_final = "";
//                    String cookie1;
//                    String cookie2;
//
//                    Pattern p = Pattern.compile("5211auth=(.+)");
//                    Matcher m = p.matcher(cookie);
//
//                    if (m.find()) { //注意这里，是while不是if
//                        String xxx = m.group();
//                        Log.v("5211authxxx", xxx);
//                        String[] aa = xxx.split(";");
//                        cookie1 = aa[0];
//                        Log.v("5211auth", cookie1);
//
//                        cookie_final = cookie1;
//
//
//                    }
//
//                    Pattern p2 = Pattern.compile("User=(.+)");
//                    Matcher m2 = p2.matcher(cookie);
//
//                    if (m2.find()) { //注意这里，是while不是if
//                        String xxx = m.group();
//                        Log.v("5211authxxx", xxx);
//                        String[] aa = xxx.split(";");
//                        cookie2 = aa[0];
//                        Log.v("5211auth", cookie2);
//
//                        if (cookie_final.equals("")) {
//                            cookie_final = cookie2;
//                        } else {
//                            cookie_final = cookie_final + ";" + cookie2;
//                        }
//
//
//                    }
//
//                    searchCookie = cookie_final;
//

                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, com.android.volley.toolbox.HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        mQueue.add(stringRequest);
    }

    private void requestSearchUserID(String searchName) {
        String infoURLstring = "";
        try {
            String strUTF8 = URLEncoder.encode(searchName, "UTF-8");
            infoURLstring = "http://i.5211game.com/Rating/Ladder?u=" + strUTF8;
            Log.v("strUTF8", strUTF8);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, infoURLstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("Before score", response);


                        CustomProgressBar.hideProgressBar();


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
                params.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:37.0) Gecko/20100101 Firefox/37.0");
                params.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
                params.put("Referer", "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=");


                Log.v("cookie3", searchCookie);
                params.put("Cookie", searchCookie);

                return params;
            }


        };

        mQueue.add(stringRequest);
    }

}
