package com.bitdubai.android_core.app;


import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.bitdubai.android_core.app.common.version_1.apps_manager.FermatAppsManager;
import com.bitdubai.android_core.app.common.version_1.sessions.FermatSessionManager;
import com.bitdubai.android_core.app.common.version_1.util.mail.YourOwnSender;
import com.bitdubai.fermat.R;
import com.bitdubai.fermat_android_api.engine.FermatApplicationSession;
import com.bitdubai.fermat_android_api.engine.FermatFragmentFactory;
import com.bitdubai.fermat_core.FermatSystem;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Reformated by Matias Furszyfer
 */

/**
 * This class, is created by the Android OS before any Activity. That means its constructor is run before any other code
 * written by ourselves.
 *
 * -- Luis.
 */

@ReportsCrashes(//formUri = "http://yourserver.com/yourscript",
        mailTo = "matiasfurszyfer@gmail.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)

public class ApplicationSession extends MultiDexApplication implements Serializable,FermatApplicationSession {


    private static ApplicationSession instance;
    /**
     * Application states
     */
    public static final int STATE_NOT_CREATED=0;
    public static final int STATE_STARTED=1;
    public static final int STATE_STARTED_DESKTOP=2;

    /**
     *  Fermat platform
     */

    private FermatSystem fermatSystem;

    /**
     * Apps manager
     */
    private FermatAppsManager fermatAppsManager;

    /**
     *  Application state
     */
    public static int applicationState=STATE_NOT_CREATED;

    /**
     *  SubApps fragment factories
     */
    private HashMap<String,FermatFragmentFactory> subAppsFragmentfFactories;



    public static ApplicationSession getInstance(){
        return instance;
    }

    /**
     *  Application session constructor
     */

    public ApplicationSession() {
        super();
        fermatSystem = FermatSystem.getInstance();
        fermatAppsManager = new FermatAppsManager(this);

    }


    /**
     *  Method to get the fermat system
     * @return FermatSystem
     */
    public FermatSystem getFermatSystem() {
        if(fermatSystem==null){
            fermatSystem = FermatSystem.getInstance();
        }
        return fermatSystem;
    }

    /**
     * Method to get FermatSessionManager which can manipulate the active session of apps
     * @return WalletSessionManager
     */
    @Deprecated
    public FermatSessionManager getFermatSessionManager() {
        if(fermatAppsManager==null){
            fermatAppsManager = new FermatAppsManager(this);
        }
        return fermatAppsManager.getFermatSessionManager();
    }

    /**
     * Fermat app manager
     *
     * @return FermatAppsManager
     */
    public FermatAppsManager getFermatAppsManager() {
        return fermatAppsManager;
    }

    /**
     *  Method to change the application state from services or activities
     *
     * @param applicationState  is an application state constant from ApplicationSession class
     */

    public void changeApplicationState(int applicationState){
        ApplicationSession.applicationState =applicationState;
    }

    /**
     * Method to get the application state from services or activities
     *
     * @return application state constant from ApplicationSession class
     */

    public int getApplicationState(){
        return applicationState;
    }


    @Override
    public void onTerminate(){
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        ACRA.init(this);
        YourOwnSender yourSender = new YourOwnSender(getApplicationContext());
        ACRA.getErrorReporter().setReportSender(yourSender);
        super.onCreate();
        instance = this;
    }
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       // MultiDex.install(this);
    }



}