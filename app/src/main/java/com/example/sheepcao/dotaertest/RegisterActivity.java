package com.example.sheepcao.dotaertest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerName;
    private EditText registerAge;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerConfirm;
    private Button headButton;
    private ImageView headImg;
    private TextView uploadTip;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;
    private RadioGroup genderGroup;
    private ScrollView registerScroll;
    private Button submitButton;


    private String gender = "no";

    RequestQueue mQueue = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerName = (EditText) findViewById(R.id.register_name);
        registerAge = (EditText) findViewById(R.id.register_age);
        registerEmail = (EditText) findViewById(R.id.register_email);
        registerPassword = (EditText) findViewById(R.id.register_password);
        registerConfirm = (EditText) findViewById(R.id.register_confirm_password);
        headButton = (Button) findViewById(R.id.headBtn_register);
        headImg = (ImageView) findViewById(R.id.headImg_register);
        uploadTip = (TextView) findViewById(R.id.upload_tip);
        maleRadio = (RadioButton) findViewById(R.id.register_male);
        femaleRadio = (RadioButton) findViewById(R.id.register_female);
        genderGroup = (RadioGroup) findViewById(R.id.register_sex);
        registerScroll = (ScrollView) findViewById(R.id.register_form);
        submitButton = (Button) findViewById(R.id.register_submit);


        //绑定一个匿名监听器
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) RegisterActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                if (rb.getText().toString().equals("男"))
                {
                    gender = "male";
                }else if (rb.getText().toString().equals("女"))
                {
                    gender = "female";

                }
            }
        });

        mQueue = Volley.newRequestQueue(this);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });


    }


    public void attemptRegister() {

        // Reset errors.
        registerName.setError(null);
        registerEmail.setError(null);
        registerPassword.setError(null);
        registerConfirm.setError(null);

        // Store values at the time of the login attempt.
        final String account = registerName.getText().toString();
        final String password = registerPassword.getText().toString();
        final String confirmPassword = registerConfirm.getText().toString();
        final String email = registerEmail.getText().toString();
        final String age = registerAge.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            registerPassword.setError(getString(R.string.error_invalid_password));
            focusView = registerPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            registerName.setError(getString(R.string.error_field_required));
            focusView = registerName;
            cancel = true;
        } else if (!isAccountValid(account)) {
            registerName.setError(getString(R.string.error_invalid_email));
            focusView = registerName;
            cancel = true;
        }

        if (!confirmPassword.equals(password)) {
            registerConfirm.setError(getString(R.string.error_not_match));
            focusView = registerConfirm;
            cancel = true;
        }

        if (TextUtils.isEmpty(age)) {
            registerAge.setError(getString(R.string.error_field_required));
            focusView = registerAge;
            cancel = true;
        }


        if (gender.equals("no"))
        {
            Toast.makeText(RegisterActivity.this, "请选择您的性别!", Toast.LENGTH_SHORT).show();
            return;


        }




    if(cancel)

    {
        // There was an error; don't attempt login and focus the first
        // form field with an error.
        focusView.requestFocus();
    }

    else

    {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/~ericcao/upload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG", response);

//                        JSONObject jObject = new JSONObject(response);
//
//                        String username = jObject.getString("username");
//                        String password = jObject.getString("password");
//                        String age = jObject.getString("age");
//                        String sex = jObject.getString("sex");
//                        String isReviewed = jObject.getString("isReviewed");
//
//
//                        SharedPreferences mSharedPreferences = getSharedPreferences("dotaerSharedPreferences", 0);
//
//
//                        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
//                        mEditor.putString("username", username);
//                        mEditor.putString("password", password);
//                        mEditor.putString("age", age);
//                        mEditor.putString("sex", sex);
//                        mEditor.putString("isReviewed", isReviewed);
//
//                        mEditor.commit();
//
//                        CustomProgressBar.hideProgressBar();
//
//                        Log.v("login", "login OK-----------");
//
//
//                        Intent intent = new Intent();
//
//                        myLogin.setResult(RESULT_OK, intent);
//                        myLogin.finish();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomProgressBar.hideProgressBar();

                if (error.networkResponse.statusCode == 417) {
                    Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(RegisterActivity.this, "登录请求失败，请稍后重试", Toast.LENGTH_SHORT).show();

                }
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "register");
                map.put("name", account);
                map.put("password", password);
                map.put("email", email);
                map.put("age", age);
                map.put("sex", gender);


                return map;
            }
        };

        mQueue.add(stringRequest);


    }

}

    public void showProgress() {
        CustomProgressBar.showProgressBar(this, false, "注册中");


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
        actionBar.setTitle("注 册");
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


        //关闭Activity
        super.onBackPressed();


    }


}