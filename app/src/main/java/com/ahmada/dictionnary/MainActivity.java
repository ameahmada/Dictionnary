package com.ahmada.dictionnary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    SearchView search;

    static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;

    SimpleCursorAdapter suggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (SearchView) findViewById(R.id.search_view);

        search.setOnClickListener((v) -> {
                search.setIconified(false);
                //Intent intent = new Intent(MainActivity.this, MeaningActivity.class);
                //startActivity(intent);

        });

        myDbHelper = new DatabaseHelper(this);

        if(myDbHelper.checkDataBase()){
            openDatabase();
        }else{
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }

        //Simple Cursor Adapter
        final String[] from = new String[] {"french"};
        final int[] to  = new int[] {R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestion_row, null, from, to,0){
            @Override
            public void changeCursor(Cursor cursor){
                super.swapCursor(cursor);
            }
        };

        search.setSuggestionsAdapter(suggestionAdapter);

        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @SuppressLint("Range")
            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                String clicked_word = cursor.getString(cursor.getColumnIndex("french"));
                search.setQuery(clicked_word, false);

                search.clearFocus();
                search.setFocusable(false);

                Intent intent = new Intent(MainActivity.this, MeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("french", clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = search.getQuery().toString();

                Cursor c = myDbHelper.getMeaning(text);

                if(c.getCount() == 0){
                    search.setQuery("", false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Mot non trouvé");
                    builder.setMessage("Mot n'ont trouvé dans le dictionnaire !");

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    //---------
                                }
                            });
                    String negativeText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    search.clearFocus();
                                }
                            });
                    AlertDialog dialog = builder.create();

                    dialog.show();
                }else{
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, MeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("french", text);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search.setIconifiedByDefault(false);
                Cursor cursorSuggestion = myDbHelper.getSuggestions(s);
                suggestionAdapter.changeCursor(cursorSuggestion);

                return false;
            }
        });
    }

    protected static void openDatabase(){
        try{
            myDbHelper.openDataBase();
            databaseOpened = true;
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_exit){
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}