package com.cc;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.cc.app.BuildConfig;
import com.cc.app.R;
import com.cc.data.MusicConstantsApp;
import com.cc.di.components.ApplicationComponent;
import com.cc.di.components.DaggerApplicationComponent;
import com.cc.di.components.UserComponent;
import com.cc.di.modules.ApplicationModule;
import com.cc.di.modules.UserModule;
import com.cc.domain.model.User;
import com.cc.helper.Prefs;
import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.cc.ui.karaoke.app.BaseConstants;
import com.cc.ui.karaoke.network.VMApiClient;
import com.cc.ui.karaoke.network.VMApiInterface;


/**
 * Author: NT
 * Since: 10/26/2016.
 */
public class MusicApplication extends Application {
    private ApplicationComponent appComponent;
    private UserComponent userComponent;
    public static volatile Handler applicationHandler = null;
    private SharedPreferences sharedPref;
    private VMApiInterface apiService;

    public static final String PREFERENCE_STORAGE = "storage_path";
    public static final String PREFERENCE_RATE = "sample_rate";
    public static final String PREFERENCE_CALL = "call";
    public static final String PREFERENCE_SILENT = "silence";
    public static final String PREFERENCE_ENCODING = "encoding";
    public static final String PREFERENCE_LAST = "last_recording";
    public static final String PREFERENCE_THEME = "theme";
    private static MusicApplication instance;

    public static MusicApplication getInstance() {
        return instance;
    }

    private void initAppComponent() {
        appComponent = DaggerApplicationComponent.builder().applicationModule(new
                ApplicationModule(this)).build();
        createUserComponent(new User());

    }

    public UserComponent createUserComponent(User user) {
         userComponent = appComponent.plus(new UserModule(user));
        return userComponent;
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (BuildConfig.DEBUG) {
            MultiDex.install(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationHandler = new Handler(getInstance().getMainLooper());

        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(localImageLoaderConfiguration);
        if (BuildConfig.DEBUG) {
//             StrictMode.enableDefaults();
        } else {
//            Timber.plant(new CrashlyticsTree());
        }
        initAppComponent();
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        checkShuffle();

        sharedPref = getSharedPreferences(BaseConstants
                .PREFERENCE_NAME_APP, Context.MODE_PRIVATE);
        apiService = VMApiClient.getClient().create(VMApiInterface.class);

        PreferenceManager.setDefaultValues(this, mmobile.com.karaoke.R.xml.pref_general, false);
    }


    private void checkShuffle() {
        if(Prefs.getBoolean(MusicConstantsApp.PREF_PLAY_SHUFFLE, false)) {
            Prefs.putBoolean(MusicConstantsApp.PREF_PLAY_IS_CHANGE_DATA_SHUFFLE, false);
        }
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPref;
    }

    public VMApiInterface getApiService() {
        return apiService;
    }

    public static int getTheme(Context context, int light, int dark) {
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = shared.getString(PREFERENCE_THEME, "");
        if (theme.equals("Theme_Dark")) {
            return dark;
        } else {
            return light;
        }
    }



    static public String formatTime(int tt) {
        return String.format("%02d", tt);
    }

    public String formatFree(long free, long left) {
        String str = "";

        long diff = left;

        int diffSeconds = (int) (diff / 1000 % 60);
        int diffMinutes = (int) (diff / (60 * 1000) % 60);
        int diffHours = (int) (diff / (60 * 60 * 1000) % 24);
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        if (diffDays > 0) {
            str = getResources().getQuantityString(R.plurals.days, diffDays, diffDays);
        } else if (diffHours > 0) {
            str = getResources().getQuantityString(R.plurals.hours, diffHours, diffHours);
        } else if (diffMinutes > 0) {
            str = getResources().getQuantityString(R.plurals.minutes, diffMinutes, diffMinutes);
        } else if (diffSeconds > 0) {
            str = getResources().getQuantityString(R.plurals.seconds, diffSeconds, diffSeconds);
        }

        return getString(R.string.title_header, MusicApplication.formatSize(this, free), str);
    }

    public static String formatSize(Context context, long s) {
        if (s > 0.1 * 1024 * 1024 * 1024) {
            float f = s / 1024f / 1024f / 1024f;
            return context.getString(R.string.size_gb, f);
        } else if (s > 0.1 * 1024 * 1024) {
            float f = s / 1024f / 1024f;
            return context.getString(R.string.size_mb, f);
        } else {
            float f = s / 1024f;
            return context.getString(R.string.size_kb, f);
        }
    }

    static public String formatDuration(Context context, long diff) {
        int diffMilliseconds = (int) (diff % 1000);
        int diffSeconds = (int) (diff / 1000 % 60);
        int diffMinutes = (int) (diff / (60 * 1000) % 60);
        int diffHours = (int) (diff / (60 * 60 * 1000) % 24);
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        String str = "";

        if (diffDays > 0)
            str = diffDays + context.getString(R.string.days_symbol) + " " + formatTime(diffHours) + ":" + formatTime(diffMinutes) + ":" + formatTime(diffSeconds);
        else if (diffHours > 0)
            str = formatTime(diffHours) + ":" + formatTime(diffMinutes) + ":" + formatTime(diffSeconds);
        else
            str = formatTime(diffMinutes) + ":" + formatTime(diffSeconds);

        return str;
    }
}