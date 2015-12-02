package com.example.sheepcao.dotaertest;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.internal.widget.ActionBarContainer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
//import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
//import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, BDLocationListener, OnSeekBarChangeListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    Boolean loggedin = false;

    RequestQueue mQueue = null;
    RequestQueue customQueue = null;

    TextView ratioText;

    Button pageUp;
    Button pageDown;
    TextView pageNum;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    LocationClient mLocClient;

    MyAdapter adapter = null;


    private ListView lv;
    private List<Map<String, Object>> data;

    List<String> usernameList;
    List<String> ageList;
    List<String> genderList;
    List<String> distanceList;
    List<String> latiList;
    List<String> longiList;
    List<String> invisibleList;


    ImageLoader imageLoader;
    ImageLoader imageLoaderOne;

    private int pageIndex = 0;

    private double lati = 0.0;
    private double longi = 0.0;
    //    private int ratio = 100000;
    private int searchRadius = 0;


    private String VIEWSTATEGENERATOR;
    private String VIEWSTATE;
    private String EVENTVALIDATION;
    private String yaoyaoURL;

    String requestCookie = "";
    String searchCookie = "";
    String redirectCookie = "";
    String ratingCookie = "";

    ImageView loadingImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "Ts2amouZ7yRx5FEmslGc4xDm");

        SDKInitializer.initialize(getApplicationContext());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = "附近玩家";

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(this);

        Log.i("onCreate", "onCreate title" + mTitle);

//        initLocation();

        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false);// 打开gps
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();


        mQueue = VolleySingleton.getInstance().getRequestQueue();

        usernameList = new ArrayList<>();
        ageList = new ArrayList<>();
        genderList = new ArrayList<>();
        distanceList = new ArrayList<>();
        latiList = new ArrayList<>();
        longiList = new ArrayList<>();
        invisibleList = new ArrayList<>();


        imageLoader = VolleySingleton.getInstance().getImageLoader();
        imageLoaderOne = VolleySingleton.getInstance().getImageLoaderOne();


        customQueue = Volley.newRequestQueue(this, new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                connection.setInstanceFollowRedirects(false);

                return connection;
            }
        });


        lv = (ListView) findViewById(R.id.dotaerList);
        //获取将要绑定的数据设置到data中
        data = getData();
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int
                    position, long id) {

                String playerName = (String) data.get(position).get("username");


                Intent intent = new Intent(MainActivity.this, playerPageActivity.class);
                Bundle mBundle = new Bundle();


                mBundle.putString("playerName", playerName);
                mBundle.putString("lati", latiList.get(position));
                mBundle.putString("longi", longiList.get(position));
                mBundle.putString("distance", distanceList.get(position));


                intent.putExtras(mBundle);
                startActivity(intent);


            }
        });


        SeekBar ratioBar = (SeekBar) findViewById(R.id.seekbar_ratio);
        ratioBar.setOnSeekBarChangeListener(this);
        ratioText = (TextView) findViewById(R.id.ratioText);

        populateRatio(9);

        pageUp = (Button) findViewById(R.id.pageUp);
        pageDown = (Button) findViewById(R.id.pageDown);
        pageNum = (TextView) findViewById(R.id.pageIndex);
        pageNum.setText(pageIndex + 1 + "");

        pageDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex++;

                Log.v("page", pageIndex + "");
                searchPeople();

                pageUp.setEnabled(true);
            }
        });

        pageUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageIndex > 0) {
                    pageIndex--;
                    searchPeople();
                } else {
                    v.setEnabled(false);
                }

            }
        });


        SharedPreferences mSharedPreferences = this.getSharedPreferences("dotaerSharedPreferences", 0);
        String name = mSharedPreferences.getString("username", "游客");
        String password = mSharedPreferences.getString("password", "");


        if (name.equals("游客")) {
            showLoginPage();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    showValidCode();
                }
            }, 1200);

        } else {
            defaultLogin(name, password);
            showValidCode();

        }


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }


