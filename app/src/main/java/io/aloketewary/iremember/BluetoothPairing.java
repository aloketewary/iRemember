package io.aloketewary.iremember;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.aloketewary.iremember.database.AppDatabase;
import io.aloketewary.iremember.model.GpsLocationEntity;
import io.aloketewary.iremember.services.GpsTracker;
import io.aloketewary.iremember.util.Constant;

public class BluetoothPairing extends AppCompatActivity {
    private final static String TAG = "BluetoothPairing";
    public ArrayList<BluetoothDevice> mBtDevices = new ArrayList<>(0);
    public ArrayList<BluetoothDevice> mBtPairedDevices = new ArrayList<>(0);
    public DeviceListAdapter mDeviceListAdapter;
    // Preferences
    SharedPreferences sharedPreferences;
    private Toolbar mBtToolbar;
    private Switch mVisibilityBtn;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView itemView, itemPairedView;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "Discoverability Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "onRecieve: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "onRecieve: STATE ON");
                        mVisibilityBtn.setChecked(false);
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "onRecieve: STATE TURNING ON");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "onRecieve: STATE TURNING ON");
                        break;
                }
            }
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBtDevices.add(bluetoothDevice);
                Log.d(TAG, "OnRecieve Device" + bluetoothDevice.getName() + " : " + bluetoothDevice.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBtDevices);
                itemView.setAdapter(mDeviceListAdapter);
            }
            // Pairing bond
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 3 Cases
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {

                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {

                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {

                }
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                new DatabaseAsync().execute();
                Toast.makeText(BluetoothPairing.this, "disConnected device", Toast.LENGTH_LONG).show();
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(BluetoothPairing.this, "Connected device", Toast.LENGTH_LONG).show();
            }
        }
    };
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_pairing);

        mBtToolbar = findViewById(R.id.bt_toolbar);
        setSupportActionBar(mBtToolbar);
        getSupportActionBar().setTitle("Bluetooth Devices");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemView = findViewById(R.id.bt_list_view);
        itemPairedView = findViewById(R.id.bt_paired_list_view);
        itemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                // first cancel discovery because its memory intensive
                mBluetoothAdapter.cancelDiscovery();

                Log.d(TAG, "OnClick: You click on a unpaired device");
                final String deviceName = mBtDevices.get(i).getName();
                new AlertDialog.Builder(BluetoothPairing.this)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setTitle("Do you want to pair with " + deviceName)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(HomeActivity.this, "You Clicked on OK", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "OK button clicked");
                                //Create the bond
                                //Create the bond
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    Log.d(TAG, "Trying to pair with" + deviceName);
                                    mBtDevices.get(i).createBond();
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(HomeActivity.this, "You Clicked on OK", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "unapired cancel button clicked");
                                //Create the bond
                            }
                        })
                        .show();

            }
        });

        itemPairedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                // first cancel discovery because its memory intensive
                mBluetoothAdapter.cancelDiscovery();

                Log.d(TAG, "OnClick: You click a device");
                final String deviceName = mBtPairedDevices.get(i).getName();
                String deviceAddress = mBtPairedDevices.get(i).getAddress();

                new AlertDialog.Builder(BluetoothPairing.this)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        .setTitle("Do you want to unpair with " + deviceName)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(HomeActivity.this, "You Clicked on OK", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "OK button clicked");
                                //Create the bond
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    Log.d(TAG, "Trying to unpair with" + deviceName);
                                    try {
                                        Method m = mBtPairedDevices.get(i).getClass()
                                                .getMethod("removeBond", (Class[]) null);
                                        m.invoke(mBtPairedDevices.get(i), (Object[]) null);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(HomeActivity.this, "You Clicked on OK", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "unapired cancel button clicked");
                                //Create the bond
                            }
                        })
                        .show();

            }
        });

        mVisibilityBtn = findViewById(R.id.bt_visibility_btn);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // ---- ON BOND STATE CHANGED FILTER -----
        IntentFilter pairingFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, pairingFilter);

        mVisibilityBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    toggleVisibility(true);
                } else {
                    // The toggle is disabled
                    toggleVisibility(false);
                }
            }
        });
        IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, f1);
        this.registerReceiver(mReceiver, f2);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "location-db").build();

        searchBTDevices();
        getPairedDevices();
        // Preferences
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPreferences.getBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_BTPAIRED, false)) {
            TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.bt_visibility_btn), "This will active discoverable for your device")
                    .cancelable(false)
                    .outerCircleColor(android.R.color.holo_green_dark)
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
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.bt_paired_device_text), "Here you can see all paired devices.")
                .cancelable(false)
                .outerCircleColor(R.color.colorMaterialYellow)
                .drawShadow(true)
                .titleTextDimen(R.dimen.title_text_size)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                // .. which evidently starts the sequence we defined earlier
                showAnotherForUnpaired();
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

    private void showAnotherForUnpaired() {
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.bt_unpaired_text), "Here you can see all unpaired and discoverable devices.")
                .cancelable(false)
                .outerCircleColor(R.color.colorAccent)
                .drawShadow(true)
                .titleTextDimen(R.dimen.title_text_size)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                // .. which evidently starts the sequence we defined earlier
                sharedPreferences.edit().putBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_BTPAIRED, true).commit();
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

    private void getPairedDevices() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice curDevice : pairedDevices) {
                Log.d(TAG, curDevice.toString());
                mBtPairedDevices.add(curDevice);
            }
            Log.i(TAG, "Paired Number of Devices : " + pairedDevices.size());
            mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mBtPairedDevices);
            itemPairedView.setAdapter(mDeviceListAdapter);
        }
    }

    private void searchBTDevices() {
        Log.d(TAG, "Looking for Unpaired devices");
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            // ------------- CHECK BT PERMISSION IN MANIFEST ----------------
            checkBTPermission();

            mBluetoothAdapter.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, intentFilter);
        }
        if (!mBluetoothAdapter.isDiscovering()) {
            checkBTPermission();
            mBluetoothAdapter.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, intentFilter);
        }
    }


    public void toggleVisibility(Boolean param) {
        if (param) {
            Log.d(TAG, "Making device deiscoverable for 300 seconds");
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mReceiver, intentFilter);
        } else {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
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

    @TargetApi(23)
    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); // any number
            } else {
                Log.d(TAG, "No need to check BT permission > Lollipop");
            }
        }
    }

    public class DatabaseAsync extends AsyncTask<Void, Void, Void> {
        GpsLocationEntity gpsLocationEntity = new GpsLocationEntity();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = new ArrayList<>(0);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GpsTracker gTracker = new GpsTracker(getApplicationContext());
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
            gpsLocationEntity = new GpsLocationEntity();
            //To after addition operation here.
        }
    }
}
