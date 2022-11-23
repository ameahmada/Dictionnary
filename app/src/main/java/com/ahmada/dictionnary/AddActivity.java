package com.ahmada.dictionnary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.textfield.TextInputEditText;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class AddActivity extends AppCompatActivity {

    DatabaseHelper myDbHelper;
    Cursor c = null;

    public String definition;
    public String enWord;
    public String frWord;
    public String wollof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ajouter mot");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        myDbHelper = new DatabaseHelper(this);

        try{
            myDbHelper.openDataBase();
        }catch(SQLException e){
            throw e;
        }

        AppCompatButton btnAdd = (AppCompatButton) findViewById(R.id.bntAdd);

        btnAdd.setOnClickListener((v) -> {
            TextInputEditText frText = (TextInputEditText) findViewById(R.id.frWord);
            frWord = frText.getText().toString();

            TextInputEditText enText = (TextInputEditText) findViewById(R.id.enWord);
            enWord = enText.getText().toString();

            TextInputEditText wfText = (TextInputEditText) findViewById(R.id.wfWord);
            wollof = wfText.getText().toString();

            TextInputEditText defText = (TextInputEditText) findViewById(R.id.defWord);
            definition = enText.getText().toString();

            try {
                myDbHelper.insertWords(frWord, enWord, wollof, definition);

                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this, R.style.MyDialogTheme);
                builder.setTitle("Insertion mot");
                builder.setMessage("Mot inserrer avec succes !");


                AlertDialog dialog = builder.create();

                dialog.show();
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}