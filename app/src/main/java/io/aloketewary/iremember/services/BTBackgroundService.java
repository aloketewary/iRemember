package io.aloketewary.iremember.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by AlokeT on 2/6/2018.
 */

public class BTBackgroundService extends IntentService {
    // Must create a default constructor
    public BTBackgroundService() {
        // Used to name the worker thread, important only for debugging.
        super("bt-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


}
