package com.ahmada.dictionnary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.ahmada.dictionnary.fragments.FragmentDefintion;
import com.ahmada.dictionnary.fragments.FragmentEnglish;
import com.ahmada.dictionnary.fragments.FragmentFrench;
import com.ahmada.dictionnary.fragments.FragmentWollof;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.lang.CharSequence;
import java.util.Locale;

public class MeaningActivity extends AppCompatActivity {
    private ViewPager viewPager;

    String frWord;
    DatabaseHelper myDbHelper;
    Cursor c = null;

    public String definition;
    public String enWord;
    public String frWords;
    public String wollof;

    TextToSpeech tts;


    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        // Les mots recus
        Bundle bundle = getIntent().getExtras();
        frWord = bundle.getString("french");

        myDbHelper = new DatabaseHelper(this);

        try{
            myDbHelper.openDataBase();
        }catch(SQLException e){
            throw e;
        }

        c = myDbHelper.getMeaning(frWord);

        if(c.moveToFirst()){
            definition = c.getString(c.getColumnIndex("definition"));
            enWord = c.getString(c.getColumnIndex("english"));
            frWords = c.getString(c.getColumnIndex("french"));
            wollof = c.getString(c.getColumnIndex("wollof"));
        }

        myDbHelper.insertHistory(frWord);

        ImageButton btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(MeaningActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            int result = tts.setLanguage(Locale.getDefault());
                            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("error", "Langages non supporté");
                            }else{
                                tts.speak(frWord, TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                        else{
                            Log.e("error", "Initialisation échoué");
                        }
                    }
                });
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(frWord);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);

        if(viewPager != null){
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager){

            super(manager);
        }

        void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position){

            return mFragmentList.get(position);
        }

        @Override
        public int getCount(){

            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentDefintion(), "Definition");
        adapter.addFrag(new FragmentEnglish(), "Anglais");
        adapter.addFrag(new FragmentFrench(), "Français");
        adapter.addFrag(new FragmentWollof(), "Wollof");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}