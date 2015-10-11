package com.pc.politicallycorrect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AskQuestionActivity extends ActionBarActivity {
    String question= "", username="", password = "password";
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        final EditText questionView = (EditText) findViewById(R.id.question);
        Button b = (Button) findViewById(R.id.askButton);

        String ts = Context.TELEPHONY_SERVICE;
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(ts);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();
        username = imsi + imei;
        //Sign up if not logged in
        if (MMX.getCurrentUser() == null) {
            MMXUser user = new MMXUser.Builder().username(username).build();
            user.register(password.getBytes(), new MMXUser.OnFinishedListener<Void>() {
                public void onSuccess(Void aVoid) {
                    //Successful registration.
                }

                public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {

                    if (MMXUser.FailureCode.REGISTRATION_INVALID_USERNAME.equals(failureCode)) {
                        //handle registration failure
                    }
                }
            });
        }
        //Log in now
        MMX.login(username, password.getBytes(), new MMX.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                //success!
                //if an EventListener has already been registered, start the MMX messaging service
                Log.d("Log in", "successful");
                MMX.start();
            }

            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                if (MMX.FailureCode.SERVER_AUTH_FAILED.equals(failureCode)) {
                    //login failed, probably an incorrect password
                }
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            /**
             * Find a random sampling of a list size LIMIT
             */
            public Set<MMXUser> getRandomSample(List<MMXUser> users, int LIMIT) {
                Set<MMXUser> usersNew = new HashSet<MMXUser>(LIMIT);
                if (LIMIT >= users.size()) {
                    usersNew = new HashSet<MMXUser>(users);
                    for (MMXUser u: users) {
                        if (u.getUsername().equals(username)) {
                            usersNew.remove(u);
                            break;
                        }
                    }
                    return usersNew;
                }
                Set<Integer> indices = new HashSet<Integer>();
                while (indices.size() < LIMIT) {
                    indices.add((int) (Math.random() * users.size()));
                }
                for (int i: indices) {
                    if (!users.get(i).getUsername().equals(username))
                        usersNew.add(users.get(i));
                }
                return usersNew;
            }

            @Override
            public void onClick(View v) {
                Log.d("Clicked", "button");
                question = questionView.getText().toString();
                Log.d("Question", question);
                MMXUser.getAllUsers(0, 10, new MMXUser.OnFinishedListener<ListResult<MMXUser>>() {
                    @Override
                    public void onSuccess(ListResult<MMXUser> mmxUserListResult) {
                        //Success
                        Log.d("Get all users", mmxUserListResult.items.toString());
                        Set<MMXUser> randomUsers = getRandomSample(mmxUserListResult.items, 10);
                        Log.d("randomUsers ", randomUsers.toString());
                        HashMap<String, String> content = new HashMap<String, String>();
                        content.put("question", question);
                        String id = username + question;
                        content.put("id", id);
                        Log.d("Asking hash", content.toString());
                        MMXMessage message = new MMXMessage.Builder()
                                .recipients(randomUsers)
                                .content(content)
                                .build();
                        HashMap<String, String> content2 = new HashMap<String, String>(content);
                        content2.put("upvotes", "0");
                        content2.put("downvotes", "0");
                        MMXMessage message2 = new MMXMessage.Builder().recipients(randomUsers)
                                .content(content2).build();
                        PoliticallyCorrectApplication.myQuestions.add(message2);
                        Log.d("Ask activity", PoliticallyCorrectApplication.myQuestions.toString());
                        Log.d("Ask message", message.toString());
                        String messageId = message.send(new MMXMessage.OnFinishedListener<String>() {
                            public void onSuccess(String s) {
                                Log.d("Created question!", "");
                            }

                            public void onFailure(MMXMessage.FailureCode failureCode, Throwable e) {
                                Log.d("Failed question!", failureCode.toString());
                            }

                        });
                        Log.d("Ask message2", message.toString());
                        Helper.addQuestion(getApplicationContext(), id, question);
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                    @Override
                    public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                        //Failure
                        Log.d("randomUsers", "Failed getting all users!");
                    }
                });
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ask_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
