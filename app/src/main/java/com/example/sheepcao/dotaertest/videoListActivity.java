package com.example.sheepcao.dotaertest;

import android.content.Context;
import android.content.Intent;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class videoListActivity extends AppCompatActivity {

    ListView videoList;
    private List<Map<String, Object>> data;
    String totalVideo;
    JSONArray videos;
    String PublisherName;

    MyAdapter adapter = null;

    ImageLoader imageLoader;
    RequestQueue mQueue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        mQueue = VolleySingleton.getInstance().getRequestQueue();

        imageLoader = VolleySingleton.getInstance().getImageLoader();


        Bundle bundle = getIntent().getExtras();
        String Videos = (String) bundle
                .get("videos");
        PublisherName = (String) bundle
                .get("publisherName");

        try {
            JSONObject jsonObj = new JSONObject(Videos);
            videos = jsonObj.getJSONArray("videos");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        videoList = (ListView) findViewById(R.id.video_list);
        data = new ArrayList<Map<String, Object>>();
        try {
            data = getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new MyAdapter(this);
        videoList.setAdapter(adapter);

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int
                    position, long id) {

                String link = "";

                try {
                    link = videos.getJSONObject(position).getString("link");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CustomProgressBar.showProgressBar(videoListActivity.this, false, "缓冲中");

                requestVideoInfo(link);

            }
        });


    }


    private void requestVideoInfo(final String link) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/videoURL.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);

                        JSONObject jObject = new JSONObject(response);

                        String m3u8 = jObject.getString("m3u8");
                        if (m3u8 != null || !m3u8.equals("null")) {
                            String vid = m3u8.split("vid=")[1].split("&")[0];
                            requestVideoURLForLink(vid);
                        }else
                        {
                            CustomProgressBar.hideProgressBar();
                        }
                        Log.v("m3u8", m3u8);


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
                map.put("tag", "getVid");
                map.put("VideoURL", link);

                return map;
            }
        };

        mQueue.add(stringRequest);

    }

    private void requestVideoURLForLink(String vid)
    {
        String videoURL = "http://lefun.net.cn:3301/?vid="+vid;

        Log.v("videoURL222",videoURL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, videoURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("videoURL", response);

                        JSONObject jObject = new JSONObject(response);
//
                        String playURL = jObject.getString("data");

                        if (playURL!=null &&!playURL.equals(""))
                        {
                            Intent intent=new Intent();
                            intent.setClass(videoListActivity.this, VideoActivity.class);
//

                            Bundle mBundle = new Bundle();


                            mBundle.putString("playURL", playURL);

                            intent.putExtras(mBundle);


                            startActivity(intent);
                            CustomProgressBar.hideProgressBar();


                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
                CustomProgressBar.hideProgressBar();

            }
        });

        mQueue.add(stringRequest);
    }


    public List<Map<String, Object>> getData() throws JSONException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < videos.length(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("gameImage", videos.getJSONObject(i).getString("thumbnail"));
            map.put("title", videos.getJSONObject(i).getString("title"));
            String time = videos.getJSONObject(i).getString("published");
            String date = time.split(" ")[0];
            date = "更新于\n"+date;

            map.put("time", date);
            String playCount = videos.getJSONObject(i).getString("view_count");
            int playCountData = Integer.parseInt(playCount);
            if (playCountData>10000)
            {
                playCountData = playCountData/10000;
                playCount = playCountData+"万";
            }
            playCount = "播放次数\n"+playCount;
            map.put("playCounts",playCount );

            list.add(map);
        }

        return list;
    }


    static class ViewHolder {
        public ImageView gameImage;


        public TextView titleName;
        public TextView updateTime;
        public TextView playCounts;


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
                convertView = mInflater.inflate(R.layout.videos_layout, null);
                holder.gameImage = (ImageView) convertView.findViewById(R.id.game_image);
                holder.titleName = (TextView) convertView.findViewById(R.id.title_video);
                holder.updateTime = (TextView) convertView.findViewById(R.id.updated_time);
                holder.playCounts = (TextView) convertView.findViewById(R.id.play_counts);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final ImageView gamaImage = holder.gameImage;

            holder.titleName.setText((String) data.get(position).get("title"));
            holder.updateTime.setText((String) data.get(position).get("time"));
            holder.playCounts.setText((String) data.get(position).get("playCounts"));

//            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.gameImage, R.drawable.nocolor, R.drawable.nocolor);

            String nameURLstring = (String) data.get(position).get("gameImage");

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
        actionBar.setTitle(PublisherName);
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


