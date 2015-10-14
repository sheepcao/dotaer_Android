package com.example.sheepcao.dotaertest;

/**
 * Created by ericcao on 10/9/15.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class searchDialogActivity extends Dialog {
    //定义回调事件，用于dialog的点击事件
    public interface OnCustomDialogListener{
        void searchAccount(String name);
    }

    private String name;
    private OnCustomDialogListener customDialogListener;
    EditText etName;

    public searchDialogActivity(Context context,String name,OnCustomDialogListener customDialogListener) {
        super(context);
        this.name = name;
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_score_layout);
        //设置标题
        setTitle(name);
        etName = (EditText)findViewById(R.id.input_account);
        Button clickBtn = (Button) findViewById(R.id.search_button);
        clickBtn.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            customDialogListener.searchAccount(String.valueOf(etName.getText()));
            searchDialogActivity.this.dismiss();
        }
    };

}