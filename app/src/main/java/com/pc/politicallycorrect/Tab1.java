package com.pc.politicallycorrect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.util.Log;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getActivity(), AskQuestionActivity.class);
                startActivity(i);
            }
        });
        ListView feedList = (ListView) v.findViewById(R.id.feedList);
        feedList.setAdapter(new FeedListViewAdapter(getActivity(), PoliticallyCorrectApplication.feedQuestions));
        Log.d("T1 setting adapter", PoliticallyCorrectApplication.feedQuestions.toString());
        return v;
    }
}