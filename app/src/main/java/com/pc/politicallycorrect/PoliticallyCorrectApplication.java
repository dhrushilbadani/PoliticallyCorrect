package com.pc.politicallycorrect;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.mmx.client.common.Log;

import java.util.ArrayList;

/**
 * Created by dhrushil on 10/10/15.
 */
public class PoliticallyCorrectApplication extends Application {
    private int mNoteId = 0;
    public static ArrayList<MMXMessage> myQuestions;
    public static ArrayList<MMXMessage> feedQuestions;
    private MMX.EventListener mListener =
            new MMX.EventListener() {
                public boolean onMessageReceived(MMXMessage mmxMessage) {
                    Log.d("Application Class", "received message");
                    Log.d("Received Message: ", mmxMessage.toString());
                    doNotify(mmxMessage);
                    if (mmxMessage.getContent().containsKey("vote")) { //somebody has voted on your message
                        int vote = Integer.parseInt(mmxMessage.getContent().get("vote"));
                        String id = mmxMessage.getContent().get("id");
                        if (vote == -1 ||mmxMessage.getContent().get("vote").startsWith("-")) {
                            Helper.downvoteQuestion(getApplicationContext(), id);
                        } else {
                            Helper.upvoteQuestion(getApplicationContext(), id);
                        }
                    } else if (mmxMessage.getContent().containsKey("question")) {
                        if (!feedQuestions.contains(mmxMessage))
                            feedQuestions.add(mmxMessage);
                    }
                    // Make a call to update

                    return false;
                }
            };

    public void onCreate() {
        super.onCreate();
        Log.setLoggable(null, Log.VERBOSE);
        MMX.init(this, R.raw.politicallycorrect);
        MMX.registerListener(mListener);

        myQuestions = Helper.getAllQuestions(getApplicationContext());
        feedQuestions = new ArrayList<>();
        Log.d("Application Class", "loaded both feeds");
        // Optionally register a wakeup broadcast intent.  This will be broadcast when a GCM message
        // for this MMX application.  If configure properly, the MMX server will send this GCM  to wakeup
        // the device when a message needs to be delivered.  It is up to the developer to define this intent
        // and implement/declare the BroadcastReceiver to handle this intent and thus to call MMX.login()
        // to retrieve pending messages.
        Intent intent = new Intent("QUICKSTART_WAKEUP");
        MMX.registerWakeupBroadcast(intent);
    }

    private void doNotify(MMXMessage message) {
        Object textObj = message.getContent().get("New Notification!");
        if (textObj != null) {
            String messageText = textObj.toString();
            MMXUser from = message.getSender();
            NotificationManager noteMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = new Notification.Builder(this).setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_https_black_18dp).setWhen(System.currentTimeMillis())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle("Message from " + from.getUsername()).setContentText(messageText).build();
            noteMgr.notify(mNoteId++, note);
        }
    }

}
