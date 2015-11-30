package com.example.sheepcao.dotaertest;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.MultipartRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.makeramen.roundedimageview.RoundedImageView;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.sheepcao.dotaertest.R.id.fragment_list;
import static com.example.sheepcao.dotaertest.R.id.search_close_btn;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private View sideBarView;
    private Button signatureView;
    private Button headButton;
    private Button logOutButton;

    private RoundedImageView headImage;

    private TextView nameLabel;
    RequestQueue mQueue = null;


    private String regName = "";

    int serverResponseCode;


    String selectedPath1;
    String compressedPath;

    ImageLoader imageLoader;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
//    private boolean isGuest;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mQueue = Volley.newRequestQueue(getActivity());
        mQueue = VolleySingleton.getInstance().getRequestQueue();
//        isGuest = true;


        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
//        selectItem(mCurrentSelectedPosition);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v("show", "onActivityCreated");
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);


        mDrawerListView = (ListView) getView().findViewById(R.id.fragment_list);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                R.layout.menu_item,
                android.R.id.text1,
                new String[]{
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                        getString(R.string.title_section4),
                        getString(R.string.title_section5)

                }));
//        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sideBarView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        Log.v("show", "onCreateView");
        final EditText et = new EditText(getActivity());

        nameLabel = (TextView) sideBarView.findViewById(R.id.nameLabel);


        headImage = (RoundedImageView) sideBarView.findViewById(R.id.headImg_bg);
        headImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.male);
        int smallOne = bmp.getWidth() > bmp.getHeight() ? bmp.getHeight() : bmp.getWidth();
        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, (bmp.getWidth() - smallOne) / 2, (bmp.getHeight() - smallOne) / 2, smallOne, smallOne);

        headImage.setImageBitmap(resizedBitmap);


        headButton = (Button) sideBarView.findViewById(R.id.headBtn);
//        headButton.setImageResource(R.drawable.boy);
        headButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.v("show", "head button -----");
                openGallery();

            }
        });

        signatureView = (Button) sideBarView.findViewById(R.id.signature_edit);
        signatureView.setText("签名的力气都拿来打dota了!");
        signatureView.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle("请输入您的签名").setView(et).setPositiveButton("确定", null).setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        signatureView.setText(et.getText());
                        pushSignature(et.getText().toString());

                        ((ViewGroup) et.getParent()).removeView(et);

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                        ((ViewGroup) et.getParent()).removeView(et);

                    }

                });
                builder.show();

            }
        });


        signatureView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                Log.v("actionId", "actionId" + actionId);
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(signatureView.getWindowToken(), 0);
                }
                return false;
            }
        });


        logOutButton = (Button) sideBarView.findViewById(R.id.logout_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(100);
            }
        });

        return sideBarView;

    }

    private void pushSignature(final String signature) {

        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("dotaerSharedPreferences", 0);
        final String name = mSharedPreferences.getString("username", "游客");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/signature.php",
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
                map.put("tag", "signature");
                map.put("content", signature);
                map.put("username", name);

                return map;
            }
        };
        mQueue.add(stringRequest);

    }


    public void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, 1);

    }


    public String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);

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
                Toast.makeText(getActivity(), "头像上传失败", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), "头像上传成功!", Toast.LENGTH_SHORT).show();


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

            try {
                String strUTF8 = URLEncoder.encode((regName + ".png"), "UTF-8");
                regName = strUTF8;
                Log.v("regName", regName);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Log.v("test............", "Content-Disposition: form-data; name=\"111111file\";filename=\"" + regName + "\"" + lineEnd);

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"111111file\";filename=\"" + regName + "\"" + lineEnd);
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


        }

    }


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

//        regName = chineseToUnicode(regName);
//        Log.v("regName", regName);


//        try {
//            regName = new String(regName.getBytes(), "GBK");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//                Log.v("regName", regName);

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
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;

                    AssetFileDescriptor fileDescriptor =null;
                    fileDescriptor =
                            getActivity().getContentResolver().openAssetFileDescriptor( selectedImageUri, "r");

                    bitmap
                            = BitmapFactory.decodeFileDescriptor(
                            fileDescriptor.getFileDescriptor(), null, options);

