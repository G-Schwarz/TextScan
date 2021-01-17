package com.example.texttoscan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "EDIT_TEXT";

    EditText mResultET;
    TextView mResult;
    ImageView mPreviewIV;
    Button mSpeakBtn, mStopBtn;
    TextToSpeech mTTS;
    ImageView mAddImg;
    ImageButton mAddBtn, mEdit;
    ScrollView mHome;


    private static final int CAMERA_REQUEST_CODE =200;
    private static final int STORAGE_REQUEST_CODE =400;
    private static final int IMAGE_PICK_GALLERY_CODE =1000;
    private static final int IMAGE_PICK_CAMERA_CODE =1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadLocale();
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Text to Scan");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(R.string.mainHint);


        ActionBar actionBar1 = getSupportActionBar();
        actionBar1.setTitle(getResources().getString(R.string.app_name));
        actionBar1.setElevation(0);

        mResultET=findViewById(R.id.ResultET);
        mPreviewIV=findViewById(R.id.ImageIV);
        mSpeakBtn=findViewById(R.id.speakBtn);
        mStopBtn=findViewById(R.id.stopBtn);
        mResult=findViewById(R.id.result);
        mHome = findViewById(R.id.mainhome);
        mAddBtn=findViewById(R.id.addImg);
        mEdit = findViewById(R.id.EditResult);


        mAddBtn.setVisibility(View.VISIBLE);
        mHome.setVisibility(View.GONE);
        mStopBtn.setVisibility(View.GONE);
        mSpeakBtn.setVisibility(View.VISIBLE);


        mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR){
                    //check if there is no error then set language
                    mTTS.setLanguage(Locale.US);
                }
                else {
                    //Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Speak button Clicked
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mStopBtn.setVisibility(View.VISIBLE);
                mSpeakBtn.setVisibility(View.GONE);
                //get text from edit text
                String toSpeak = mResultET.getText().toString().trim();
                if (toSpeak.equals("")){
                    //if there is no text in the result
                    Toast.makeText(HomeActivity.this, R.string.nhaptext, Toast.LENGTH_SHORT).show();
                }
                else {
                    //Speak the text
                    mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        //Stop button Clicked
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                mSpeakBtn.setVisibility(View.VISIBLE);
                mStopBtn.setVisibility(View.GONE);
                if (mTTS.isSpeaking()){
                    //if is speaking then stop
                    mTTS.stop();
                }
                else{
                    //not speaking
                    Toast.makeText(HomeActivity.this, R.string.khongdoc, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //get text from edit text
                String toSpeak = mResultET.getText().toString().trim();
                if (toSpeak.equals("")){
                    //if there is no text in the result
                    Toast.makeText(HomeActivity.this, R.string.nhaptext, Toast.LENGTH_SHORT).show();
                }
                else {
                    //Speak the text
                    mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showImageImportDialog();

            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

               
            }
        });
        //camera permission
        cameraPermission=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //storage permission
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    }


    //actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memu_main, menu);
        return true;
    }

    //handle actionbar item click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id==R.id.addImage){
            showImageImportDialog();
        }
        if (id==R.id.save){
            final EditText saveEditText = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.tenfile)
                    .setMessage(R.string.dcluu)
                    .setView(saveEditText)
                    .setPositiveButton(R.string.luu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String filename = saveEditText.getText().toString();
                            String content = mResultET.getText().toString();

                            if(!filename.equals("") && !content.equals("")){
                                saveTextAsFile(filename, content);
                            }
                        }
                    })
                    .setNegativeButton(R.string.huytext, null)
                    .create();
            dialog.show();
        }
        if (id==R.id.editting){
            EditText editText = findViewById(R.id.ResultET);
            String text = editText.getText().toString();

            Intent intent = new Intent(this, Edit.class);
            intent.putExtra(EXTRA_TEXT, text);
            startActivityForResult(intent, 1);
        }
        if (id==R.id.files){
            String Path = Environment.getExternalStorageDirectory()+File.separator+"MyScannedFiles";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri mydir = Uri.parse("file://" + Path);
            intent.setDataAndType(mydir, "*/*");
        }
        if(id==R.id.languages) {
            showChangeLanguageDialog();
        }
        if(id==R.id.setting) {
            showChangeLanguageDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    //Language
    private void showChangeLanguageDialog() {
        //array of language to display in alert dialog
        final String[] listItems = {"English", "French", "日本語", "한국어", "Tiếng Việt"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        mBuilder.setTitle(R.string.chonngonngu);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i==0){
                    //English
                    setLocale("en");
                    recreate();
                }
                else if (i==1){
                    //French
                    setLocale("fr");
                    recreate();
                }
                else if (i==2){
                    //Japanese
                    setLocale("ja");
                    recreate();
                }
                else if (i==3){
                    //Korean
                    setLocale("ko");
                    recreate();
                }
                else if (i==4){
                    //Vietnamese
                    setLocale("vn");
                    recreate();
                }

                //dismiss alert dialog when language selected
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.show();
        mDialog.show();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration(   );
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Language", language);
        editor.apply();
    }

    //load language saved in shared preferences
    public void LoadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String Language = prefs.getString("My_Language", "");
        setLocale(Language);
    }

    //Save
    private void saveTextAsFile(String filename, String content){
        String Filename = filename + ".html";

        if(!checkStoragePermission()){
            //Chua cho phep thu vien. Can gui yeu cau
            requestStoragePermission();
        }
        else {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Log.d("MyApp", "No SDCARD");
            } else {
                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"MyScannedFiles");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            }
        }

        String Path = Environment.getExternalStorageDirectory()+File.separator+"MyScannedFiles";

        //create file
        File file = new File(Path, Filename);

        //write file
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this, R.string.daluufile, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, R.string.khongtimthayfile, Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, R.string.loiluufile, Toast.LENGTH_SHORT).show();
        }

    }

    private void showImageImportDialog() {
        //item to display in dialog
        String[] items = {getString(R.string.mayanh), getString(R.string.thuvien)};
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        //set title
        dialog.setTitle(R.string.chonnguonanh);
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                    //option may anh clicked
                    //Kiem tra cho phep may anh
                    if(!checkCameraPermission()){
                        //Chua cho phep may anh. Can gui yeu cau
                        requestCameraPermission();
                    }
                    else {
                        //Da cho phep may anh
                        pickCamera();

                    }
                }
                if(which==1){
                    //option thu vien clicked
                    if(!checkStoragePermission()){
                        //Chua cho phep thu vien. Can gui yeu cau
                        requestStoragePermission();
                    }
                    else {
                        //Da cho phep thu vien anh
                        pickGallery();
                        mAddBtn.setVisibility(View.GONE);
                        mHome.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        dialog.create().show(); //show dialog
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);

        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        //intent to take image from camera
        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        image_uri= getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

        mAddBtn.setVisibility(View.GONE);
        mHome.setVisibility(View.VISIBLE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean resultSto= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return resultSto;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //in order to have high quality image we need save image to external storage first
        boolean resultcam= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultSto= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return resultcam && resultSto;
    }


    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && StorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this, R.string.deniedrequest, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (StorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this, R.string.deniedrequest, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    //handle image result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //crop image after get it
                //enable image guidelines
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if(requestCode == 1){
                String textResult = data.getStringExtra("result");
                mResultET.setText(textResult);
            }
        }
        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri(); //get image uri

                //set image to image view
                mPreviewIV.setImageURI(resultUri);

                //drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIV.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder stringb = new StringBuilder();
                    //get text from stringb untill no text left
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        stringb.append(myItem.getValue());
                        stringb.append("\n");
                    }
                    //set text to edit text
                    mResultET.setText(stringb.toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }
}
