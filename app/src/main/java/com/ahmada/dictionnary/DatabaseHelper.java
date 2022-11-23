package com.ahmada.dictionnary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String db_path = null;
    private static final String db_name = "dictionnary.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context){
        super(context, db_name, null, DATABASE_VERSION);
        this.myContext = context;
        this.db_path = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", db_path);
    }

    public void createDatabase() throws  IOException{
        boolean dbExist = checkDataBase();
        if(!dbExist){

            this.getReadableDatabase();
            try{
                copyDataBase();
            }catch (IOException e){
                throw  new Error("Error copying database !!!");
            }
        }
    }

    public boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = db_path + db_name;
            checkDB = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
        }catch (SQLException e){
            //
        }
        if(checkDB != null){
            checkDB.close();
        }
        return  checkDB != null ? true:false;
    }

    private void copyDataBase() throws IOException{
        InputStream myInput = myContext.getAssets().open(db_name);
        String outFileName = db_path + db_name;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.i("copyDataBase", "Database copied");
    }

    public void openDataBase() throws  SQLException{
        String myPath = db_path + db_name;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try{
            this.getReadableDatabase();
            myContext.deleteDatabase(db_name);
            copyDataBase();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Cursor getMeaning(String text){
        Cursor c = myDataBase.rawQuery("SELECT * FROM mots WHERE french=='"+text+"'",null);
        return c;
    }

    public Cursor getSuggestions(String text){
        Cursor c = myDataBase.rawQuery("SELECT _id,french FROM mots WHERE french LIKE '"+text+"%' LIMIT 20 ",null);
        return c;
    }

    public void insertHistory(String text){
        myDataBase.execSQL("INSERT INTO history(word) VALUES (UPPER('"+text+"'))");
    }

    public void insertWords(String french, String english, String wollof, String definition){
        String req = "INSERT INTO mots(definition,french,english,wollof) VALUES ('"+definition+"','"+french+"','"+english+"','"+wollof+"')";
        myDataBase.execSQL(req);
    }

    @SuppressLint("Range")
    public int deleteWords(String text){
        Cursor c = myDataBase.rawQuery("SELECT * FROM mots WHERE french=='"+text+"'",null);
        String mots;
        System.out.println(text + "  0 ");
        if(c.moveToFirst()) {
            //mots = c.getString(c.getColumnIndex("definition"));
            myDataBase.execSQL("DELETE FROM mots WHERE french == '"+text+"'");
            System.out.println(text + "  1");
            return 1;
        }else{
            System.out.println(text + "  -1 ");
            return -1;
        }
    }
}
