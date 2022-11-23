package com.ahmada.dictionnary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ahmada.dictionnary.MeaningActivity;
import com.ahmada.dictionnary.R;

public class FragmentEnglish extends Fragment {
    public FragmentEnglish(){
        //-------------
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_definition, container, false);

        Context context = getActivity();

        TextView text = (TextView) view.findViewById(R.id.textViewD);

        String enWord = ((MeaningActivity)context).enWord;
        text.setText(enWord);

        if(enWord == null){
            text.setText("pas de mots en anglais trouvé");
        }

        return  view;
    }
}
