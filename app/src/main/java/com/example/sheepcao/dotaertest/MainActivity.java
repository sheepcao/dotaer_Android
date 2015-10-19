package com.example.sheepcao.dotaertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, BDLocationListener, OnSeekBarChangeListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    RequestQueue mQueue = null;
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

    ImageLoader imageLoader;


    private int pageIndex = 0;

    private double lati = 0.0;
    private double longi = 0.0;
//    private int ratio = 100000;
    private int searchRadius = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

//        mLocClient = new LocationClient(this);
//        mLocClient.registerLocationListener(this);
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(false);// 打开gps
//        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(1000);
//        mLocClient.setLocOption(option);
//        mLocClient.start();


        mQueue = Volley.newRequestQueue(this);
        usernameList = new ArrayList<>();
        ageList = new ArrayList<>();
        genderList = new ArrayList<>();
        distanceList = new ArrayList<>();

        imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });


        lv = (ListView) findViewById(R.id.dotaerList);
        //获取将要绑定的数据设置到data中
        data = getData();
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);


        SeekBar ratioBar = (SeekBar) findViewById(R.id.seekbar_ratio);
        ratioBar.setOnSeekBarChangeListener(this);
        ratioText = (TextView)findViewById(R.id.ratioText);

        populateRatio(9);

        pageUp = (Button)findViewById(R.id.pageUp);
        pageDown = (Button)findViewById(R.id.pageDown);
        pageNum = (TextView)findViewById(R.id.pageIndex);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                showLoginPage();
            }
        }, 1000);


    }

    public void showLoginPage()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1);

    }

    public int calculateFabonacci(int seq)
    {
        int i; // used in the "for" loop
        int fcounter = seq; // specifies the number of values to loop through
        int f1 = 1; // seed value 1
        int f2 = 0; // seed value 2
        int fn = 0; // used as a holder for each new value in the loop

        for (i=1; i<fcounter; i++){

            fn = f1 + f2;
            f1 = f2;
            f2 = fn;
        }
        return fn;
    }

    public void populateRatio(int progress)
    {
        int seed = progress + 13;

        if (seed<31) {
            searchRadius =  calculateFabonacci(seed);


            if (searchRadius<1000) {
                ratioText.setText((searchRadius/100)+"00米");

            }else if(searchRadius>1000000)
            {
                ratioText.setText("> 500KM");

            }else
            {
                ratioText.setText(((searchRadius/1000) +1)+"KM");

            }


        }else
        {
            searchRadius = 9999999;//无限远
            ratioText.setText("> 500KM");

        }

        Log.v("Radius------",searchRadius+"");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        populateRatio(progress);

        Log.v("ratio","Progress is " + progress
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
        public ImageView headImg;
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
            map.put("distance", distanceList.get(i) + "米");
            map.put("age", ageList.get(i));

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
                holder.headImg = (ImageView) convertView.findViewById(R.id.headImg);
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

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.headImg, R.drawable.male, R.drawable.male);


            imageLoader.get("http://cgx.nwpu.info/Sites/upload/" + data.get(position).get("username") + ".png", listener);

            return convertView;
        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        switch (position) {
            case 0:

                Intent intent = new Intent(MainActivity.this, myPage.class);
                startActivityForResult(intent, 2);

                Log.i("i", "=============================");

                break;
            case 1:

                break;
            default:

                break;
        }


    }

//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
//            case 3:
//                mTitle = getString(R.string.title_section3);
//                break;
//        }
//    }

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

            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("请输入11游戏ID进行查询").setView(new EditText(this)).setPositiveButton("搜索", null).setNegativeButton("取消", null);
            builder.setPositiveButton("搜索", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Toast.makeText(MainActivity.this, "我很喜欢海贼王", Toast.LENGTH_SHORT).show();

                }
            });
            builder.show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchPeople()
    {

        CustomProgressBar.showProgressBar(MainActivity.this, false,"正在登录");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                CustomProgressBar.hideProgressBar();
            }
        }, 8000);


        lati = 22.299439;
        longi = 114.173881;

        Log.i("TAG", "searchDotaer");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/~ericcao/position.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);

                        pageNum.setText(pageIndex+1+"");

                        JSONObject jObject = new JSONObject(response);

                        JSONArray jArray = jObject.getJSONArray("username");
                        JSONArray jArrayAge = jObject.getJSONArray("age");
                        JSONArray jArrayGender = jObject.getJSONArray("sex");
                        JSONArray jArrayDistance = jObject.getJSONArray("juli");

                        usernameList.clear();
                        ageList.clear();
                        genderList.clear();
                        distanceList.clear();

                        if (jArray.length()>=50)
                        {
                            pageDown.setEnabled(true);
                        }else
                        {
                            pageDown.setEnabled(false);
                        }


                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                String oneName = jArray.getString(i);
                                usernameList.add(oneName);

                                String oneAge = jArrayAge.getString(i);
                                ageList.add(oneAge);

                                String oneGender = jArrayGender.getString(i);
                                genderList.add(oneGender);

                                String oneDistance = jArrayDistance.getString(i);
                                distanceList.add(oneDistance);

                                Log.d("oneObject", oneName + "--" + oneAge + "--" + oneGender + "--" + oneDistance);
                            } catch (JSONException e) {
                                // Oops
                            }
                        }

                        Log.d("TAG", response);
                        Log.d("usernameList", usernameList.toString());
                        data = getData();
                        adapter.notifyDataSetChanged();

//                        CustomProgressBar.hideProgressBar();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                CustomProgressBar.hideProgressBar();

                Log.e("TAG", error.getMessage(), error);
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


    }

    public void searchDotaer(View v) {

        searchPeople();

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

        this.lati = location.getLatitude();
        this.longi = location.getLongitude();

        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        Log.i("BaiduLocationApiDem", sb.toString());


    }

    private void initLocation() {

        Log.i("BaiduLocation", "initLocation");

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocClient.setLocOption(option);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("activity requestCode", requestCode+"<<<<<<<<<");

        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.v("activity resultCode", resultCode+"<<<<<<<<<1");

//                    int tabIndex = data.getIntExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                    // TODO Switch tabs using the index.
                }else  if (resultCode == Activity.RESULT_OK)
                {
                    Log.v("activity resultCode", resultCode+"<<<<<<<<<1");
                    mNavigationDrawerFragment.findIdentity();

                }
                break;
            }
            case (2) : {
                Log.v("activity resultCode", resultCode+"<<<<<<<<<");

                if (resultCode == Activity.RESULT_OK) {
//                    int tabIndex = data.getIntExtra(PUBLIC_STATIC_STRING_IDENTIFIER);
                    // TODO Switch tabs using the index.
                    Log.v("activity return","2");
                }
                break;
            }
        }
    }

}
