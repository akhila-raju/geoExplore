package com.geoexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class AddPOIActivity extends Activity {

    private Button createBtn, backBtn;
    private LinearLayout globalScreen;

    private OnClickListener mCreatePOIClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Sup", Toast.LENGTH_SHORT).show();
        }
    };
    private OnClickListener mGlobalClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poi);

        globalScreen = (LinearLayout)findViewById(R.id.global_screen);
        globalScreen.setOnClickListener(mGlobalClickListener);

        createBtn = (Button)findViewById(R.id.save_btn);
        createBtn.setOnClickListener(mCreatePOIClickListener);
    }

}
