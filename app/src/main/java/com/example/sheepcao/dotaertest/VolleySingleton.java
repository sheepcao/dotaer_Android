package com.example.sheepcao.dotaertest;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private ImageLoader imageLoaderOne;


    private ImageLoader.ImageCache imageCache;
    private LruCache<String, Bitmap> mCache;
    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        mCache = new LruCache<String, Bitmap>(5*1024);

        imageCache = new ImageLoader.ImageCache() {
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        };
        mImageLoader = new ImageLoader(this.mRequestQueue,imageCache );

        imageLoaderOne = new ImageLoader(this.mRequestQueue,new ImageLoader.ImageCache() {
            public void putBitmap(String url, Bitmap bitmap) {
            }
            public Bitmap getBitmap(String url) {
                return null;
            }
        } );
    }

    public static VolleySingleton getInstance(){
        if(mInstance == null){
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }
    
    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }

    public ImageLoader getImageLoaderOne(){
        return this.imageLoaderOne;
    }

    public void clearCache(){
        mCache.evictAll();


    }

    public void instead(String url,Bitmap image){
        mCache.put(url, image);

    }
//
//    public void removeOne(String url){
//        mCache.put(url, null);
//
//    }

}