//    public void onWindowFocusChanged(boolean hasFocus) {
//        if (hasFocus) {
//            showValidCode();
//
//
//        }
//    }

    public void yaoyaoExtroInfo(final Button codeButton, final Button submitButton) {

        CustomProgressBar.showProgressBar(MainActivity.this, false, "获取中");


        RequestExtras extras = new RequestExtras(customQueue, imageLoaderOne);
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
                loadingImage.setVisibility(View.GONE);
            }

            @Override
            public void onCompleteCookie(String cookies) {
                requestCookie = cookies;
            }
        });
        extras.startRequest();
    }

    public void refreshYaoYaoCode(final Button codeButton, final Button submitButton) {

        CustomProgressBar.showProgressBar(MainActivity.this, false, "获取中");


        RotateAnimation rotateAnimation1 = new RotateAnimation(0f, 359f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation1.setInterpolator(new LinearInterpolator());
        rotateAnimation1.setDuration(1300);
        rotateAnimation1.setRepeatCount(Animation.INFINITE);
        rotateAnimation1.setFillAfter(true);

        loadingImage.startAnimation(rotateAnimation1);
        loadingImage.setVisibility(View.VISIBLE);

        RequestExtras extras = new RequestExtras(customQueue, imageLoaderOne);
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
                loadingImage.setVisibility(View.GONE);

            }

            @Override
            public void onCompleteCookie(String cookies) {

            }
        });
        extras.startRequestValidCode();
    }

    public void showValidCode() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View codeDialogView = factory.inflate(
                R.layout.valid_code_layout, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(codeDialogView);

        deleteDialog.setCanceledOnTouchOutside(false);

        if (loadingImage == null) {
            loadingImage = (ImageView) codeDialogView.findViewById(R.id.loading_image);
            RotateAnimation rotateAnimation1 = new RotateAnimation(0f, 359f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation1.setInterpolator(new LinearInterpolator());
            rotateAnimation1.setDuration(1300);
            rotateAnimation1.setRepeatCount(Animation.INFINITE);
            rotateAnimation1.setFillAfter(true);

            loadingImage.startAnimation(rotateAnimation1);
        }
        loadingImage.setVisibility(View.VISIBLE);



//        ObjectAnimator animator = ObjectAnimator.ofFloat(loadingImage, "rotationZ", 0, 90, 180, 270, 360);
//        animator.setDuration(300000);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.start();


//
//


        final EditText codeField = (EditText) codeDialogView.findViewById(R.id.code_field);

        codeDialogView.findViewById(R.id.submit_btn).setEnabled(false);
        codeDialogView.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v("code submit", "code submit");

                submitYaoYaoLogin(deleteDialog, (Button) codeDialogView.findViewById(R.id.submit_btn), (Button) codeDialogView.findViewById(R.id.code_image_btn),
                        yaoyaoURL, VIEWSTATEGENERATOR, VIEWSTATE, EVENTVALIDATION, codeField.getText().toString(), "不是故意咯", "xuechan99");


            }
        });

        codeDialogView.findViewById(R.id.code_image_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                refreshYaoYaoCode((Button) codeDialogView.findViewById(R.id.code_image_btn), (Button) codeDialogView.findViewById(R.id.submit_btn));

            }
        });


        deleteDialog.show();
        deleteDialog.getWindow().setLayout(460, 350);

        yaoyaoExtroInfo((Button) codeDialogView.findViewById(R.id.code_image_btn), (Button) codeDialogView.findViewById(R.id.submit_btn));


    }


    public void submitYaoYaoLogin(final AlertDialog deleteDialog, final Button submitBtn, final Button codeButton, String destURL, final String mVIEWSTATEGENERATOR, final String mVIEWSTATE, final String mEVENTVALIDATION, final String validCode, final String username, final String password) {

        CustomProgressBar.showProgressBar(MainActivity.this, false, "验证中");


        final String infoURLstring = "http://passport.5211game.com" + destURL;

        if (destURL == null || destURL.equals("0") || mVIEWSTATEGENERATOR.equals("0") || mVIEWSTATE.equals("0") || mEVENTVALIDATION.equals("0")) {
            yaoyaoExtroInfo(codeButton, submitBtn);
            Toast.makeText(MainActivity.this, "验证失败，请重试", Toast.LENGTH_SHORT).show();

        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, infoURLstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("On11Login", response);
                        CustomProgressBar.hideProgressBar();

                        String gamerID = pickUserID(response);

                        if (gamerID.equals("")) {

                            Toast.makeText(MainActivity.this, "验证失败，请重试", Toast.LENGTH_SHORT).show();
                            refreshYaoYaoCode(codeButton, submitBtn);


                        } else {
                            deleteDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();


                String location = "";
                String cookie_final = "";
                if (error.networkResponse == null) {
                    Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
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
                searchCookie = cookie_final;
                cookie_final = searchCookie + ";" + requestCookie;

//
                redirectWithCookie(cookie_final, location, infoURLstring, deleteDialog);


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


        customQueue.add(stringRequest);
    }


    private void redirectWithCookie(final String cookie, final String loc, final String Referer, final AlertDialog deleteDialog) {
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
                    Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
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

                    }
                }
                redirectCookie = cookie_final;
                cookie_final = searchCookie + ";" + redirectCookie;
                ratingCookie = cookie_final;
                Log.v("coooooo", ratingCookie);

                SharedPreferences mSharedPreferences = MyApplication.getAppContext().getSharedPreferences("dotaerSharedPreferences", 0);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString("secondCookie", ratingCookie);
                mEditor.commit();

                CustomProgressBar.hideProgressBar();

                deleteDialog.dismiss();


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

        customQueue.add(stringRequest);
    }


    public String pickUserID(String fullText) {
        String userID = "";
        Pattern p = Pattern.compile("UserId = \"(.+)\"");
        Matcher m = p.matcher(fullText);

        if (m.find()) { //注意这里，是while不是if
            String xxx = m.group();


            String[] aa = xxx.split("UserId = \"");

            if (aa.length > 1) {

                String[] bb = aa[1].split("\"");
                userID = bb[0];

            }

        }
        return userID;
    }

    public void showLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1);

    }

    private void defaultLogin(final String username, final String password) {
        CustomProgressBar.showProgressBar(MainActivity.this, false, "登录中");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/upload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);

                        loggedin = true;
                        uploadPosition();

                        mNavigationDrawerFragment.findIdentity();
                        CustomProgressBar.hideProgressBar();

                        searchPeople();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                if (error.networkResponse != null && error.networkResponse.statusCode == 417) {
                    Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "登录请求失败，请稍后重试", Toast.LENGTH_SHORT).show();

                }
                SharedPreferences mSharedPreferences = MainActivity.this.getSharedPreferences("dotaerSharedPreferences", 0);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString("username", "游客");
                mEditor.commit();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        showLoginPage();
                    }
                }, 1200);
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "login");
                map.put("name", username);
                map.put("password", password);

                return map;
            }
        };

        mQueue.add(stringRequest);

    }

    public int calculateFabonacci(int seq) {
        int i; // used in the "for" loop
        int fcounter = seq; // specifies the number of values to loop through
        int f1 = 1; // seed value 1
        int f2 = 0; // seed value 2
        int fn = 0; // used as a holder for each new value in the loop

        for (i = 1; i < fcounter; i++) {

            fn = f1 + f2;
            f1 = f2;
            f2 = fn;
        }
        return fn;
    }

    public void populateRatio(int progress) {
        int seed = progress + 13;

        if (seed < 31) {
            searchRadius = calculateFabonacci(seed);


            if (searchRadius < 1000) {
                ratioText.setText((searchRadius / 100) + "00米");

            } else if (searchRadius > 1000000) {
                ratioText.setText("> 500KM");

            } else {
                ratioText.setText(((searchRadius / 1000) + 1) + "KM");

            }


        } else {
            searchRadius = 9999999;//无限远
            ratioText.setText("> 500KM");

        }

        Log.v("Radius------", searchRadius + "");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        populateRatio(progress);

        Log.v("ratio", "Progress is " + progress
                + (fromUser ? " Trigger" : " Nontrigger") + " by user.");
    }

    @Override

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        System.out.println("onStart-->" + seekBar.getProgress());
    }

    @Override

    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

        System.out.println("onStop-->" + seekBar.getProgress());
    }


    //ViewHolder静态类
    static class ViewHolder {
        public RoundedImageView headImg;
        public ImageView gender;
        public ImageView mapImg;

        public TextView username;
        public TextView isReviewed;
        public TextView age;
        public TextView distance;


    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < usernameList.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("img", R.drawable.male);
            map.put("map", R.drawable.mid_fujin);

            map.put("gender", genderList.get(i));
            map.put("username", usernameList.get(i));
            int distance = Integer.parseInt(distanceList.get(i));
            if (distance > 1000) {
                map.put("distance", distance / 1000 + "KM");
            } else {
                map.put("distance", distanceList.get(i) + "米");
            }
            map.put("age", ageList.get(i));
            map.put("lati", latiList.get(i));
            map.put("longi", longiList.get(i));


            list.add(map);
        }
        return list;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;

        private MyAdapter(Context context) {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            //How many items are in the data set represented by this Adapter.
            //在此适配器中所代表的数据集中的条目数
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.
            //获取数据集中与指定索引对应的数据项
            return position;
        }

        @Override
        public long getItemId(int position) {
            //Get the row id associated with the specified position in the list.
            //获取在列表中与指定索引对应的行id
            return position;
        }

        //Get a View that displays the data at the specified position in the data set.
        //获取一个在数据集中指定索引的视图来显示数据
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //如果缓存convertView为空，则需要创建View
            if (convertView == null) {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.dotaer_item, null);
                holder.headImg = (RoundedImageView) convertView.findViewById(R.id.headImg);
                holder.gender = (ImageView) convertView.findViewById(R.id.genderImg);
                holder.username = (TextView) convertView.findViewById(R.id.usernameLabel);
                holder.age = (TextView) convertView.findViewById(R.id.ageLabel);
                holder.mapImg = (ImageView) convertView.findViewById(R.id.roundImg);
                holder.distance = (TextView) convertView.findViewById(R.id.distanceLabel);

                holder.headImg.setScaleType(ImageView.ScaleType.FIT_CENTER);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String gender = (String) data.get(position).get("gender");

