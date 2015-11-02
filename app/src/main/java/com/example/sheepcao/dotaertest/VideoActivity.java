package com.example.sheepcao.dotaertest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);



        VideoView vid = (VideoView) findViewById(R.id.videoView1);
        Uri vidUri = Uri.parse("http://pl.youku.com/playlist/m3u8?vid=341407959&ts=1446110341&ctype=12&token=1382&keyframe=1&sid=844611034139212f11d1b&ev=1&type=mp4&ep=eiaQHEiMVs0E5CDWij8bMS3jIHZdXP0O9xuFhttnCdQlS%2BC9&oip=1931342760");
        vid.setVideoURI(vidUri);
        vid.setMediaController(new MediaController(this));
        vid.start();
    }
}