//                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int smallOne = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - smallOne) / 2, (bitmap.getHeight() - smallOne) / 2, smallOne, smallOne);
                Bitmap smallHead = Bitmap.createScaledBitmap(resizedBitmap, 200, 200, false);
                headImage.setImageBitmap(smallHead);

                final Bitmap cacheImage = resizedBitmap;

                SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("dotaerSharedPreferences", 0);
                String name = mSharedPreferences.getString("username", "游客");


                regName = name;
                File uploadFile = storeImage(smallHead);

                Log.v("length", uploadFile.length() + "");



                String boundary = "*****";

                CustomProgressBar.showProgressBar(getActivity(), false, "上传中");

                Map<String, String> bodyMap = new HashMap<String, String>();
                bodyMap.put("Connection", "Keep-Alive");
                bodyMap.put("Content-Type", "multipart/form-data;charset=utf-8;boundary=" + boundary);


                String nameURLstring = "";
                try {
                    String strUTF8 = URLEncoder.encode(name, "UTF-8");
                    nameURLstring = "http://cgx.nwpu.info/Sites/upload/" + strUTF8 + ".png";
                    Log.v("nameURLstring", nameURLstring);


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                final String url = nameURLstring;

                MultipartRequest stringRequest = new MultipartRequest("http://cgx.nwpu.info/Sites/AndroidImage.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) throws JSONException {
                                Log.v("response", response);
                                VolleySingleton.getInstance().instead(url,cacheImage);
                                CustomProgressBar.hideProgressBar();

                            }


                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("TAG", error.getMessage(), error);
                        CustomProgressBar.hideProgressBar();

                    }
                }, uploadFile, bodyMap);

                mQueue.add(stringRequest);

//                (new MyTask()).execute("");




            }


        }

    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void makeGuest() {
        nameLabel.setText("");
        headImage.setVisibility(View.INVISIBLE);
        signatureView.setVisibility(View.INVISIBLE);
        headButton.setText("点击登陆\n\ndota有你更精彩!");
        headButton.setClickable(false);

        logOutButton.setText("登 录");


        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                R.layout.menu_item,
                android.R.id.text1,
                new String[]{
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                        getString(R.string.title_section4),
                        getString(R.string.title_section5)

                }) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                if (position == 0) {
                    text.setTextColor(Color.GRAY);
                } else
                    text.setTextColor(Color.WHITE);


                return view;
            }
        });


    }

    public void findIdentity() {
        headImage.setVisibility(View.VISIBLE);
        signatureView.setVisibility(View.VISIBLE);
        headButton.setText("");
        headButton.setClickable(true);
        logOutButton.setText("注 销");


        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                R.layout.menu_item,
                android.R.id.text1,
                new String[]{
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                        getString(R.string.title_section4),
                        getString(R.string.title_section5)

                }) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);


                text.setTextColor(Color.WHITE);


                return view;
            }
        });


        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("dotaerSharedPreferences", 0);
        String name = mSharedPreferences.getString("username", "游客");

        if (!name.equals("游客")) {
            nameLabel.setText(name);
            requestSignature(name);
            loadHead(name);
        }

    }

    private void loadHead(String name) {
        imageLoader = VolleySingleton.getInstance().getImageLoaderOne();




        String nameURLstring = "";
        try {
            String strUTF8 = URLEncoder.encode(name, "UTF-8");
            nameURLstring = "http://cgx.nwpu.info/Sites/upload/" + strUTF8 + ".png";
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
                    headImage.setImageBitmap(resizedBitmap);

                } else {

                    headImage.setImageResource(R.drawable.boysmall);
                }
            }
        });


    }


    private void requestSignature(final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://cgx.nwpu.info/Sites/signature.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) throws JSONException {

                        Log.d("TAG SIgnature", response);

                        JSONObject jObject = new JSONObject(response);
                        String content = jObject.getString("content");

                        signatureView.setText(content);


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TAG", error.getMessage(), error);
                signatureView.setText("签名的力气都拿来打dota了!");


            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tag", "getSignature");
                map.put("username", username);


                return map;
            }


        };

        mQueue.add(stringRequest);
    }

    private void selectItem(int position) {
        if (position < 99) {
            mCurrentSelectedPosition = position;
            if (mDrawerListView != null) {
                mDrawerListView.setItemChecked(position, true);
            }
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }
            if (mCallbacks != null) {
                mCallbacks.onNavigationDrawerItemSelected(position);
            }
        } else {
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }
            if (mCallbacks != null) {
                mCallbacks.onNavigationDrawerItemSelected(position);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.i("i", "11111111");

            return true;
        }
//
//        if (item.getItemId() == R.id.action_example) {
//
//
//            Toast.makeText(getActivity(), "Search action.", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle("菜单");
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }


    protected void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, 0);
    }


//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 0) {
//            Uri uri = data.getData();
//            //to do find the path of pic by uri
//            if (uri == null) {
//                //use bundle to get data
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
//                    headButton.setBackground(new BitmapDrawable(photo));
//                }
//
//            }
//        }
//    }


    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("sideBar"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("sideBar");
    }

}
