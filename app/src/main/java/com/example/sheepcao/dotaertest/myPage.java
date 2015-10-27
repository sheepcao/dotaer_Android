package com.example.sheepcao.dotaertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.gms.plus.model.people.Person;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class myPage extends AppCompatActivity {

    Menu myMenu;
    String playerName = "";


    RequestQueue mQueue = null;
    ImageLoader imageLoader;

    String requestCookie = "";
    String searchCookie = "";
    String redirectCookie = "";
    String ratingCookie = "";

    TextView selectedButton = null;

    JSONObject mjInfos = null;
    JSONObject ttInfos = null;
    JSONObject jjcInfos = null;

    String jjcScore = "";
    String mjScore = "";
    String ttScore = "";

    TextView score_type = null;
    TextView score_label = null;
    TextView total_label = null;
    TextView win_rate = null;
    TextView mvp_label = null;
    TextView podi_label = null;
    TextView pojun_label = null;
    TextView fuhao_label = null;
    TextView buwang_label = null;
    TextView pianjiang_label = null;
    TextView yinghun_label = null;
    TextView double_kill = null;
    TextView triple_kill = null;

    ImageView hero_first = null;
    ImageView hero_second = null;
    ImageView hero_third = null;


    TextView ttMenu = null;
    TextView jjcMenu = null;
    TextView mjMenu = null;
    TextView noteMenu = null;


    private ListView lv;
    private List<Map<String, Object>> data;
    MyAdapter adapter = null;

    List<String> visitorList;
    List<String> contentList;
    List<String> timeList;


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
        imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });

        score_type = (TextView) findViewById(R.id.score_type);
        score_label = (TextView) findViewById(R.id.score_label);
        total_label = (TextView) findViewById(R.id.total_label);
        win_rate = (TextView) findViewById(R.id.win_rate);
        mvp_label = (TextView) findViewById(R.id.mvp_label);
        podi_label = (TextView) findViewById(R.id.podi_label);
        pojun_label = (TextView) findViewById(R.id.pojun_label);
        fuhao_label = (TextView) findViewById(R.id.fuhao_label);
        buwang_label = (TextView) findViewById(R.id.buwang_label);
        pianjiang_label = (TextView) findViewById(R.id.pianjiang_label);
        yinghun_label = (TextView) findViewById(R.id.yinghun_label);
        double_kill = (TextView) findViewById(R.id.double_kill);
        triple_kill = (TextView) findViewById(R.id.triple_kill);

        hero_first = (ImageView) findViewById(R.id.hero_first);
        hero_second = (ImageView) findViewById(R.id.hero_second);
        hero_third = (ImageView) findViewById(R.id.hero_third);

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
        mjMenu = (TextView) findViewById(R.id.mj_button);
        mjMenu.setOnClickListener(new View.OnClickListener() {
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
        noteMenu = (TextView) findViewById(R.id.note_button);
        noteMenu.setOnClickListener(new View.OnClickListener() {
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


        visitorList = new ArrayList<>();
        contentList = new ArrayList<>();
        timeList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.note_list);
        //获取将要绑定的数据设置到data中
        data = getData();
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);


//        CustomProgressBar.showProgressBar(this, false, "Loading");

        playerName = "lol";

        requestBasicInfo(playerName);
//        requestExtroInfoWithUser("宝贝拼吧");

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
        super.onCreateOptionsMenu(menu);
        myMenu = menu;

        getMenuInflater().inflate(R.menu.menu_my_page, menu);
        Button locButton = (Button) menu.findItem(R.id.action_binding).getActionView();
        locButton.setBackgroundResource(R.drawable.nocolor);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myPage.this, confirmActivity.class);
                startActivityForResult(intent, 1);
            }
        });


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
//
            Intent intent = new Intent(myPage.this, confirmActivity.class);
            startActivityForResult(intent, 1);

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


    private void setUpScorePage(TextView btn) throws JSONException {

        if (btn == noteMenu) {
            View notePad = (View) findViewById(R.id.note_pad);
            notePad.setVisibility(View.VISIBLE);

            final EditText et = new EditText(myPage.this);

            TextView addNote = (TextView) findViewById(R.id.add_note_button);
            addNote.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(myPage.this).setTitle("请输入您的留言").setView(et).setPositiveButton("确定", null).setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            String content = et.getText().toString();
                            SharedPreferences mSharedPreferences = getSharedPreferences("dotaerSharedPreferences", 0);
                            String name = mSharedPreferences.getString("username", "匿名游客");

                            String replyTo = "";
                            String[] replyArray = content.split("回复:");
                            if (replyArray.length > 1) {
                                replyTo = replyArray[1].split(",")[0];
                            }

                            addNoteRequest(playerName, content, name, replyTo);
                            et.setText("");
                            ((ViewGroup) et.getParent()).removeView(et);

                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub


                            ((ViewGroup) et.getParent()).removeView(et);

                        }

                    });
                    builder.show();

                }
            });
            requestNotes(playerName);
            return;
        } else {
            View notePad = (View) findViewById(R.id.note_pad);
            notePad.setVisibility(View.GONE);
        }

        JSONObject tempDic = ttInfos;
        if (btn == ttMenu) {
            tempDic = ttInfos;
            score_label.setText(ttScore);
            score_type.setText("天梯积分");
        } else if (btn == jjcMenu) {
            tempDic = jjcInfos;
            score_label.setText(jjcScore);
            score_type.setText("竞技场积分");

        } else if (btn == mjMenu) {
            tempDic = mjInfos;
            score_label.setText(mjScore);
            score_type.setText("名将等级");

        }

        if (tempDic != null) {

            total_label.setText(tempDic.getInt("Total") + "");
            win_rate.setText(tempDic.getString("R_Win"));

            mvp_label.setText(tempDic.getInt("MVP") + "");
            fuhao_label.setText(tempDic.getInt("FuHao") + "");
            podi_label.setText(tempDic.getInt("PoDi") + "");
            pojun_label.setText(tempDic.getInt("PoJun") + "");
            buwang_label.setText(tempDic.getInt("BuWang") + "");
            pianjiang_label.setText(tempDic.getInt("PianJiang") + "");
            yinghun_label.setText(tempDic.getInt("YingHun") + "");

            double_kill.setText(tempDic.getInt("DoubleKill") + "");
            triple_kill.setText(tempDic.getInt("TripleKill") + "");

            String hero1 = tempDic.getString("AdeptHero1");
            String hero2 = tempDic.getString("AdeptHero2");
            String hero3 = tempDic.getString("AdeptHero3");


            if (hero1 != null) {
                ImageLoader.ImageListener listener = ImageLoader.getImageListener(hero_first, R.drawable.nocolor, R.drawable.nocolor);
                String heroUrl = "http://i.5211game.com/img/dota/hero/" + hero1 + ".jpg";
                imageLoader.get(heroUrl, listener);
            }

            if (hero2 != null) {
                ImageLoader.ImageListener listener2 = ImageLoader.getImageListener(hero_second, R.drawable.nocolor, R.drawable.nocolor);
                String heroUrl2 = "http://i.5211game.com/img/dota/hero/" + hero2 + ".jpg";
                imageLoader.get(heroUrl2, listener2);
            }

            if (hero3 != null) {
                ImageLoader.ImageListener listener3 = ImageLoader.getImageListener(hero_third, R.drawable.nocolor, R.drawable.nocolor);
                String heroUrl3 = "http://i.5211game.com/img/dota/hero/" + hero3 + ".jpg";
                imageLoader.get(heroUrl3, listener3);
            }
        } else {

            score_label.setText("0");
            total_label.setText("0");
            win_rate.setText("0");

            mvp_label.setText("0");
            fuhao_label.setText("0");
            podi_label.setText("0");
            pojun_label.setText("0");
            buwang_label.setText("0");
            pianjiang_label.setText("0");
            yinghun_label.setText("0");

            double_kill.setText("0");
            triple_kill.setText("0");
            hero_first.setImageResource(R.drawable.nocolor);
            hero_second.setImageResource(R.drawable.nocolor);
            hero_third.setImageResource(R.drawable.nocolor);

        }


    }

    private void addNoteRequest(final String username, final String content, final String visitor, final String replyTo) {

        CustomProgressBar.showProgressBar(this, false, "uploading");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/~ericcao/note.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);

                        JSONObject jObject = new JSONObject(response);
                        JSONArray jArray = jObject.getJSONArray("visitor");
                        JSONArray jArray_content = jObject.getJSONArray("content");
                        JSONArray jArray_time = jObject.getJSONArray("createdAt");

                        visitorList.clear();
                        contentList.clear();
                        timeList.clear();

                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                String oneName = jArray.getString(i);
                                visitorList.add(oneName);

                                String oneContent = jArray_content.getString(i);
                                contentList.add(oneContent);

                                String oneTime = jArray_time.getString(i);
                                timeList.add(oneTime);

                            } catch (JSONException e) {
                                // Oops
                            }
                        }


                        data = getData();
                        adapter.notifyDataSetChanged();


                        CustomProgressBar.hideProgressBar();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG", error.getMessage(), error);
                CustomProgressBar.hideProgressBar();
                Toast.makeText(myPage.this, "网络请求失败", Toast.LENGTH_SHORT).show();


            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "addNote");
                map.put("username", username);
                map.put("content", content);
                map.put("visitor", visitor);
                map.put("replyTo", replyTo);


                return map;
            }


        };

        mQueue.add(stringRequest);
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


    private void requestNotes(final String name) {
        CustomProgressBar.showProgressBar(this, false, "Loading");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/note.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);
                        JSONObject jObject = new JSONObject(response);
                        JSONArray jArray = jObject.getJSONArray("visitor");
                        JSONArray jArray_content = jObject.getJSONArray("content");
                        JSONArray jArray_time = jObject.getJSONArray("createdAt");

                        visitorList.clear();
                        contentList.clear();
                        timeList.clear();

                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                String oneName = jArray.getString(i);
                                visitorList.add(oneName);

                                String oneContent = jArray_content.getString(i);
                                contentList.add(oneContent);

                                String oneTime = jArray_time.getString(i);
                                timeList.add(oneTime);

                            } catch (JSONException e) {
                                // Oops
                            }
                        }


                        data = getData();
                        adapter.notifyDataSetChanged();


                        CustomProgressBar.hideProgressBar();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG", error.getMessage(), error);
                CustomProgressBar.hideProgressBar();
                Toast.makeText(myPage.this, "网络请求失败", Toast.LENGTH_SHORT).show();


            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "getNote");
                map.put("username", name);

                return map;
            }


        };

        mQueue.add(stringRequest);

    }


    //ViewHolder静态类
    static class ViewHolder {
        public ImageView headImg;

        public TextView usernameLabel;
        public TextView timeLabel;
        public TextView noteNumber;
        public TextView noteText;


    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < visitorList.size(); i++) {
            map = new HashMap<String, Object>();


            map.put("visitor", visitorList.get(i));
            map.put("content", contentList.get(i));
            map.put("time", timeList.get(i));

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
                convertView = mInflater.inflate(R.layout.note_item, null);
                holder.headImg = (ImageView) convertView.findViewById(R.id.note_head);
                holder.usernameLabel = (TextView) convertView.findViewById(R.id.note_username);

                holder.timeLabel = (TextView) convertView.findViewById(R.id.time_label);
                holder.noteNumber = (TextView) convertView.findViewById(R.id.note_number);
                holder.noteText = (TextView) convertView.findViewById(R.id.note_text);


                holder.headImg.setScaleType(ImageView.ScaleType.FIT_CENTER);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String time = (String) data.get(position).get("time");
            time = time.substring(0, time.length() - 3);

            holder.noteNumber.setText((position + 1) + ".");

            holder.usernameLabel.setText((String) data.get(position).get("visitor"));
            holder.timeLabel.setText(time);
            holder.noteText.setText((String) data.get(position).get("content"));

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.headImg, R.drawable.nocolor, R.drawable.nocolor);

            String name = (String) data.get(position).get("visitor");
            String nameURLstring = "";
            try {
                String strUTF8 = URLEncoder.encode((name + ".png"), "UTF-8");
                nameURLstring = "http://cgx.nwpu.info/Sites/upload/" + strUTF8;
                Log.v("nameURLstring", nameURLstring);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            imageLoader.get(nameURLstring, listener);

            return convertView;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("activity requestCode", requestCode + "<<<<<<<<<");

        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.v("activity resultCode", resultCode + "<<<<<<<<<1");

//                    int tabIndex = data.getIntExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                    // TODO Switch tabs using the index.
                } else if (resultCode == Activity.RESULT_OK) {
                    Log.v("activity resultCode", resultCode + "<<<<<<<<<1");

//                    requestExtroInfoWithUser("不是故意咯");
                    requestBasicInfo(playerName);

                }
                break;
            }

        }
    }


    //request score procedure.........


    private void requestBasicInfo(final String username) {

        CustomProgressBar.showProgressBar(this, false, "Loading");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/~ericcao/playerInfo.php",
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
//                        TextView norecord_label = (TextView) findViewById(R.id.noRecord_label);


                        age_label.setText(age);
                        if (signature.equals("null")) {
                            signature_label.setText("签名的力气都拿来打dota了!");

                        } else {
                            signature_label.setText(signature);
                        }
                        game_name.setText(gameName);

                        if (sex.equals("male")) {
                            gender_img.setImageResource(R.drawable.male);

                        } else if (sex.equals("female")) {
                            gender_img.setImageResource(R.drawable.female);

                        }


                        if (isReviewed.equals("no")) {
//                            norecord_label.setVisibility(View.VISIBLE);
                            Button locButton = (Button) myMenu.findItem(R.id.action_binding).getActionView();
                            locButton.setText("战绩绑定");


                        } else {
//                            norecord_label.setVisibility(View.GONE);
                            Button locButton = (Button) myMenu.findItem(R.id.action_binding).getActionView();
                            locButton.setText("绑定变更");
                        }

                        Log.v("gamename", gameName);

                        if (gameName.equals("null"))
                        {
                            setUpScorePage(selectedButton);
                            game_name.setText("暂未认证");
                            CustomProgressBar.hideProgressBar();


                        }else
                        {
                            requestExtroInfoWithUser(gameName);
                        }


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

//                        Log.d("TAG", response);

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

    private void requestUserID(final String VIEWSTATE, final String VIEWSTATEGENERATOR, final String EVENTVALIDATION, final String username, final String password, final String SearchName) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2fi.5211game.com%2flogin.aspx%3freturnurl%3d%252frating&loginUserName=",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

//                        Log.d("TAG<<<<<<<<<<", response);
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

//                        for (int i = 0; i < response.length(); i += 1024) {
//                            if (i + 1024 < response.length())
//                                Log.d("redirect done", response.substring(i, i + 1024));
//                            else
//                                Log.d("redirect done", response.substring(i, response.length()));
//                        }

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
//                            Log.v("Before score", xxx);

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

//                        Log.d("Json string>>>>>", response);


                        JSONObject jObject = new JSONObject(response);

//
//                        mjInfos = jObject.getJSONObject("mjInfos");
//                        ttInfos = jObject.getJSONObject("ttInfos");
//                        jjcInfos = jObject.getJSONObject("jjcInfos");
//
//                        mjScore = mjInfos.getString("MingJiang");
//                        ttScore = jObject.getInt("rating") + "";
//                        jjcScore = jObject.getInt("jjcRating") + "";

//
                        if (!jObject.getString("mjInfos").equals("null")) {
                            mjInfos = jObject.getJSONObject("mjInfos");
                            mjScore = mjInfos.getString("MingJiang");

                        }
                        if (!jObject.getString("ttInfos").equals("null")) {
                            ttInfos = jObject.getJSONObject("ttInfos");

                        }
                        if (!jObject.getString("jjcInfos").equals("null")) {
                            jjcInfos = jObject.getJSONObject("jjcInfos");
                        }

                        ttScore = jObject.getInt("rating") + "";

                        jjcScore = jObject.getInt("jjcRating") + "";


                        setUpScorePage(selectedButton);

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

