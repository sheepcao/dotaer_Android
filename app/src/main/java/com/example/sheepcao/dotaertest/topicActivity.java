package com.example.sheepcao.dotaertest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.ContentHandler;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class topicActivity extends AppCompatActivity {


    RequestQueue mQueue = null;
    MyAdapter adapter = null;


    private ListView lv;
    private List<Map<String, Object>> data;

    List<String> usernameList;
    List<String> commentList;
    List<String> commentIDList;
    List<String> commentTime;

    List<String> upList;


    ImageLoader imageLoader;
    String name;
    String dayTopic = "";
    int commentNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);


        mQueue = Volley.newRequestQueue(this);
        imageLoader = VolleySingleton.getInstance().getImageLoader();

        usernameList = new ArrayList<>();
        commentList = new ArrayList<>();
        commentIDList = new ArrayList<>();
        commentTime = new ArrayList<>();

        upList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.comment_list);

        SharedPreferences mSharedPreferences = this.getSharedPreferences("dotaerSharedPreferences", 0);
        name = mSharedPreferences.getString("username", "游客");


        //获取将要绑定的数据设置到data中
        data = getData();
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int
                    position, long id) {

                String playerName = (String) data.get(position).get("username");
                String content = (String) data.get(position).get("comment");
                String time = (String) data.get(position).get("comment_time");


                Intent intent = new Intent(topicActivity.this, commentDetailActivity.class);
                Bundle mBundle = new Bundle();


                mBundle.putString("username", playerName);
                mBundle.putString("comment", content);
                mBundle.putString("time", time);



                intent.putExtras(mBundle);
                startActivity(intent);


            }
        });


        CustomProgressBar.showProgressBar(topicActivity.this, false, "读取中");


        requestTopic();
    }


    private void requestTopic() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/makeTopic.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("topic found", response);
                        JSONObject JsonDIc = new JSONObject(response);
                        String topicContent = JsonDIc.getString("topic_content");
                        String topicDay = JsonDIc.getString("topic_day");

                        TextView topicLabel = (TextView) findViewById(R.id.topic_label);
                        topicLabel.setText(topicContent);
                        dayTopic = topicDay;
                        requestComment(topicDay);

                        CustomProgressBar.hideProgressBar();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "todayTopic");


                return map;
            }
        };
        mQueue.add(stringRequest);

    }

    private void requestComment(final String topicDate) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/comments.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("comment found", response);
                        JSONObject JsonDic = new JSONObject(response);

                        JSONArray commentArray = JsonDic.getJSONArray("comments");

                        if (commentArray == null) {
                            CustomProgressBar.hideProgressBar();
                            return;
                        }

                        commentArray = sortJsonArrayByDate(commentArray, "upsCount");

                        TextView commentCount = (TextView) findViewById(R.id.comment_count);
                        commentCount.setText(commentArray.length() + "条评论");
                        commentNum = commentArray.length();

                        usernameList.clear();
                        commentList.clear();
                        commentIDList.clear();
                        commentTime.clear();
                        upList.clear();

                        for (int i = 0; i < commentArray.length(); i++) {

                            JSONObject oneItem = commentArray.getJSONObject(i);
                            usernameList.add(oneItem.getString("comment_user"));
                            commentIDList.add(oneItem.getString("comment_id"));
                            commentTime.add(oneItem.getString("comment_time"));
                            commentList.add(oneItem.getString("comment_content"));
                            upList.add(oneItem.getString("upsCount"));
                        }
                        Log.d("usernameList", usernameList.toString());
                        data = getData();
                        adapter.notifyDataSetChanged();

                        CustomProgressBar.hideProgressBar();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "fetchComments");
                map.put("topicDate", topicDate);


                return map;
            }
        };
        mQueue.add(stringRequest);
    }

    protected JSONArray sortJsonArrayByDate(JSONArray mJSONArray, String dateName) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject jsonObj = null;
        for (int i = 0; i < mJSONArray.length(); i++) {
            jsonObj = mJSONArray.optJSONObject(i);
            list.add(jsonObj);
        }
        //排序操作
        JsonComparator pComparator = new JsonComparator(dateName);
        Collections.sort(list, pComparator);

        //把数据放回去
        mJSONArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonObj = list.get(i);
            mJSONArray.put(jsonObj);
        }
        return mJSONArray;
    }

    public class JsonComparator implements Comparator<JSONObject> {

        String dateName = "";

        JsonComparator(String dateName) {
            this.dateName = dateName;
        }

        @Override
        public int compare(JSONObject json1, JSONObject json2) {
            String date1 = json1.optString(dateName);
            String date2 = json2.optString(dateName);
            if (date1.compareTo(date2) < 0) {
                return 1;
            } else if (date1.compareTo(date2) > 0) {
                return -1;
            }
            return 0;
        }
    }

    public void addComment(View v) {


        if (!name.equals("游客")) {
            final EditText commentText = new EditText(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("请发表您的见解").setView(commentText).setPositiveButton("发布", null).setNegativeButton("取消", null);
            builder.setPositiveButton("发布", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (commentText.getText() != null && !commentText.getText().toString().trim().equals("")) {


                        pushComment(commentText.getText().toString());
                        ((ViewGroup) commentText.getParent()).removeView(commentText);


                    } else {
                        Toast.makeText(topicActivity.this, "发布内容不能为空", Toast.LENGTH_SHORT).show();

                    }


                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub


                    ((ViewGroup) commentText.getParent()).removeView(commentText);

                }

            });
            builder.show();


        } else {
            Toast.makeText(this, "登陆后即可发表见解", Toast.LENGTH_SHORT).show();
        }

    }

    private void pushComment(final String commentContent) {

        if (name.equals("游客") || dayTopic.trim().equals("")) {
            return;
        }

        CustomProgressBar.showProgressBar(topicActivity.this, false, "发布中");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/comments.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("comment added", response);

                        usernameList.add(name);
                        commentList.add(commentContent);
                        upList.add("0");
                        JSONObject jsonOBJ = new JSONObject(response);
                        String commentID = jsonOBJ.getString("comment_id");
                        String comment_time = jsonOBJ.getString("comment_time");

                        commentIDList.add(commentID);
                        commentTime.add(comment_time);

                        data = getData();
                        adapter.notifyDataSetChanged();

                        TextView commentCount = (TextView) findViewById(R.id.comment_count);
                        commentCount.setText(commentNum + 1 + "条评论");

                        CustomProgressBar.hideProgressBar();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "addcomment");
                map.put("commentUser", name);
                map.put("commentContent", commentContent);
                map.put("commentDay", dayTopic);


                return map;
            }
        };
        mQueue.add(stringRequest);


    }


    private void requestUps(final String commentID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/comments.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("comment found", response);
                        JSONObject JsonDic = new JSONObject(response);

                        String ups = JsonDic.getString("ups_count");
                        if (ups.equals("-1"))
                        {
                            Toast.makeText(topicActivity.this, "请勿重复点赞", Toast.LENGTH_SHORT).show();

                        }else
                        {

                            requestComment(dayTopic);
                        }

                        CustomProgressBar.hideProgressBar();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "addUps");
                map.put("commentID", commentID);
                map.put("commentUser", name);


                return map;
            }
        };
        mQueue.add(stringRequest);
    }


    //ViewHolder静态类
    static class ViewHolder {
        public RoundedImageView headImg;

        public TextView username;
        public TextView comment;
        public TextView upCount;
        public Button upButton;


    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < usernameList.size(); i++) {
            map = new HashMap<String, Object>();

            map.put("username", usernameList.get(i));
            map.put("comment", commentList.get(i));
            map.put("upCounts", "赞:" + upList.get(i));
            map.put("comment_id",commentIDList.get(i));
            map.put("comment_time",commentTime.get(i));


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
            final int positionIndex = position;
            if (convertView == null) {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.comment_item, null);
                holder.headImg = (RoundedImageView) convertView.findViewById(R.id.head_comment);
                holder.username = (TextView) convertView.findViewById(R.id.username_comment);
                holder.comment = (TextView) convertView.findViewById(R.id.comment_contentLabel);
                holder.upCount = (TextView) convertView.findViewById(R.id.up_count);
                holder.upButton = (Button) convertView.findViewById(R.id.up_button);

                holder.headImg.setScaleType(ImageView.ScaleType.FIT_CENTER);


                holder.upButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomProgressBar.showProgressBar(topicActivity.this, false, "读取中");

                        String commentID = (String) data.get(positionIndex).get("comment_id");
                        requestUps(commentID);

                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.username.setText((String) data.get(position).get("username"));
            holder.comment.setText((String) data.get(position).get("comment"));
            holder.upCount.setText((String) data.get(position).get("upCounts"));


            if (name.equals("游客")) {
                holder.upButton.setVisibility(View.INVISIBLE);
            } else {
                holder.upButton.setVisibility(View.VISIBLE);


            }

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
                        headTemp.setImageBitmap(Bitmap.createScaledBitmap(resizedBitmap, 80, 80, false));

                    } else {

                        headTemp.setImageResource(R.drawable.boysmall);
                    }
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
        actionBar.setTitle("今日话题");
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
