package com.example.sheepcao.dotaertest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;

import java.util.List;

/**
 * Created by ericcao on 10/22/15.
 */
public class MyPushMessageReceiver extends PushMessageReceiver {
    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d("Push Setting info:", responseString);

        if (errorCode == 0) {
            // 绑定成功
            SharedPreferences mSharedPreferences = context.getSharedPreferences("dotaerSharedPreferences", 0);


            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putString("channelId",channelId);

            mEditor.commit();
        }

    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {

    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}
