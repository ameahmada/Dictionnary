package com.ahmada.dictionnary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.textfield.TextInputEditText;

public class DeleteActivity extends AppCompatActivity {
    DatabaseHelper myDbHelper;
    Cursor c = null;

    public String motSup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Supprimer mot");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        myDbHelper = new DatabaseHelper(this);

        try{
            myDbHelper.openDataBase();
        }catch(SQLException e){
            throw e;
        }

        AppCompatButton btnDelete = (AppCompatButton) findViewById(R.id.bntDelete);

        btnDelete.setOnClickListener((v) -> {
            TextInputEditText frText = (TextInputEditText) findViewById(R.id.deleteWords);
            motSup = frText.getText().toString();

            int result = 0;
            try {
                result = myDbHelper.deleteWords(motSup);
            }catch (SQLException e){
                e.printStackTrace();
            }
            if(result == 1){
                AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this, R.style.MyDialogTheme);
                builder.setTitle("suppression mot");
                builder.setMessage("Mot supprimé avec succes !");

                AlertDialog dialog = builder.create();

                dialog.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this, R.style.MyDialogTheme);
                builder.setTitle("Suppression mot");
                builder.setMessage("Mot introuvable !");
                AlertDialog dialog = builder.create();

                dialog.show();
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