package com.medistore.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.utils.Constants;
import com.medistore.app.utils.SampleDataHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediStoreApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        
        // Apply saved dark mode theme setting on startup
        boolean isDarkMode = prefs.getBoolean(Constants.KEY_DARK_MODE, false);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES 
                           : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);

        boolean sampleDataInserted = prefs.getBoolean(Constants.KEY_SAMPLE_DATA_INSERTED, false);

        if (!sampleDataInserted) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                SampleDataHelper.insertSampleData(db);
                prefs.edit().putBoolean(Constants.KEY_SAMPLE_DATA_INSERTED, true).apply();
            });
        }
    }
}
