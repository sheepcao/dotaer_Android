package com.example.sheepcao.dotaertest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    LoginActivity myLogin = this;


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    RequestQueue mQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.account);

        mPasswordView = (EditText) findViewById(R.id.password);





        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);





        mQueue = Volley.newRequestQueue(this);

        TextView registerButton = (TextView) findViewById(R.id.register_button);
        String udata = "还没圈子账号？快速注册->";
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        registerButton.setText(content);

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("register", "register enter");
                //添加注册界面 intent.
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String account = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isAccountValid(account)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress();


            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/~ericcao/upload.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) throws JSONException {

                            Log.d("TAG", response);

                            JSONObject jObject = new JSONObject(response);

                            String username = jObject.getString("username");
                            String password = jObject.getString("password");
                            String age = jObject.getString("age");
                            String sex = jObject.getString("sex");
                            String isReviewed = jObject.getString("isReviewed");


                            SharedPreferences mSharedPreferences = getSharedPreferences("dotaerSharedPreferences", 0);


                            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                            mEditor.putString("username", username);
                            mEditor.putString("password", password);
                            mEditor.putString("age", age);
                            mEditor.putString("sex", sex);
                            mEditor.putString("isReviewed", isReviewed);

                            mEditor.commit();

                            CustomProgressBar.hideProgressBar();

                            Log.v("login", "login OK-----------");

                            uploadPushId();


                            Intent intent = new Intent();

                            myLogin.setResult(RESULT_OK, intent);
                            myLogin.finish();


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                    if (error.networkResponse.statusCode == 417)
                    {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();

                    }else
                    {
                        Toast.makeText(LoginActivity.this, "登录请求失败，请稍后重试", Toast.LENGTH_SHORT).show();

                    }
                    Log.e("TAG", error.getMessage(), error);
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tag", "login");
                    map.put("name", account);
                    map.put("password", password);

                    return map;
                }
            };

            mQueue.add(stringRequest);


        }
    }

    public boolean isAccountValid(String account) {
        Pattern p = Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5]+$");
        Matcher m = p.matcher(account);
        return m.matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress() {
        CustomProgressBar.showProgressBar(this, false, "登录中");

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                CustomProgressBar.hideProgressBar();
//            }
//        }, 3000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        restoreActionBar();

        return true;
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("登 录");
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
        if (id == R.id.visitor || id == android.R.id.home) {

            Log.v("back", "menu back-----------");

            uploadPushId();


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

        uploadPushId();

        //关闭Activity
        super.onBackPressed();


    }


    private void uploadPushId() {
        SharedPreferences mSharedPreferences = getSharedPreferences("dotaerSharedPreferences", 0);
        String name = mSharedPreferences.getString("username", "游客");
        String channelId = mSharedPreferences.getString("channelId", "none");

        if (name.equals("游客")) {
            name = "SystemAnonymous";
        }
        if (!channelId.equals("none"))
        {
            channelId = "Android"+channelId;
            startUploading(name,channelId);

        }



    }

    private void startUploading(final String name , final String channelId)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/~ericcao/deviceURL.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("deivce added", response);


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
                map.put("tag", "addDevice");
                map.put("username", name);
                map.put("device", channelId);

                return map;
            }
        };
        mQueue.add(stringRequest);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("login requestCode", requestCode + "<<<<<<<<<");

        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.v("login resultCode", resultCode + "<<<<<<<<<1");

                    // TODO Switch tabs using the index.
                } else if (resultCode == Activity.RESULT_OK) {
                    Log.v("login resultCode", resultCode + "<<<<<<<<<1");

                    Intent intent = new Intent();

                    myLogin.setResult(RESULT_OK, intent);
                    myLogin.finish();
                }

                break;
            }

        }
    }


}

