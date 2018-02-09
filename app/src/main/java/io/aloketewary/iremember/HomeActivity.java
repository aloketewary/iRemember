package io.aloketewary.iremember;

import android.app.ActivityManager;
import android.arch.persistence.room.Room;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.aloketewary.iremember.database.AppDatabase;
import io.aloketewary.iremember.model.GpsLocationEntity;
import io.aloketewary.iremember.services.BTBackgroundService;
import io.aloketewary.iremember.services.GpsTracker;
import io.aloketewary.iremember.util.Constant;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    //Room database
    static AppDatabase appDatabase;
    // Bluetooth
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    //Views
    private CardView mGpsCard, mHelpCard, mLocationCard;
    private LinearLayout mBtLinear;
    private Button mBtToogle, mGpsBtn;
    private Toolbar mHomeToolbar;
    private ImageView mBtImageView;
    private TextView mLocationSizeView;
    private int locationSize;
    //Static datas
    private Boolean isBtEnabled = false;
    SharedPreferences sharedPreferences;
    /**
     * This is a Broadcast receiver which receive messages upon activity of certain events
     *
     * @author AlokeT
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onRecieve: STATE OFF");
                        mBtToogle.setBackgroundResource(R.drawable.circle_background_btn_grey);
                        mBtToogle.setText("OFF");
                        mBtImageView.setBackgroundResource(R.drawable.circle_background_grey);
                        isBtEnabled = false;
                        onBtTurnOff();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onRecieve: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onRecieve: STATE ON");
                        mBtToogle.setBackgroundResource(R.drawable.circle_background_btn_purple);
                        mBtImageView.setBackgroundResource(R.drawable.circle_background_purple);
                        isBtEnabled = true;
                        mBtToogle.setText("ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onRecieve: STATE TURNING ON");
                        break;
                }
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                new DatabaseAsync().execute();
                Toast.makeText(HomeActivity.this, "Device Disconnected", Toast.LENGTH_LONG).show();
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(HomeActivity.this, "Connected device", Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * onCreate arrange and create connection between view and controller
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mBtLinear = findViewById(R.id.home_bt_card);
        mBtToogle = findViewById(R.id.home_bt_toggle);
        mBtImageView = findViewById(R.id.home_bt_image);

        mGpsCard = findViewById(R.id.home_gps_card);
        mGpsBtn = findViewById(R.id.home_gps_btn);

        mHomeToolbar = findViewById(R.id.home_toolbar);

        setSupportActionBar(mHomeToolbar);
        getSupportActionBar().setTitle("iRemember");

        // ------------ WORKING WITH GPS ---------
        mGpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gpsIntent = new Intent(HomeActivity.this, GPSActivity.class);
                startActivity(gpsIntent);
            }
        });

        // ------------ GET BT ADAPTER ---------
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enable :)
            mBtToogle.setBackgroundResource(R.drawable.circle_background_btn_grey);
            mBtImageView.setBackgroundResource(R.drawable.circle_background_grey);
            mBtToogle.setText("OFF");
            isBtEnabled = false;
        } else {
            mBtToogle.setBackgroundResource(R.drawable.circle_background_btn_purple);
            mBtImageView.setBackgroundResource(R.drawable.circle_background_purple);
            mBtToogle.setText("ON");
            isBtEnabled = true;
        }

        mBtLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBtEnabled) {
                    Intent btIntent = new Intent(HomeActivity.this, BluetoothPairing.class);
                    startActivity(btIntent);
                } else {
                    alertDialog();
                }
            }
        });

        mBtToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button click for BT Toggle");
                enableDisableBT();
            }
        });
        // Preferences
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        // Watch for device connection and disconnection
        IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, f1);
        this.registerReceiver(mReceiver, f2);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "location-db").build();

        onStartService();


        if (!sharedPreferences.getBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_HOME, false)) {
            TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.home_bt_card), "This will open Bluetooth Devices")
                    .cancelable(false)
                    .drawShadow(true)
                    .titleTextDimen(R.dimen.title_text_size)
                    .tintTarget(false), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    // .. which evidently starts the sequence we defined earlier
                    showAnother();
                }

                @Override
                public void onOuterCircleClick(TapTargetView view) {
                    super.onOuterCircleClick(view);
                    //Toast.makeText(view.getContext(), "You clicked the outer circle!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                    Log.d("TapTargetViewSample", "You dismissed me :(");
                }
            });
        }

    }

    private void showAnother() {
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.home_bt_toggle), "This will ON / OFF Bluetooth")
                .cancelable(false)
                .dimColor(android.R.color.holo_green_light)
                .outerCircleColor(R.color.colorAccent)
                .targetCircleColor(android.R.color.holo_green_dark)
                .transparentTarget(true)
                .titleTextDimen(R.dimen.title_text_size)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                // .. which evidently starts the sequence we defined earlier
                showGpsTutorial();
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                //Toast.makeText(view.getContext(), "You clicked the outer circle!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                Log.d("TapTargetViewSample", "You dismissed me :(");
            }
        });
    }

    private void showGpsTutorial() {
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.home_gps_btn), "This will show saven Locations")
                .cancelable(false)
                .dimColor(android.R.color.holo_green_light)
                .outerCircleColor(R.color.colorDeepPurple)
                .targetCircleColor(android.R.color.holo_green_dark)
                .transparentTarget(true)
                .titleTextDimen(R.dimen.title_text_size)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                // .. which evidently starts the sequence we defined earlier
                sharedPreferences.edit().putBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_HOME, true).commit();
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                //Toast.makeText(view.getContext(), "You clicked the outer circle!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                Log.d("TapTargetViewSample", "You dismissed me :(");
            }
        });
    }

    private void alertDialog() {

        new AlertDialog.Builder(HomeActivity.this)
                .setIcon(R.drawable.ic_info_black_24dp)
                .setTitle("Please Turn On Bluetooth")
                .setPositiveButton("OK", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(HomeActivity.this, "You Clicked on OK", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "OK button clicked");
                    }
                })
                .show();
    }

    private void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Doesn't have BT capabilities");
            Toast.makeText(this, "No BT device found", Toast.LENGTH_LONG).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, intentFilter);
        }
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();

            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, intentFilter);
        }
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
        try {
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void onBtTurnOff() {
        new DatabaseAsync().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent aboutIntent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.menu_help:
                Intent helpIntent = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(helpIntent);
                break;
        }
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Launching the service
    public void onStartService() {

        if (isMyServiceRunning(BTBackgroundService.class)) {
            Log.d(TAG, "Service already running");
        } else {
            Intent i = new Intent(this, BTBackgroundService.class);
            i.putExtra("from-intent", "HomeActivity");
            startService(i);
        }

    }

    public class DatabaseAsync extends AsyncTask<Void, Void, Void> {
        GpsLocationEntity gpsLocationEntity = new GpsLocationEntity();
        GpsTracker gTracker = new GpsTracker(getApplicationContext());
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = new ArrayList<>(0);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Location loc = gTracker.getLocation();
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            gpsLocationEntity.setLatitude(loc.getLatitude());
            gpsLocationEntity.setLongtitude(loc.getLongitude());
            gpsLocationEntity.setMapAdd(addresses.get(0).getAddressLine(0));
            Date date = new Date();
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy KK:mm");
            gpsLocationEntity.setAddedTime(fmtOut.format(date));
            //Perform pre-adding operation here.

        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.gpsLocationDao().insertOnlySingleRecord(gpsLocationEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //To after addition operation here.
            gpsLocationEntity = new GpsLocationEntity();
        }
    }

}

