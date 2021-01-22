package com.example.texttoscan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.w3c.dom.Text;

public class Edit extends AppCompatActivity {

    public EditText textEdit;
    public int startSelection;
    public int endSelection;
    public String selectedText;
    public int textLength;
    Button efinish;
    ImageView ecancel, econfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        String text = intent.getStringExtra(HomeActivity.EXTRA_TEXT);


        textEdit = findViewById(R.id.editText);
        textEdit.setText(text);

        ImageView eBold;
        ImageView eItalic;
        ImageView eUnderline;
        ImageView eStrikethrough;
        ImageView eNormal;

        efinish = findViewById(R.id.finish);
        efinish.setVisibility(View.GONE);
        eBold = findViewById(R.id.bold);
        eItalic = findViewById(R.id.italic);
        eUnderline = findViewById(R.id.underline);
        eStrikethrough = findViewById(R.id.strikethrough);
        //eNormal = findViewById(R.id.normalize);


        eBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get start and end position of selected txt
                startSelection = textEdit.getSelectionStart();
                endSelection = textEdit.getSelectionEnd();

                //get the text from the start point to the end point
                selectedText = "<b>" + textEdit.getText().toString().substring(startSelection, endSelection) + "</b>";

                textLength= selectedText.length();

                //set type to Bold
                final SpannableString spannableString = new SpannableString(selectedText);
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                spannableString.setSpan(boldSpan, 0, +textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                textEdit.getText().replace(startSelection,endSelection, spannableString);
            }
        });

        eItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelection = textEdit.getSelectionStart();
                endSelection = textEdit.getSelectionEnd();

                selectedText = "<i>" + textEdit.getText().toString().substring(startSelection, endSelection) + "</i>'";

                textLength= selectedText.length();

                final SpannableString spannableString = new SpannableString(selectedText);
                StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
                spannableString.setSpan(italicSpan, 0, +textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                textEdit.getText().replace(startSelection,endSelection, spannableString);
            }
        });

        eUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelection = textEdit.getSelectionStart();
                endSelection = textEdit.getSelectionEnd();

                selectedText = "<u>" + textEdit.getText().toString().substring(startSelection, endSelection) + "</u>";

                textLength= selectedText.length();

                final SpannableString spannableString = new SpannableString(selectedText);
                UnderlineSpan underlineSpan = new UnderlineSpan();
                spannableString.setSpan(underlineSpan, 0, +textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                textEdit.getText().replace(startSelection,endSelection, spannableString);
            }
        });

        eStrikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelection = textEdit.getSelectionStart();
                endSelection = textEdit.getSelectionEnd();

                selectedText = textEdit.getText().toString().substring(startSelection, endSelection);

                textLength= selectedText.length();

                final SpannableString spannableString = new SpannableString(selectedText);
                StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
                spannableString.setSpan(strikethroughSpan, 0, +textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                textEdit.getText().replace(startSelection,endSelection, spannableString);
            }
        });

        //Work in progress - Stuck
       /* eNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelection = textEdit.getSelectionStart();
                endSelection = textEdit.getSelectionEnd();

                selectedText = textEdit.getText().toString().substring(startSelection, endSelection);

                textLength= selectedText.length();

                final SpannableString spannableString = new SpannableString(selectedText);
                StyleSpan normalSpan = new StyleSpan(Typeface.NORMAL);
                spannableString.removeSpan(Typeface.BOLD);
                spannableString.removeSpan(Typeface.ITALIC);
                textEdit.getText().replace(startSelection,endSelection, spannableString);
            }
        });*/


        efinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ResultText = findViewById(R.id.editText);
                String returnText = ResultText.getText().toString();

                Intent intent1 = new Intent();
                intent1.putExtra("result", returnText);
                setResult(RESULT_OK, intent1);
                finish();
            }
        }
        );

        econfirm = findViewById(R.id.confirm);
        econfirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            EditText ResultText = findViewById(R.id.editText);
                                            String returnText = ResultText.getText().toString();

                                            Intent intent1 = new Intent();
                                            intent1.putExtra("result", returnText);
                                            setResult(RESULT_OK, intent1);
                                            finish();
                                        }
                                    }
        );

        ecancel = findViewById(R.id.cancel);
        ecancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent homeIntent = new Intent(Edit.this, HomeActivity.class);
                                            setResult(RESULT_OK, homeIntent);
                                            finish();
                                        }
                                    }
        );


    }




}
