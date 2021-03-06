package com.example.strictmodefix;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;

public class App extends Application {   
    
    @Override
    public void onCreate() {        
        if (BuildConfig.DEBUG) {
            enableStrictMode();
        }
        super.onCreate();        
    }
        
    private static void enableStrictMode() {
        if (Build.VERSION.SDK_INT >= 9) {
            doEnableStrictMode();
        }
            
        if (Build.VERSION.SDK_INT >= 16) {
            //restore strict mode after onCreate() returns.
            new Handler().postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    doEnableStrictMode();
                }
            });
        }
    }

    @TargetApi(9)
    private static void doEnableStrictMode() {
        ThreadPolicy.Builder threadBuilder;
        VmPolicy.Builder vmBuilder;

        threadBuilder = new ThreadPolicy.Builder();
        vmBuilder = new VmPolicy.Builder();

        int version = Build.VERSION.SDK_INT;
        if (version >= 9) {
            updateStrictModePolicies_v9(threadBuilder, vmBuilder);
        }

        if (version >= 11) {
            updateStrictModePolicies_v11(threadBuilder, vmBuilder);
        }
        
        if (version >= 16) {
            updateStrictModePolicies_v16(threadBuilder, vmBuilder);
        }

        StrictMode.setThreadPolicy(threadBuilder.build());
        StrictMode.setVmPolicy(vmBuilder.build());
    }

    @TargetApi(9)
    private static void updateStrictModePolicies_v9(ThreadPolicy.Builder threadBuilder,
            VmPolicy.Builder vmBuilder) {
        
        threadBuilder.detectAll()
                     .penaltyLog();
                     
        vmBuilder.detectLeakedSqlLiteObjects()
                 .penaltyLog();
    }

    @TargetApi(11)
    private static void updateStrictModePolicies_v11(ThreadPolicy.Builder threadBuilder,
            VmPolicy.Builder vmBuilder) {
        
        threadBuilder.penaltyFlashScreen();
        
        vmBuilder.detectLeakedClosableObjects()
                 .detectActivityLeaks();
    }
    
    @TargetApi(16)
    private static void updateStrictModePolicies_v16(ThreadPolicy.Builder threadBuilder,
            VmPolicy.Builder vmBuilder) {
        
        vmBuilder.detectLeakedRegistrationObjects();
    }
}
