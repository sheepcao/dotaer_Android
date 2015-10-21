package com.example.sheepcao.dotaertest;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;


import org.json.JSONArray;
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


public class myPage extends AppCompatActivity {

    RequestQueue mQueue = null;

    String requestCookie = "";
    String searchCookie = "";
    String redirectCookie = "";
    String ratingCookie = "";

    JSONObject mjInfos = null;
    JSONObject ttInfos = null;
    JSONObject jjcInfos = null;

    String jjcScore = "";
    String mjScore = "";
    String ttScore = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        mQueue = Volley.newRequestQueue(this, new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                connection.setInstanceFollowRedirects(false);

                return connection;
            }
        });

//        mQueue = Volley.newRequestQueue(this);
//
//        Network network = new BasicNetwork(new OkHttpStack());
//        RequestQueue mQueue = new RequestQueue(new DiskBasedCache(new File(getCacheDir(), "volley")), network);
//        mQueue.start();

//        requestBasicInfo("小奴");
        requestExtroInfoWithUser("宝贝拼吧");

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


    private void setUpScorePage()
    {
        TextView score_label = (TextView) findViewById(R.id.score_label);
        TextView total_label = (TextView) findViewById(R.id.total_label);
        TextView win_rate = (TextView) findViewById(R.id.win_rate);
        TextView mvp_label = (TextView) findViewById(R.id.mvp_label);
        TextView podi_label = (TextView) findViewById(R.id.podi_label);
        TextView pojun_label = (TextView) findViewById(R.id.pojun_label);
        TextView fuhao_label = (TextView) findViewById(R.id.fuhao_label);
        TextView buwang_label = (TextView) findViewById(R.id.buwang_label);
        TextView pianjiang_label = (TextView) findViewById(R.id.pianjiang_label);
        TextView yinghun_label = (TextView) findViewById(R.id.yinghun_label);
        TextView double_kill = (TextView) findViewById(R.id.double_kill);
        TextView triple_kill = (TextView) findViewById(R.id.triple_kill);

        ImageView hero_first = (ImageView) findViewById(R.id.hero_first);
        ImageView hero_second = (ImageView) findViewById(R.id.hero_second);
        ImageView hero_third = (ImageView) findViewById(R.id.hero_third);


        score_label.setText(ttScore);



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
                        requestSearchUserID(SearchName);


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

                redirectWithCookie(cookie_final, location, SearchName);


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

//                        Log.d("redirect done", response);

                        for (int i = 0; i < response.length(); i += 1024) {
                            if (i + 1024 < response.length())
                                Log.d("redirect done", response.substring(i, i + 1024));
                            else
                                Log.d("redirect done", response.substring(i, response.length()));
                        }

                        requestSearchUserID(SearchName);


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
                        Log.d("my header", error.networkResponse.apacheHeaders[i].getName() + " - " + error.networkResponse.apacheHeaders[i].getValue());
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


                        String userID = "";


                        Pattern p = Pattern.compile("YY.d.u = (.+)");
                        Matcher m = p.matcher(response);

                        if (m.find()) { //注意这里，是while不是if
                            String xxx = m.group();
                            Log.v("Before score", xxx);

                            String[] resultArray = xxx.split("YY.d.j = ");
                            if (resultArray.length > 1) {
                                userID = resultArray[1].split(",YY.d.k")[0];
                                if (userID.equals("YY.d.u")) {
                                    userID = "443732422";
                                }
                            }

                        }

                        requestScore(userID);


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


                Log.v("ratingCookie", ratingCookie);
                params.put("Cookie", ratingCookie);

                return params;
            }


        };

        mQueue.add(stringRequest);
    }

    private void requestScore(final String userID) {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        String urlString = "http://i.5211game.com/request/rating/?r=" + ts;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("Json string>>>>>", response);


                        JSONObject jObject = new JSONObject(response);


                        mjInfos = jObject.getJSONObject("mjInfos");
                        ttInfos = jObject.getJSONObject("ttInfos");
                        jjcInfos = jObject.getJSONObject("jjcInfos");

                        mjScore = mjInfos.getString("MingJiang");
                        ttScore = jObject.getInt("rating")+"";
                        jjcScore = jObject.getInt("jjcRating")+"";




                        setUpScorePage();

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


                params.put("Cookie", ratingCookie);

                return params;
            }


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("method", "getrating");
                map.put("u", userID);
                map.put("t", "10001");


                return map;
            }


        };
        mQueue.add(stringRequest);
    }

}

