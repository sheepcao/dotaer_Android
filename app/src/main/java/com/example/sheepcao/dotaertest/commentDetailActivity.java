package com.example.sheepcao.dotaertest;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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



        Bundle bundle = getIntent().getExtras();

        String username = (String) bundle.get("username");
        String contentBody = (String) bundle.get("comment");
        String time = (String) bundle.get("time");

        name.setText(username);
        content.setText(contentBody);
        commentDate.setText(time);

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
}
