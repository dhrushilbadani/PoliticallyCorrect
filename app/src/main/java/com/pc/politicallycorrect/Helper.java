
package com.pc.politicallycorrect;

import android.content.Context;
import android.util.Log;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Helper {

    static HashMap<String, String> id2questions;
    static HashMap<String, Integer> id2upvotes;
    static HashMap<String, Integer> id2downvotes;
    static File downvotesFile, upvotesFile, questionsFile;

    public static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public static void getFiles(Context context) {
        try {
            if (!fileExists(context, "downvotes.ser")) {
                downvotesFile = new File(context.getFilesDir(), "downvotes.ser");
                id2downvotes = new HashMap<>();
                upvotesFile = new File(context.getFilesDir(), "upvotes.ser");
                id2upvotes = new HashMap<>();
                questionsFile = new File(context.getFilesDir(), "questions.ser");
                id2questions = new HashMap<>();
            } else {
                FileInputStream fis = context.openFileInput("downvotes.ser");
                ObjectInputStream is = new ObjectInputStream(fis);
                id2downvotes = (HashMap<String, Integer>) is.readObject();
                FileInputStream fis2 = context.openFileInput("upvotes.ser");
                ObjectInputStream is2 = new ObjectInputStream(fis2);
                id2upvotes = (HashMap<String, Integer>) is2.readObject();
                FileInputStream fis3 = context.openFileInput("questions.ser");
                ObjectInputStream is3 = new ObjectInputStream(fis3);
                id2questions = (HashMap<String, String>) is3.readObject();
                is3.close();
                fis3.close();
                is2.close();
                fis2.close();
                is.close();
                fis.close();
            }
        } catch(Exception e) {
            Log.d("exception loading", e.toString());
        }
    }

    public static void saveFiles(Context context){
        try {
            FileOutputStream fos = context.openFileOutput("downvotes.ser", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(id2downvotes);
            FileOutputStream fos2 = context.openFileOutput("upvotes.ser", Context.MODE_PRIVATE);
            ObjectOutputStream os2 = new ObjectOutputStream(fos2);
            os2.writeObject(id2upvotes);
            FileOutputStream fos3 = context.openFileOutput("questions.ser", Context.MODE_PRIVATE);
            ObjectOutputStream os3 = new ObjectOutputStream(fos3);
            os3.writeObject(id2questions);
            os3.close();
            fos3.close();
            os2.close();
            fos2.close();
            os.close();
            fos.close();
        } catch(Exception e) {
            Log.d("exception saving", e.toString());
        }

    }

    // Adding new question
    public static void addQuestion(Context context, String id, String question) {
        getFiles(context);
        id2downvotes.put(id, 0);
        id2upvotes.put(id, 0);
        id2questions.put(id, question);
        saveFiles(context);
    }

    public static void upvoteQuestion(Context context, String id) {
        getFiles(context);
        id2upvotes.put(id, id2upvotes.get(id)+1);
        saveFiles(context);
    }

    public static void downvoteQuestion(Context context, String id) {
        getFiles(context);
        id2downvotes.put(id, id2downvotes.get(id)+1);
        saveFiles(context);
    }

    public static ArrayList<MMXMessage> getAllQuestions(Context context) {
        getFiles(context);
        ArrayList<MMXMessage> allQuestions = new ArrayList<>();
        Set<MMXUser> dummyRecipients = new HashSet<MMXUser>();
        dummyRecipients.add(MMX.getCurrentUser());
        for (String id: id2questions.keySet()) {
            String question = id2questions.get(id);
            int downvotes = id2downvotes.get(id);
            int upvotes = id2upvotes.get(id);
            HashMap<String, String> content = new HashMap<String, String>();
            content.put("question", question);
            content.put("id", id);
            content.put("downvotes", downvotes+"");
            content.put("upvotes", upvotes+"");
            MMXMessage message = new MMXMessage.Builder()
                    .recipients(dummyRecipients)
                    .content(content)
                    .build();
            allQuestions.add(message);
        }
        return allQuestions;
    }


}