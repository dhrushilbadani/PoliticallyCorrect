package com.pc.politicallycorrect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_2,container,false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getActivity(), AskQuestionActivity.class);
                startActivity(i);
            }
        });
        ListView lv = (ListView) v.findViewById(R.id.listview);
        PoliticallyCorrectApplication.myQuestions = Helper.getAllQuestions(getActivity());
        Log.d("T2 ", PoliticallyCorrectApplication.myQuestions.toString());
        lv.setAdapter(new QuestionsListViewAdapter(getActivity(), PoliticallyCorrectApplication.myQuestions));
        return v;
    }
}