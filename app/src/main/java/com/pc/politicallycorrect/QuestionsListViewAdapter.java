package com.pc.politicallycorrect;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.magnet.mmx.client.api.MMXMessage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Chase on 10/10/15.
 */
public class QuestionsListViewAdapter extends BaseAdapter {
    public ArrayList<MMXMessage> list;
    public Activity activity;
    public static JSONParser jParser = new JSONParser();
    String currMessage;

    public QuestionsListViewAdapter(Activity activity, ArrayList<MMXMessage> list) {
        super();

        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        //if (convertView == null)
        // {
        convertView = inflater.inflate(R.layout.listview_row, null);

        // }
        TextView txtFirst = (TextView) convertView.findViewById(R.id.FirstText);
        TextView txtSecond = (TextView) convertView.findViewById(R.id.SecondText);
        TextView txtThird = (TextView) convertView.findViewById(R.id.ThirdText);
        TextView txtFourth = (TextView) convertView.findViewById(R.id.FourthText);

        txtFirst.setText("Message: " + list.get(position).getContent().get("question"));
        currMessage = list.get(position).getContent().get("question");
        new LoadML().execute();
        txtSecond.setText("Sentiment Analysis: " + "Machine Learning");
        txtThird.setText("Upvotes: " + list.get(position).getContent().get("upvotes"));
        txtFourth.setText("Downvotes: " + list.get(position).getContent().get("downvotes"));
        return convertView;
    }

    class LoadML extends AsyncTask<String, String, String> {

        /**
         * getting All products from url
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = (new ParamsGenerator()).getParams(currMessage);
            JSONObject json = jParser.makeHttpRequest("https://ussouthcentral.services.azureml.net/workspaces/0580607015814c0d982d8631da974eaf/services/7dcbe648fffa4044a7d5cf399c773242/execute?api-version=2.0&details=true", "POST", params);

            try {
                // Check your log cat for JSON response
                Log.d("All Categories: ", json.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {

        }
    }

    public class ParamsGenerator {
        public List<NameValuePair> getParams(String message) {
            HashMap<String, Integer> abuseToIndex = new HashMap<String, Integer>();
            AssetManager am = activity.getAssets();
            InputStream is = null;
            try{
                is = am.open("abuse.txt");
            } catch (Exception e) {

            }
            int i = 0;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("abuseword", line);
                    abuseToIndex.put(line, i);
                    i += 1;
                }
                reader.close();
            } catch (Exception fnfe) {

            }

            ArrayList<Integer> features = new ArrayList<Integer>(); // params <=> feature set
            String[] words = message.split(" ,.;:");
            for (i = 0; i < abuseToIndex.size(); i += 1) {
                features.add(0);

            }
            Log.d("abuse2index: ", "" + abuseToIndex.size());

            for (String word : words) {
                if (abuseToIndex.containsKey(word)) {
                    int index = abuseToIndex.get(word);
                    features.add(features.get(index) + 1);
                }
            }

            features.add(0); // set param "cluster" = 0

            ArrayList<String> paramNames = new ArrayList<String>();
            for (i = 0; i < 1155; i += 1) {
                paramNames.add("x_" + Integer.toString(i));
            }
            paramNames.add("cluster");

            // paramNames -> params
            // Example:
            // x_0 -> 0
            // x_1 -> 2
            // ...
            // x_1154 -> 0
            // cluster -> 0
            // Creating nameValuePairList for that

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (i = 0; i < 1156; i += 1) {
                Log.d("i: ", "" + i);
                params.add(new BasicNameValuePair(paramNames.get(i), Integer.toString(features.get(i))));
            }
            return params;
        }

    }
}

