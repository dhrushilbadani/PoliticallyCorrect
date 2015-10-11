package com.pc.politicallycorrect;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magnet.mmx.client.api.MMXMessage;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Chase on 10/10/15.
 */
public class FeedListViewAdapter extends BaseAdapter {

        public ArrayList<MMXMessage> list;
        Activity activity;
        String id;
        int curr;
        MMXMessage currMessage;
        View currParent;

        public FeedListViewAdapter(Activity activity, ArrayList<MMXMessage> list) {
            super();
            this.activity = activity;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.feedlist_view, null);
            }
            RelativeLayout rl = (RelativeLayout) convertView;
            TextView txtView = (TextView) convertView.findViewById(R.id.MessageText);
            Button buttonUp = (Button) convertView.findViewById(R.id.UpvoteButton);
            Log.d("getContent", list.get(position).getContent().toString());
            Button buttonDown = (Button) convertView.findViewById(R.id.DownvoteButton);
            txtView.setText(list.get(position).getContent().get("question"));
            id = list.get(position).getContent().get("id");
            curr = position;
            currMessage = list.get(curr);
            currParent = parent;
            buttonUp.setOnClickListener
                    (new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> content = new HashMap<String, String>();
                            content.put("id", id);
                            content.put("vote", "1");
                            currMessage.reply(content, new MMXMessage.OnFinishedListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Log.d("Upvoted succesfully", "Upvoted succesfully");
                                }

                                @Override
                                public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {

                                }
                            });
                            list.remove(curr);
                            ((ListView) currParent).invalidateViews();
                        }
                    });

            buttonDown.setOnClickListener
                    (new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> content = new HashMap<String, String>();
                            content.put("id", id);
                            content.put("vote", "-1");
                            currMessage.reply(content, new MMXMessage.OnFinishedListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Log.d("Downvoted succesfully", "Downvoted succesfully");
                                }

                                @Override
                                public void onFailure(MMXMessage.FailureCode failureCode, Throwable throwable) {

                                }
                            });
                            list.remove(curr);
                            ((ListView) currParent).invalidateViews();
                        }
                    });
            return convertView;
        }

    }
