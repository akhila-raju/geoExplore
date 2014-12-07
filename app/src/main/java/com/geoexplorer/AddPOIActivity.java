package com.geoexplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddPOIActivity extends Activity {

    private Button createBtn, backBtn;

    private OnClickListener mCreatePOIClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Sup", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poi);

        createBtn = (Button)findViewById(R.id.create_btn);
        createBtn.setOnClickListener(mCreatePOIClickListener);
    }

}
