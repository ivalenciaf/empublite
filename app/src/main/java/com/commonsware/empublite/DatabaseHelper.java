package com.commonsware.empublite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.*;

import de.greenrobot.event.EventBus;

/**
 * Created by ivan on 10/11/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "empublite.db";
    private static final int SCHEMA_VERSION = 1;

    private static DatabaseHelper instance = null;

    public static DatabaseHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseHelper(ctx, DATABASE_NAME, null, SCHEMA_VERSION);
        }

        return instance;
    }

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (position INTEGER PRIMARY KEY, prose TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void loadNote(int position) {
        new LoadThread(position).run();
    }

    void updateNote(int position, String prose) {
        new UpdateThread(position, prose).run();
    }

    private class LoadThread extends Thread {
        private int position = -1;

        public LoadThread(int position) {
            super();
            this.position = position;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            String args[] = {String.valueOf(position)};

            Cursor c = getReadableDatabase().rawQuery("select PROSE from NOTES where position=?", args);
            if(c.getCount() > 0) {
                c.moveToFirst();

                EventBus.getDefault().post(new NoteLoadedEvent(position, c.getString(0)));

                c.close();
            }
        }
    }

    private class UpdateThread extends Thread {
        private int position = -1;
        private String prose;

        public UpdateThread(int position, String prose) {
            super();
            this.position = position;
            this.prose = prose;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            String args[] = {String.valueOf(position), prose};

            getReadableDatabase().execSQL("insert or replace into NOTES(position, prose) values(?, ?)", args);
        }
    }
}
