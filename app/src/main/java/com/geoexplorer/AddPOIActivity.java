package com.geoexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPOIActivity extends Activity {

    private EditText locationName, description;
    private SeekBar difficultyBar;
    private ImageView imageUpload;
    private String imagePath;
    private Button createBtn, backBtn;
    private LinearLayout globalScreen;
    private static int RESULT_LOAD_IMAGE = 1;
    private ArrayList<HashMap<String,String>> cardList;

    private OnClickListener mCreatePOIClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<String,String> newCard = new HashMap<String, String>();
            newCard.put("name", locationName.getText().toString());
            newCard.put("description", description.getText().toString());
            newCard.put("difficulty", Integer.toString(difficultyBar.getProgress()));
            newCard.put("imagePath", imagePath);
            cardList.add(newCard);
            Intent intent = new Intent(AddPOIActivity.this, MainActivity.class);
            intent.putExtra("cardList", cardList);
            startActivity(intent);
        }
    };
    private OnClickListener mImageUploadClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imagePath = picturePath;
            imageUpload.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poi);

        globalScreen = (LinearLayout)findViewById(R.id.global_screen);
        globalScreen.setOnClickListener(mGlobalClickListener);

        locationName = (EditText)findViewById(R.id.location_name);
        difficultyBar = (SeekBar)findViewById(R.id.difficulty_bar);
        description = (EditText)findViewById(R.id.description);
        imageUpload = (ImageView)findViewById(R.id.image_upload);
        imageUpload.setOnClickListener(mImageUploadClickListener);
        imagePath = "";

        createBtn = (Button)findViewById(R.id.save_btn);
        createBtn.setOnClickListener(mCreatePOIClickListener);

        cardList = (ArrayList<HashMap<String,String>>)getIntent().getSerializableExtra("cardList");
    }

}
