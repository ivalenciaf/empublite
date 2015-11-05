package com.commonsware.empublite;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.greenrobot.event.EventBus;

/**
 * Created by ND on 06/02/2015.
 */
public class ModelFragment extends Fragment {
    private BookContents contents = null;
    private SharedPreferences prefs=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(contents == null) {
            new LoadThread(activity).start();
        }
    }

    public BookContents getBook() {
        return contents;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }


    private class LoadThread extends Thread {
        private Context context = null;

        LoadThread(Context context) {
            this.context = context.getApplicationContext();

            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        public void run() {
            Gson gson = new Gson();

            prefs = PreferenceManager.getDefaultSharedPreferences(context);

            try {
                InputStream is = context.getAssets().open("book/contents.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                contents = gson.fromJson(reader, BookContents.class);
                EventBus.getDefault().post(new BookLoadedEvent(contents));
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
            }
        }
    }
}
