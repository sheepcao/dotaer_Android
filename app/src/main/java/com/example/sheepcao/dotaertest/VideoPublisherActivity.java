package com.example.sheepcao.dotaertest;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyToolboxExtension;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VideoPublisherActivity extends AppCompatActivity {

    RequestQueue mQueue = null;

    private GridView gview;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;


    int[] publiserViewIDs;
    int[] publiserIDs;
    String[] publiser_name;
    String[] publiser;

    List<Map<String, String>> update;
    Map<String, Object> videos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_publisher);
//        mQueue = VolleyToolboxExtension.newRequestQueue(getApplicationContext());
        mQueue = Volley.newRequestQueue(this);



        publiserIDs = new int[]{R.drawable.publisher1, R.drawable.publisher2, R.drawable.publisher3, R.drawable.publisher4, R.drawable.publisher5, R.drawable.publisher6, R.drawable.publisher7, R.drawable.publisher8, R.drawable.publisher9, R.drawable.publisher10, R.drawable.publisher11, R.drawable.publisher12, R.drawable.publisher13, R.drawable.publisher14, R.drawable.publisher15, R.drawable.publisher16, R.drawable.publisher17, R.drawable.publisher18, R.drawable.publisher19, R.drawable.publisher20};
        Resources res = getResources();
        publiser_name = res.getStringArray(R.array.publiser_name);
        publiser = res.getStringArray(R.array.publiser);
        update = new ArrayList<>();
        videos = new HashMap<String, Object>();


        CustomProgressBar.showProgressBar(VideoPublisherActivity.this, false, "读取中");

        for (int i = 0; i < publiser.length; i++) {
            requestPublisherVideos(publiser[i]);
        }

        gview = (GridView) findViewById(R.id.gridview);
        data_list = new ArrayList<Map<String, Object>>();
        data_list = getData();
        String[] from = {"head", "name", "time"};
        int[] to = {R.id.headImg_publisher, R.id.name, R.id.update_time};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.publisher_layout, from, to);
        gview.setAdapter(sim_adapter);



        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    public void clickPublisher(View v) {


        TextView name = (TextView) v.findViewById(R.id.name);
        Log.v("click", name.getText().toString());
        String publisherName = name.getText().toString();

        Intent intent=new Intent();
        intent.setClass(VideoPublisherActivity.this, videoListActivity.class);
//

        Bundle mBundle = new Bundle();

        String onePublisher = "";
        for (int i = 0;i<publiser_name.length;i++)
        {
            if (publisherName.equals(publiser_name[i]))
            {
                onePublisher = publiser[i];
            }
        }
        mBundle.putString("videos", videos.get(onePublisher).toString());
        mBundle.putString("publisherName", publisherName);

        intent.putExtras(mBundle);


        startActivity(intent);


    }


    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < publiserIDs.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("head", publiserIDs[i]);
            map.put("name", publiser_name[i]);
            for (int j = 0; j < update.size(); j++) {
                String time = update.get(j).get(publiser[i]);
                if (time != null) {
                    map.put("time", "更新于:" + time);
                    break;
                }

            }
            list.add(map);
        }

        return list;
    }


    private void requestPublisherVideos(String publisherName) {
        final String nameKey = publisherName;

        try {
            publisherName = URLEncoder.encode(publisherName, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String host = "https://openapi.youku.com/v2/videos/by_user.json?client_id=cb52b838b5477abd&user_name=" + publisherName + "&count=30";
        Log.v("host", host);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, host,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

//                        Log.d("TAG", response);

                        JSONObject jObject = new JSONObject(response);
                        videos.put(nameKey,jObject);

                        JSONArray jArray = jObject.getJSONArray("videos");
                        if (jArray.length() > 0) {
                            JSONObject videoDIc = jArray.getJSONObject(0);
                            String updateTime = videoDIc.getString("published");
                            String updateDate = updateTime.split(" ")[0];
                            Map<String, String> map1 = new HashMap<String, String>();
                            map1.put(nameKey, updateDate);
                            update.add(map1);
                        }
                        if (update.size() % 5 == 0) {
                            data_list = getData();
                            String[] from = {"head", "name", "time"};
                            int[] to = {R.id.headImg_publisher, R.id.name, R.id.update_time};
                            sim_adapter = new SimpleAdapter(VideoPublisherActivity.this, data_list, R.layout.publisher_layout, from, to);
                            gview.setAdapter(sim_adapter);
                            sim_adapter.notifyDataSetChanged();
                        }

                        if (update.size() > 12) {
                            CustomProgressBar.hideProgressBar();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG", error.getMessage(), error);
                CustomProgressBar.hideProgressBar();

            }
        });

        mQueue.add(stringRequest);

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
        actionBar.setTitle("视频解说");
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


}
