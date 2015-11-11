package com.example.sheepcao.dotaertest;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavorActivity extends AppCompatActivity {


    ListView lv;

    private List<Map<String, Object>> data_list;

    MyAdapter adapter = null;

    RequestQueue mQueue = null;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor);

        imageLoader = VolleySingleton.getInstance().getImageLoader();
        lv = (ListView) findViewById(R.id.favor_list);

        data_list = new ArrayList<Map<String, Object>>();

        data_list = getData();
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int
                    position, long id) {

                String playerName = (String) data_list.get(position).get("name");


                Intent intent = new Intent(FavorActivity.this, myPage.class);
                Bundle mBundle = new Bundle();


                mBundle.putString("myName", playerName);

                intent.putExtras(mBundle);
                startActivity(intent);


            }
        });
    }

    static class ViewHolder {
        public ImageView heroImg;
        public TextView favorName;


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
                convertView = mInflater.inflate(R.layout.favor_item, null);
                holder.heroImg = (ImageView) convertView.findViewById(R.id.head_favor);
                holder.favorName = (TextView) convertView.findViewById(R.id.username_favor);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final ImageView gamaImage = holder.heroImg;

            holder.favorName.setText((String) data_list.get(position).get("name"));


            String userName = (String) data_list.get(position).get("name");

            String nameURLstring = "";
            try {
                String strUTF8 = URLEncoder.encode((userName + ".png"), "UTF-8");
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


                        Bitmap bmp= response.getBitmap();
                        int smallOne = bmp.getWidth()>bmp.getHeight()?bmp.getHeight():bmp.getWidth();

                        Bitmap resizedBitmap=Bitmap.createBitmap(bmp,(bmp.getWidth()-smallOne)/2,(bmp.getHeight()-smallOne)/2, smallOne, smallOne);
                        gamaImage.setImageBitmap(Bitmap.createScaledBitmap(resizedBitmap, 50, 50, false));
//                        headTemp.setImageBitmap(resizedBitmap);

                    } else  {

                        gamaImage.setImageResource(R.drawable.boysmall);
                    }
                }
            });


            return convertView;
        }

    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        String[] favors = loadArray("favorArray", getApplicationContext());


        for (int i = 0; i < favors.length; i++) {
            map = new HashMap<String, Object>();


            map.put("name", favors[i]);


            list.add(map);
        }
        return list;
    }


    public String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("dotaerSharedPreferences", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("关注的人");
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
}
