package io.aloketewary.iremember.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by AlokeT on 2/8/2018.
 */

public class BootstrapService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(action != null) {
                if(action.equals("android.provider.Telephony.SMS_RECEIVED")) {
                    // DO YOUR STUFF
                    Intent i = new Intent("io.aloketewary.iremember.BTBackgroundService");
                    i.setClass(context, BTBackgroundService.class);
                    context.startService(i);
                } else if (action.equals("BluetoothAdapter.STATE_OFF")) {
                    // DO ANOTHER STUFF
                    Log.d("btoff", "off bosedk");
                }
            }
        }
    }
}