package jepsen.dk.galgeleg_fragm;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by JacobWorckJepsen on 06/01/16.
 */
public class SingleTon extends Application{
    private static SingleTon ourInstance = new SingleTon();
    public static SingleTon getInstance() {
        return ourInstance;
    }
    public static Galgelogik gl;
    public static Galgelogik getGlInstance() { return gl; };
    public static String[] navn;
    public static String[] score;
    public static Integer[] scoreInt;
    public static String [] navnShow = new String[8];
    public static String [] scoreShow = new String[8];
    public static String [] tempNavn;
    public static Integer [] tempScoreInt;
    public static String [] tempScore;
    public static String [] tempNavnShow = new String[8];
    public static String [] tempScoreShow = new String[8];
    private ArrayList<String> placeHolder;
    private boolean firstStartUp;
    public static Sensor accelerometer;
    public static SensorManager sensorManager;
    SharedPreferences sp;
    public static Vibrator vibe;
    public static MediaPlayer lyd;
    public static boolean doneLoading=false;



    public void onCreate(){
        super.onCreate();
        gl = new Galgelogik();
        checkNetworkConnectionAvailable();
        if(Galgelogik.network){
            Parse.initialize(this);
            hentScore();
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        firstStartUp = sp.getBoolean("startUp",true);
        if(firstStartUp){
            if(Galgelogik.network){
                hentOrd(sp);
            } else {
                gl.standardWords();
                gl.inddelSvaerhedsgrad();
            }

        } else{
            notFirstStartUp();
        }
    }

    public void notFirstStartUp(){
        try {
            placeHolder = (ArrayList) Serialisering.hent(this.getFilesDir() + "/ord.ser");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String i : placeHolder){
            gl.trimmedeOrd.add(i);
        }
        gl.inddelSvaerhedsgrad();
        gl.saetsvaerhedsgrad(2);
    }

    public void startUpFirstTime(){
        try {
            Serialisering.gem(Galgelogik.trimmedeOrd, this.getFilesDir() + "/ord.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
        gl.inddelSvaerhedsgrad();
        gl.saetsvaerhedsgrad(2);
        doneLoading=true;
    }

    public static void tempHighscore(String name){
        tempScoreInt = new Integer[scoreShow.length];
        tempScore = new String[scoreShow.length];
        tempNavn = new String[navnShow.length];

        for (int i = 0; navnShow.length > i; i++) {
            tempScoreInt[i] = Integer.parseInt(scoreShow[i]);
            tempNavn[i] = navnShow[i];
        }
        tempNavn[7]=name;
        tempScoreInt[7]=Integer.valueOf(Long.toString(gl.highscore));

        boolean swapped = true;
        int j = 0;
        int tmpI;
        String tmpS;
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < tempScoreInt.length - j; i++) {
                if (tempScoreInt[i] < tempScoreInt[i + 1]) {
                    tmpI = tempScoreInt[i + 1];
                    tmpS = tempNavn[i + 1];
                    tempScoreInt[i + 1] = tempScoreInt[i];
                    tempNavn[i + 1] = tempNavn[i];
                    tempScoreInt[i] = tmpI;
                    tempNavn[i] = tmpS;
                    swapped = true;
                }
            }
        }

        for (int i = 0; tempScoreInt.length > i; i++) {
            tempScore[i] = tempScoreInt[i].toString();
        }

        //nye array-udfyldning
        for (int i = 0; tempNavnShow.length > i; i++) {
            tempNavnShow[i] = tempNavn[i];
            tempScoreShow[i] = tempScore[i];
        }



    }

    public void hentOrd(final SharedPreferences sp){
        new AsyncTask<String,Void, String>(){

            @Override
            protected String doInBackground(String... params) {
                try {
                    gl.hentOrdFraDr();
                    return "Ordene blev hentet!";
                } catch (Exception e){
                    return "Der skete en fejl!";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                sp.edit().putBoolean("startUp", false).commit();
                startUpFirstTime();
            }
        }.execute();
    }

    public static void hentScore(){
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("HiScore");
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    scoreInt = new Integer[scoreList.size()];
                    score = new String[scoreList.size()];
                    navn = new String[scoreList.size()];

                    for (int i = 0; scoreList.size() > i; i++) {
                        scoreInt[i] = scoreList.get(i).getInt("score");
                        navn[i] = scoreList.get(i).getString("navn");
                    }

                    boolean swapped = true;
                    int j = 0;
                    int tmpI;
                    String tmpS;
                    while (swapped) {
                        swapped = false;
                        j++;
                        for (int i = 0; i < scoreInt.length - j; i++) {
                            if (scoreInt[i] < scoreInt[i + 1]) {
                                tmpI = scoreInt[i + 1];
                                tmpS = navn[i + 1];
                                scoreInt[i + 1] = scoreInt[i];
                                navn[i + 1] = navn[i];
                                scoreInt[i] = tmpI;
                                navn[i] = tmpS;
                                swapped = true;
                            }
                        }
                    }

                    for (int i = 0; scoreInt.length > i; i++) {
                        score[i] = scoreInt[i].toString();
                    }

                    //nye array-udfyldning
                    for (int i = 0; navnShow.length > i; i++) {
                        navnShow[i] = navn[i];
                        scoreShow[i] = score[i];
                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void checkNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            Galgelogik.network = false;
        } else {
            Galgelogik.network = true;
        }
    }
}
