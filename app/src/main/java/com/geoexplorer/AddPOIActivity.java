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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.HashMap;

public class AddPOIActivity extends Activity {

    private EditText locationName, description;
    private SeekBar difficultyBar;
    private Button createBtn, backBtn;
    private LinearLayout globalScreen;

    private OnClickListener mCreatePOIClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<String,String> newCard = new HashMap<String, String>();
            newCard.put("name", locationName.getText().toString());
            newCard.put("description", description.getText().toString());
            newCard.put("difficulty", Integer.toString(difficultyBar.getProgress()));
            Intent intent = new Intent(AddPOIActivity.this, MainActivity.class);
            intent.putExtra("newCard", newCard);
            startActivity(intent);
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

        locationName = (EditText)findViewById(R.id.location_name);
        difficultyBar = (SeekBar)findViewById(R.id.difficulty_bar);
        description = (EditText)findViewById(R.id.description);

        createBtn = (Button)findViewById(R.id.save_btn);
        createBtn.setOnClickListener(mCreatePOIClickListener);
    }

}