//            holder.headImg.setImageResource((Integer) data.get(position).get("img"));
            if (gender.equals("male")) {
                holder.gender.setBackgroundResource(R.drawable.male);

            } else {
                holder.gender.setBackgroundResource(R.drawable.female);

            }
            holder.username.setText((String) data.get(position).get("username"));
            holder.age.setText((String) data.get(position).get("age"));
            holder.mapImg.setBackgroundResource((Integer) data.get(position).get("map"));
            holder.distance.setText((String) data.get(position).get("distance"));

            final RoundedImageView headTemp = holder.headImg;


            String nameURLstring = "";
            try {
                String strUTF8 = URLEncoder.encode((data.get(position).get("username") + ".png"), "UTF-8");
                nameURLstring = "http://cgx.nwpu.info/Sites/upload/" + strUTF8;
                Log.v("nameURLstring", nameURLstring);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            imageLoader.get(nameURLstring, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Image Load", "Image Load Error: " + error.getMessage());
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {


                    if (response.getBitmap() != null) {


                        Bitmap bmp = response.getBitmap();
                        int smallOne = bmp.getWidth() > bmp.getHeight() ? bmp.getHeight() : bmp.getWidth();

                        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, (bmp.getWidth() - smallOne) / 2, (bmp.getHeight() - smallOne) / 2, smallOne, smallOne);
                        headTemp.setImageBitmap(Bitmap.createScaledBitmap(resizedBitmap, 100, 100, false));

                    } else {

                        headTemp.setImageResource(R.drawable.boysmall);
                    }
                }
            });


            return convertView;
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Intent intent;
        SharedPreferences mSharedPreferences = this.getSharedPreferences("dotaerSharedPreferences", 0);
        String name = mSharedPreferences.getString("username", "游客");
        switch (position) {


            case 0:


                if (name.equals("游客")) {
                    return;
                } else {


                    intent = new Intent(MainActivity.this, myPage.class);

                    Bundle mBundle = new Bundle();


                    mBundle.putString("myName", name);


                    intent.putExtras(mBundle);

                    startActivityForResult(intent, 2);

                    Log.i("i", "=============================");
                }
                break;
            case 1:

                intent = new Intent(MainActivity.this, topicActivity.class);
                startActivity(intent);

                break;

            case 2:
                intent = new Intent(MainActivity.this, VideoPublisherActivity.class);
                startActivityForResult(intent, 4);

                Log.i("i", "=============================");
                break;
            case 3:
                intent = new Intent(MainActivity.this, FavorActivity.class);
                startActivity(intent);

                Log.i("i", "=============================");
                break;
            case 100:
                Log.i("i", "=login page=");


                if (name.equals("游客")) {
                    showLoginPage();

                } else {
                    SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.putString("username", "游客");


                    mEditor.commit();
                    Toast.makeText(MainActivity.this, "注销成功", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loggedin = false;

                            showLoginPage();
                        }
                    }, 1200);
                }

                break;
            default:

                break;
        }


    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setHomeAsUpIndicator(R.drawable.menu4);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Log.v("v", "main......................");

            final EditText gameNameText = new EditText(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("请输入11游戏ID进行查询").setView(gameNameText).setPositiveButton("搜索", null).setNegativeButton("取消", null);
            builder.setPositiveButton("搜索", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (gameNameText.getText() != null && !gameNameText.getText().toString().trim().equals("")) {
                        Intent intent = new Intent(MainActivity.this, scoreDetailActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("gameName", gameNameText.getText().toString());
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        MobclickAgent.onEvent(MainActivity.this, "11assist");


                        ((ViewGroup) gameNameText.getParent()).removeView(gameNameText);


                    }


                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub


                    ((ViewGroup) gameNameText.getParent()).removeView(gameNameText);

                }

            });
            builder.show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchPeople() {


        if ((lati > 0.001 || lati < -0.001) && (longi > 0.001 || longi < -0.001)) {

            CustomProgressBar.showProgressBar(MainActivity.this, false, "搜索中");


            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/position.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) throws JSONException {

                            Log.d("TAG", response);


                            pageNum.setText(pageIndex + 1 + "");

                            JSONObject jObject = new JSONObject(response);

                            if (jObject.getString("noRecord").equals("yes")) {
                                Toast.makeText(MainActivity.this, "没有搜到玩家，请加大搜索半径!", Toast.LENGTH_SHORT).show();

                            } else {
                                JSONArray jArray = jObject.getJSONArray("username");
                                JSONArray jArrayAge = jObject.getJSONArray("age");
                                JSONArray jArrayGender = jObject.getJSONArray("sex");
                                JSONArray jArrayDistance = jObject.getJSONArray("juli");
                                JSONArray jArraylati = jObject.getJSONArray("latitude");
                                JSONArray jArraylongi = jObject.getJSONArray("longitude");
                                JSONArray jArrayinvisible = jObject.getJSONArray("invisible");


                                usernameList.clear();
                                ageList.clear();
                                genderList.clear();
                                distanceList.clear();
                                latiList.clear();
                                longiList.clear();
                                invisibleList.clear();


                                if (jArray.length() >= 50) {
                                    pageDown.setEnabled(true);
                                } else {
                                    pageDown.setEnabled(false);
                                }


                                for (int i = 0; i < jArray.length(); i++) {
                                    try {

                                        String oneInvisible = jArrayinvisible.getString(i);
                                        if (oneInvisible.equals("yes")) {
                                            continue;
                                        }


                                        String oneName = jArray.getString(i);
                                        usernameList.add(oneName);

                                        String oneAge = jArrayAge.getString(i);
                                        ageList.add(oneAge);

                                        String oneGender = jArrayGender.getString(i);
                                        genderList.add(oneGender);

                                        String oneDistance = jArrayDistance.getString(i);
                                        distanceList.add(oneDistance);

                                        String oneLati = jArraylati.getString(i);
                                        latiList.add(oneLati);

                                        String oneLongi = jArraylongi.getString(i);
                                        longiList.add(oneLongi);


                                        Log.d("oneObject", oneName + "--" + oneAge + "--" + oneGender + "--" + oneDistance);
                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }

                                Log.d("TAG", response);
                                Log.d("usernameList", usernameList.toString());
                                data = getData();
                                adapter.notifyDataSetChanged();
                            }


                            CustomProgressBar.hideProgressBar();


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
                    map.put("tag", "getDistance");
                    map.put("lat", Double.toString(lati));
                    map.put("long", Double.toString(longi));
                    map.put("ratio", Double.toString(searchRadius));
                    map.put("page", Double.toString(pageIndex));
                    return map;
                }
            };

            mQueue.add(stringRequest);


        } else {
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            mLocClient.setLocOption(option);
            Toast.makeText(this, "获取位置失败，请重试", Toast.LENGTH_SHORT).show();
            return;


        }

        Log.i("TAG", "searchDotaer");


    }

    public void searchDotaer(View v) {

        PackageManager pm = getPackageManager();

        try {
            PackageInfo pack = pm.getPackageInfo("com.example.sheepcao.dotaertest", PackageManager.GET_PERMISSIONS);
            String[] permissionStrings = pack.requestedPermissions;
            String allPermissions = "";
            for (int i = 0; i < permissionStrings.length; i++) {
                Log.v("权限清单", "权限清单--->" + permissionStrings[i].toString());

                allPermissions = allPermissions + permissionStrings[i].toString() + ";";

            }
            if (allPermissions.contains("ACCESS_COARSE_LOCATION") && allPermissions.contains("ACCESS_FINE_LOCATION") && allPermissions.contains("READ_PHONE_STATE")) {
                searchPeople();

            } else {
                Toast.makeText(MainActivity.this, "授权错误:请从系统设置中打开捣塔圈所需权限。", Toast.LENGTH_SHORT).show();

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        MobclickAgent.onEvent(this, "search");


    }


    @Override
    public void onReceiveLocation(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());

        lati = location.getLatitude();
        longi = location.getLongitude();
//        BaiduAddress = location.getAddress();


        if ((lati > 0.001 || lati < -0.001) && (longi > 0.001 || longi < -0.001)) {

            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(false);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(50000);
            mLocClient.setLocOption(option);

            if (loggedin) {
                uploadPosition();
            }

        } else {
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(false);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            mLocClient.setLocOption(option);
        }


        Log.v("la,long", lati + "...." + longi);
//
//        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//            sb.append("\nspeed : ");
//            sb.append(location.getSpeed());// 单位：公里每小时
//            sb.append("\nsatellite : ");
//            sb.append(location.getSatelliteNumber());
//            sb.append("\nheight : ");
//            sb.append(location.getAltitude());// 单位：米
//            sb.append("\ndirection : ");
//            sb.append(location.getDirection());// 单位度
//            sb.append("\naddr : ");
//            sb.append(location.getAddrStr());
//            sb.append("\ndescribe : ");
//            sb.append("gps定位成功");
//
//        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//            sb.append("\naddr : ");
//            sb.append(location.getAddrStr());
//            //运营商信息
//            sb.append("\noperationers : ");
//            sb.append(location.getOperators());
//            sb.append("\ndescribe : ");
//            sb.append("网络定位成功");
//        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//            sb.append("\ndescribe : ");
//            sb.append("离线定位成功，离线定位结果也是有效的");
//        } else if (location.getLocType() == BDLocation.TypeServerError) {
//            sb.append("\ndescribe : ");
//            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//            sb.append("\ndescribe : ");
//            sb.append("网络不同导致定位失败，请检查网络是否通畅");
//        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//            sb.append("\ndescribe : ");
//            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//        }
//        sb.append("\nlocationdescribe : ");
//        sb.append(location.getLocationDescribe());// 位置语义化信息
//        List<Poi> list = location.getPoiList();// POI数据
//        if (list != null) {
//            sb.append("\npoilist size = : ");
//            sb.append(list.size());
//            for (Poi p : list) {
//                sb.append("\npoi= : ");
//                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//            }
//        }
//        Log.i("BaiduLocationApiDem", sb.toString());


    }

//    private void initLocation() {
//
//        Log.i("BaiduLocation", "initLocation");
//
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
//        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 0;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
//        mLocClient.setLocOption(option);
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("activity requestCode", requestCode + "<<<<<<<<<");

        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.v("activity resultCode", resultCode + "<<<<<<<<<1");
                    mNavigationDrawerFragment.makeGuest();
                    //eric:test video...

//                    searchPeople();

//                    int tabIndex = data.getIntExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                    // TODO Switch tabs using the index.
                } else if (resultCode == Activity.RESULT_OK) {
                    Log.v("activity resultCode", resultCode + "<<<<<<<<<1");
                    mNavigationDrawerFragment.findIdentity();
                    SharedPreferences mSharedPreferences = this.getSharedPreferences("dotaerSharedPreferences", 0);
                    String newRegister = mSharedPreferences.getString("newRegister", "no");


                    loggedin = true;
                    uploadPosition();

                    if (newRegister.equals("yes")) {

//                        Intent intent = new Intent(MainActivity.this, confirmActivity.class);
//                        startActivityForResult(intent, 3);

                        String name = mSharedPreferences.getString("username", "游客");

                        Intent intent = new Intent(MainActivity.this, confirmActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("username", name);
                        intent.putExtras(mBundle);
                        startActivityForResult(intent, 3);

                    } else {
                        searchPeople();

                    }


                }

                break;
            }
            case (2): {
                Log.v("activity resultCode", resultCode + "<<<<<<<<<");

                if (resultCode == Activity.RESULT_OK) {
//                    int tabIndex = data.getIntExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                    // TODO Switch tabs using the index.
                    Log.v("activity return", "2");
                }
                break;
            }
        }
    }


    private void uploadPosition() {

        SharedPreferences mSharedPreferences = this.getSharedPreferences("dotaerSharedPreferences", 0);
        final String username = mSharedPreferences.getString("username", "游客000");
        final String age = mSharedPreferences.getString("age", "0");
        final String sex = mSharedPreferences.getString("sex", "male");
        final String isReviewed = mSharedPreferences.getString("isReviewed", "no");
        final String invisible = "no";
        final String myLati = lati + "";
        final String myLongi = longi + "";


        if ((lati > 0.001 || lati < -0.001) && (longi > 0.001 || longi < -0.001)) {


            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/position.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) throws JSONException {

                            Log.d("TAG", response);

                            JSONObject jObject = new JSONObject(response);

                            String username = jObject.getString("username");
                            String age = jObject.getString("age");
                            String sex = jObject.getString("sex");
                            String isReviewed = jObject.getString("isReviewed");
                            String latitude = jObject.getString("latitude");
                            String longitude = jObject.getString("longitude");


                            Log.v("position uploaded:", username + "-" + "-" + age + "-" + sex + "-" + isReviewed + "-" + latitude + "-" + longitude);


                            Log.v("login", "login OK-----------");


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                    Log.v("position uploaded:", "位置上传失败！！");


                    Log.e("TAG", error.getMessage(), error);
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tag", "uploadPosition");
                    map.put("name", username);
                    map.put("lat", myLati);
                    map.put("long", myLongi);
                    map.put("invisible", invisible);
                    map.put("isReviewed", isReviewed);
                    map.put("age", age);
                    map.put("sex", sex);
                    map.put("TTscore", "999");


                    return map;
                }
            };


            mQueue.add(stringRequest);
        }

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
