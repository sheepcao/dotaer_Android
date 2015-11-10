package com.example.sheepcao.dotaertest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makeramen.roundedimageview.RoundedImageView;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class commentDetailActivity extends AppCompatActivity {
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

        imageLoader = VolleySingleton.getInstance().getImageLoader();


        RoundedImageView head = (RoundedImageView)findViewById(R.id.head_comment_detail);
        TextView name = (TextView)findViewById(R.id.username_comment_detail);
        TextView content = (TextView)findViewById(R.id.comment_content);
        TextView commentDate = (TextView)findViewById(R.id.comment_date);
        LinearLayout userField = (LinearLayout)findViewById(R.id.user_field);




        Bundle bundle = getIntent().getExtras();

        final String username = (String) bundle.get("username");
        String contentBody = (String) bundle.get("comment");
        String time = (String) bundle.get("time");

        name.setText(username);
        content.setText(contentBody);
        commentDate.setText(time);


        userField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(commentDetailActivity.this, myPage.class);

                Bundle mBundle = new Bundle();


                mBundle.putString("myName", username);

                intent.putExtras(mBundle);

                startActivity(intent);
            }
        });

        final RoundedImageView headTemp = head;

        String nameURLstring = "";
        try {
            String strUTF8 = URLEncoder.encode((username + ".png"), "UTF-8");
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
        actionBar.setTitle("玩家评论");
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
