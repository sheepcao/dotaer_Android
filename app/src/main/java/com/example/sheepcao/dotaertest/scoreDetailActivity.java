package com.example.sheepcao.dotaertest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class scoreDetailActivity extends AppCompatActivity {

    Boolean TTheroDone = false;
    Boolean JJCheroDone = false;
    Boolean scoreDone = false;

    String gameName="";

    ListView lv;
    private List<Map<String, Object>> tt_list;
    private List<Map<String, Object>> jjc_list;
    private List<Map<String, Object>> data_list;

    MyAdapter adapter = null;

    RequestQueue mQueue = null;
    ImageLoader imageLoader;



    String requestCookie = "";
    String searchCookie = "";
    String redirectCookie = "";
    String ratingCookie = "";

    TextView selectedButton = null;

    JSONObject ttInfos = null;
    JSONObject jjcInfos = null;


    JSONArray ttHero = null;
    JSONArray jjcHero = null;



    String jjcScore = "";
    String ttScore = "";

    TextView score_type = null;
    TextView score_label = null;
    TextView totalLabel = null;
    TextView win = null;
    TextView mvp = null;



    TextView ttMenu = null;
    TextView jjcMenu = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);

        mQueue = Volley.newRequestQueue(this, new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                connection.setInstanceFollowRedirects(false);

                return connection;
            }
        });
        imageLoader = VolleySingleton.getInstance().getImageLoader();


        Bundle bundle = getIntent().getExtras();


        gameName = (String) bundle
                .get("gameName");


        score_type = (TextView) findViewById(R.id.score_type);
        score_label = (TextView) findViewById(R.id.score_label);
        totalLabel = (TextView) findViewById(R.id.total_data);
        win = (TextView) findViewById(R.id.win_data);
        mvp = (TextView) findViewById(R.id.mvp_data);


        ttMenu = (TextView) findViewById(R.id.tt_button);
        ttMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                try {
                    selectedItem(tv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        jjcMenu = (TextView) findViewById(R.id.jjc_button);
        jjcMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                try {
                    selectedItem(tv);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        selectedButton = ttMenu;
        selectedButton.setBackgroundResource(R.drawable.list_button_press);
        selectedButton.setClickable(false);
        selectedButton.setTextColor(Color.parseColor("#C97311"));


        requestUserID(gameName);


        lv = (ListView) findViewById(R.id.hero_list);
        tt_list = new ArrayList<Map<String, Object>>();
        jjc_list = new ArrayList<Map<String, Object>>();
        data_list = new ArrayList<Map<String, Object>>();

//        data_list = getData();
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }



    static class ViewHolder {
        public ImageView heroImg;


        public TextView totalGame;
        public TextView mvpData;
        public TextView winData;
        public TextView heroKill;
        public TextView heroScore;



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
            return data_list.size();
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
                convertView = mInflater.inflate(R.layout.hero_item, null);
                holder.heroImg = (ImageView) convertView.findViewById(R.id.hero_img);
                holder.totalGame = (TextView) convertView.findViewById(R.id.total_game_data);
                holder.mvpData = (TextView) convertView.findViewById(R.id.mvp_hero_data);
                holder.winData = (TextView) convertView.findViewById(R.id.hero_win_data);
                holder.heroKill = (TextView) convertView.findViewById(R.id.hero_kill_data);
                holder.heroScore = (TextView) convertView.findViewById(R.id.hero_score_data);



                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final ImageView gamaImage = holder.heroImg;

            holder.totalGame.setText((String) data_list.get(position).get("total"));
            holder.mvpData.setText((String) data_list.get(position).get("mvp"));
            holder.winData.setText((String) data_list.get(position).get("r_win"));
            holder.heroKill.setText((String) data_list.get(position).get("herokill"));
            holder.heroScore.setText((String) data_list.get(position).get("score"));


            String heroId = (String) data_list.get(position).get("heroId");

            String nameURLstring = "http://score.5211game.com/RecordCenter/img/dota/hero/"+heroId+ ".jpg";
            imageLoader.get(nameURLstring, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Image Load", "Image Load Error: " + error.getMessage());
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    gamaImage.setImageBitmap(response.getBitmap());


                }
            });


            return convertView;
        }

    }



    private void setUpScorePage(TextView btn) throws JSONException {


        JSONObject tempDic = ttInfos;
        if (btn == ttMenu) {
            tempDic = ttInfos;
            score_label.setText(ttScore);
            score_type.setText("天梯积分");

            if (data_list == jjc_list) {
                data_list = tt_list;
                adapter.notifyDataSetChanged();
            }

        } else if (btn == jjcMenu) {
            tempDic = jjcInfos;
            score_label.setText(jjcScore);
            score_type.setText("竞技场积分");
            if (data_list == tt_list) {
                data_list = jjc_list;
                adapter.notifyDataSetChanged();
            }

        }

        if (tempDic != null) {

            totalLabel.setText(tempDic.getInt("Total") + "");
            win.setText(tempDic.getString("R_Win"));

            mvp.setText(tempDic.getInt("MVP") + "");


        } else {
            totalLabel.setText("0");
            score_label.setText("0");
            win.setText("0");
            mvp.setText("0");


        }


    }

    private void selectedItem(TextView btn) throws JSONException {

        selectedButton.setBackgroundResource(R.drawable.listbutton);
        selectedButton.setClickable(true);
        selectedButton.setTextColor(Color.parseColor("#dddddd"));

        btn.setBackgroundResource(R.drawable.list_button_press);
        btn.setClickable(false);
        btn.setTextColor(Color.parseColor("#C97311"));

        selectedButton = btn;

        try {
            setUpScorePage(btn);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    private void requestUserID(String username) {

        CustomProgressBar.showProgressBar(this, false, "Loading");
        try {
            username = URLEncoder.encode(username, "UTF-8");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = "http://score.5211game.com/RecordCenter/?u=" + username + "&t=10001";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.v("userID----", response);
                        String userID = pickUserID(response);
                        requestScore(userID);
                        requestTTheroScore(userID);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(scoreDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();

                CustomProgressBar.hideProgressBar();
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences mSharedPreferences = getApplication().getSharedPreferences("dotaerSharedPreferences", 0);
                String firstCookie = mSharedPreferences.getString("firstCookie", "");
                String secondCookie = mSharedPreferences.getString("secondCookie", "");

                String cookie = firstCookie + ";" + secondCookie;

                params.put("Cookie", cookie);

                return params;
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


    private void requestScore(final String userID) {


        String urlString = "http://score.5211game.com/RecordCenter/request/record";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

//                        Log.d("Json string>>>>>", response);


                        JSONObject jObject = new JSONObject(response);


                        if (!jObject.getString("ttInfos").equals("null")) {
                            ttInfos = jObject.getJSONObject("ttInfos");

                        }
                        if (!jObject.getString("jjcInfos").equals("null")) {
                            jjcInfos = jObject.getJSONObject("jjcInfos");
                        }

                        ttScore = jObject.getInt("rating") + "";

                        jjcScore = jObject.getInt("jjcRating") + "";


                        setUpScorePage(selectedButton);
                        scoreDone = true;

                        if(TTheroDone==true ) {
                            CustomProgressBar.hideProgressBar();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(scoreDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();

                CustomProgressBar.hideProgressBar();
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();



                SharedPreferences mSharedPreferences = getApplication().getSharedPreferences("dotaerSharedPreferences", 0);
                String firstCookie = mSharedPreferences.getString("firstCookie", "");
                String secondCookie = mSharedPreferences.getString("secondCookie", "");

                String cookie = firstCookie + ";" + secondCookie;

                params.put("Cookie", cookie);
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


    private void requestTTheroScore(final String userID)
    {

        String urlString = "http://score.5211game.com/RecordCenter/request/record";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TT score string>>>>>", response);

                        JSONObject jObject = new JSONObject(response);


                        ttHero = jObject.getJSONArray("ratingHeros");

                        tt_list = getTTData();

                        data_list = tt_list;
                        adapter.notifyDataSetChanged();


                        TTheroDone = true;

                        if(scoreDone==true ) {
                            CustomProgressBar.hideProgressBar();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(scoreDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();

                CustomProgressBar.hideProgressBar();
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();



                SharedPreferences mSharedPreferences = getApplication().getSharedPreferences("dotaerSharedPreferences", 0);
                String firstCookie = mSharedPreferences.getString("firstCookie", "");
                String secondCookie = mSharedPreferences.getString("secondCookie", "");

                String cookie = firstCookie + ";" + secondCookie;

                params.put("Cookie", cookie);
                return params;
            }


            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("method", "ladderheros");
                map.put("u", userID);
                map.put("t", "10001");


                return map;
            }


        };
        mQueue.add(stringRequest);
    }






    private List<Map<String, Object>> getJJCData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < jjcHero.length(); i++) {
            map = new HashMap<String, Object>();
            JSONObject oneHero = null;
            try {
                oneHero = (JSONObject)jjcHero.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                map.put("heroId", oneHero.getString("heroId"));
                map.put("score",  oneHero.getString("score"));
                map.put("total",  oneHero.getString("total"));
                map.put("mvp",  oneHero.getString("mvp"));
                map.put("herokill",  oneHero.getString("herokill"));
                String rate = oneHero.getString("r_win");
                String[] rateParts = rate.split(".");
                if (rateParts.length>0)
                {
                    rate = rateParts[0]+"%";
                }
                map.put("r_win",rate);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> getTTData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < ttHero.length(); i++) {
            map = new HashMap<String, Object>();
            JSONObject oneHero = null;
            try {
                oneHero = (JSONObject)ttHero.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                map.put("heroId", oneHero.getString("heroId"));
                map.put("score",  oneHero.getString("score"));
                map.put("total",  oneHero.getString("total"));
                map.put("mvp",  oneHero.getString("mvp"));
                map.put("herokill",  oneHero.getString("herokill"));
                String rate = oneHero.getString("r_win");
                String[] rateParts = rate.split(".");
                if (rateParts.length>0)
                {
                    rate = rateParts[0]+"%";
                }
                map.put("r_win",rate);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            list.add(map);
        }
        return list;
    }






    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(gameName);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.global, menu);



        restoreActionBar();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        Log.v("option", id + "----home id:" + android.R.id.home);
        if (id == android.R.id.home) {


            Log.v("back", "menu back-----------");
            Intent intent = new Intent();

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


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
