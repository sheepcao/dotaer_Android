package com.example.sheepcao.dotaertest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


public class splashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MILLIS = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goHome();
            }
        }, SPLASH_DELAY_MILLIS);




    }

    private void goHome() {
        Intent intent = new Intent(splashActivity.this, MainActivity.class);
        splashActivity.this.startActivity(intent);
        splashActivity.this.finish();
    }
}
