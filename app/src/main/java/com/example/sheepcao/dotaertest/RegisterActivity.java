package com.example.sheepcao.dotaertest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.MultipartRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
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
    private String regName = "";
    Bitmap smallHead=null;

    RequestQueue mQueue = null;

    int serverResponseCode;


    String selectedPath1;
    String compressedPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


//        UploadFile();

//        uploadFiles();

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
                if (rb.getText().toString().equals("男")) {
                    gender = "male";
                } else if (rb.getText().toString().equals("女")) {
                    gender = "female";

                }
            }
        });

        mQueue = Volley.newRequestQueue(this);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    attemptRegister();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        headButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
            }
        });


    }


    public void attemptRegister() throws IOException {

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


        if (gender.equals("no")) {
            Toast.makeText(RegisterActivity.this, "请选择您的性别!", Toast.LENGTH_SHORT).show();
            return;


        }


        if (cancel)

        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else

        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress();

//            CustomProgressBar.showProgressBar(RegisterActivity.this, false, "上传中");

/*  */
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/upload.php",
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


                            regName = username;
//                            (new MyTask()).execute("");



                            SharedPreferences mSharedPreferences = getSharedPreferences("dotaerSharedPreferences", 0);


                            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                            mEditor.putString("username", username);
                            mEditor.putString("password", password);
                            mEditor.putString("age", age);
                            mEditor.putString("sex", sex);
                            mEditor.putString("isReviewed", isReviewed);
                            mEditor.putString("newRegister", "yes");


                            mEditor.commit();



                            File uploadFile = storeImage(smallHead);

                            Log.v("length", uploadFile.length() + "");



                            String boundary = "*****";

                            CustomProgressBar.showProgressBar(RegisterActivity.this, false, "上传中");

                            Map<String, String> bodyMap = new HashMap<String, String>();
                            bodyMap.put("Connection", "Keep-Alive");
                            bodyMap.put("Content-Type", "multipart/form-data;charset=utf-8;boundary=" + boundary);





                            MultipartRequest stringRequest = new MultipartRequest("http://cgx.nwpu.info/Sites/AndroidImage.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) throws JSONException {
                                            Log.v("response", response);


                                        }


                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.e("TAG", error.getMessage(), error);

                                }
                            }, uploadFile, bodyMap);

                            mQueue.add(stringRequest);

//
//
                            Log.v("register", "register OK-----------");
//
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CustomProgressBar.hideProgressBar();

                                    Intent intent = new Intent();

                                    RegisterActivity.this.setResult(RESULT_OK, intent);
                                    RegisterActivity.this.finish();

                                }
                            }, 1100);
//


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


    public void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, 1);

    }


    public String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }




    private class MyTask extends AsyncTask<String, Integer, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            Log.i("TAG", "onPreExecute() called");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            Log.i("TAG", "doInBackground(Params... params) called");
            uploadFiles();
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i("TAG", "onProgressUpdate(Progress... progresses) called");

        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            Log.i("TAG", "onPostExecute(Result result) called");
            if (serverResponseCode != 200) {
                Toast.makeText(RegisterActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(RegisterActivity.this, "头像上传成功!", Toast.LENGTH_SHORT).show();


                //test
                Intent intent = new Intent();

                RegisterActivity.this.setResult(RESULT_OK, intent);
                RegisterActivity.this.finish();


            }

        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i("TAG", "onCancelled() called");

        }
    }


    public void uploadFiles() {


        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String pathToOurFile = compressedPath;
//        String pathToOurFile = "file:///mnt/sdcard/.QQ/head/122559518.png";

        String urlServer = "http://cgx.nwpu.info/Sites/AndroidImage.php";
//        String urlServer = "http://www.baidu.com";

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        String serverResponseMessage;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 25 * 1024 * 1024;//25M

        try {

            Log.v("upload======", pathToOurFile);


            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));

            URL url = new URL(urlServer);

            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");
            Log.v("upload======", "11111");

            connection.setRequestProperty("Connection", "Keep-Alive");
            Log.v("upload======", "4454555");

            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            Log.v("upload======", "1212");

            outputStream = new DataOutputStream(connection.getOutputStream());
            Log.v("upload======", "333333");
//
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + regName +".png" + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            Log.v("upload======", "444444");


            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.v("upload======bytesRead", bytesRead + "");

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            Log.v("upload======", "22222");

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            Log.v("upload======", "rrrrrr");

            // Responses from the server (code and message)
            serverResponseCode = connection.getResponseCode();
            Log.v("upload....", serverResponseCode + "");

            serverResponseMessage = connection.getResponseMessage();


            CustomProgressBar.hideProgressBar();
            Log.v("upload....", serverResponseCode + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            //Exception handling
            Log.v("EXXXXXXX", ex.toString());
            CustomProgressBar.hideProgressBar();
            Toast.makeText(RegisterActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();


        }

    } // End else block



    private File storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TAG",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);


            int imageSize = image.getByteCount();
            Log.d("imageSize", "imageSize: " + imageSize);
            int rate = 100;
            if (imageSize > 1024 * 100) {
                rate = 100 * 1024 * 100 /(3* imageSize);
            }



            if(selectedPath1.contains(".png"))
            {
                image.compress(Bitmap.CompressFormat.PNG, 100, fos);

            }else
            {
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            fos.close();


        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
        return pictureFile;
    }


    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String dirPath = "/";
        String[] dirPathArray = selectedPath1.split("/");
        for (int i = 0; i < dirPathArray.length - 1; i++) {
            dirPath = dirPath + dirPathArray[i] + "/";
        }
        Log.v("dirPath", dirPath);

        File mediaStorageDir = new File(dirPath);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
//
        try {
            String strUTF8 = URLEncoder.encode(regName, "UTF-8");
            regName = strUTF8;
            Log.v("regName", regName);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



        File mediaFile;
        String mImageName = regName + ".png";


//        mImageName = "333小.png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

        compressedPath = mediaStorageDir.getPath() + File.separator + mImageName;
        return mediaFile;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == -1) {
            Uri selectedImageUri = data.getData();
            if (requestCode == 1)

            {

                selectedPath1 = getPath(selectedImageUri);

                Log.v("selectedPath1", selectedPath1);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int smallOne = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - smallOne) / 2, (bitmap.getHeight() - smallOne) / 2, smallOne, smallOne);
                smallHead = Bitmap.createScaledBitmap(resizedBitmap, 200, 200, false);
                headImg.setImageBitmap(smallHead);

                uploadTip.setVisibility(View.INVISIBLE);
                headButton.setText("");



//                (new MyTask()).execute("");




            }


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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }



    public void checkAgreement(View v)
    {
        Intent intent = new Intent(RegisterActivity.this, agreementActivity.class);
        startActivity(intent);
    }

}