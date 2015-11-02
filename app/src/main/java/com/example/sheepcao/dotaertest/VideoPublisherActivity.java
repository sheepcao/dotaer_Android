package com.example.sheepcao.dotaertest;

import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

public class VideoPublisherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_publisher);

        int[] publiserViewIDs = {R.id.publisher0, R.id.publisher1, R.id.publisher2 ,R.id.publisher3,R.id.publisher4,R.id.publisher5,R.id.publisher6,R.id.publisher7,R.id.publisher8,R.id.publisher9,R.id.publisher10,R.id.publisher11,R.id.publisher12,R.id.publisher13,R.id.publisher14,R.id.publisher15,R.id.publisher16,R.id.publisher17,R.id.publisher18,R.id.publisher19};
        int[] publiserIDs = {R.drawable.publisher1,R.drawable.publisher2,R.drawable.publisher3,R.drawable.publisher4,R.drawable.publisher5,R.drawable.publisher6,R.drawable.publisher7,R.drawable.publisher8,R.drawable.publisher9,R.drawable.publisher10,R.drawable.publisher11,R.drawable.publisher12,R.drawable.publisher13,R.drawable.publisher14,R.drawable.publisher15,R.drawable.publisher16,R.drawable.publisher17,R.drawable.publisher18,R.drawable.publisher19,R.drawable.publisher20};
        Resources res =getResources();
        String[] publiser_name=res.getStringArray(R.array.publiser_name);
        String[] publiser=res.getStringArray(R.array.publiser);


        for (int i = 0;i<publiserViewIDs.length;i++)
        {
            LayoutInflater mInflater;
            mInflater = LayoutInflater.from(this);
            FrameLayout viewFrame = (FrameLayout)findViewById(publiserViewIDs[i]);

            FrameLayout publisherButton;
            publisherButton = (FrameLayout)  mInflater.inflate(R.layout.publisher_layout, null);
            publisherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("lll", "create one");
                }
            });

            ViewHolder holder = new ViewHolder();
            holder.headImg = (RoundedImageView)findViewById(R.id.headImg_publisher);
            holder.name = (TextView)findViewById(R.id.name);
            holder.time = (TextView)findViewById(R.id.time);


            holder.name.setText(publiser_name[i]);
            holder.headImg.setImageResource(publiserIDs[i]);


            viewFrame.addView(publisherButton);

        }
    }


    //ViewHolder静态类
    static class ViewHolder {
        public RoundedImageView headImg;


        public TextView name;
        public TextView time;


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
