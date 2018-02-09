package io.aloketewary.iremember;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;
import java.util.List;

import io.aloketewary.iremember.database.AppDatabase;
import io.aloketewary.iremember.model.GpsLocationEntity;
import io.aloketewary.iremember.util.Constant;

public class GPSActivity extends AppCompatActivity {

    public GpsListAdapter mGpsListAdapter;
    public ArrayList<GpsLocation> mGpsLocations = new ArrayList<>(0);
    double lat, lon;
    AppDatabase appDatabase;
    GpsLocationEntity gpsLocationEntity;
    // Preferences
    SharedPreferences sharedPreferences;
    private Toolbar mGpsToolbar;
    private Button mGpsButton;
    private GpsLocation gpsLocation;
    private ListView mGpsListLoc;
    private boolean isLocationThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1209);

        mGpsToolbar = findViewById(R.id.gps_toolbar);
        setSupportActionBar(mGpsToolbar);
        getSupportActionBar().setTitle("Location History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gpsLocation = new GpsLocation();
        gpsLocationEntity = new GpsLocationEntity();

        mGpsListLoc = findViewById(R.id.gps_list_loc);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "location-db").build();


        mGpsListLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                PopupMenu popup = new PopupMenu(GPSActivity.this, view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigate_to_item:
                                // do your code
                                String lat = mGpsLocations.get(i).getLat();
                                String lng = mGpsLocations.get(i).getLon();
                                // Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng);
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=w");
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                                return true;
                            case R.id.delete_item:
                                // do your code
                                gpsLocationEntity.setUid(mGpsLocations.get(i).getId());
                                new DatabaseAsync().execute();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.popup_menu);
                popup.show();

            }
        });
        // Preferences
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        checkForDbData();

        if (!sharedPreferences.getBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_GPS, false)) {
            TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.gps_location_text), "Here you can see all saved locations")
                    .cancelable(false)
                    .outerCircleColor(R.color.colorMaterialYellow)
                    .drawShadow(true)
                    .titleTextDimen(R.dimen.title_text_size)
                    .tintTarget(false), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    // .. which evidently starts the sequence we defined earlier
                    sharedPreferences.edit().putBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_GPS, true).commit();
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

    private void checkForDbData() {
        LiveData<List<GpsLocationEntity>> locationLiveData = appDatabase.gpsLocationDao().fetchAllData();
        locationLiveData.observe(this, new Observer<List<GpsLocationEntity>>() {
            @Override
            public void onChanged(@Nullable List<GpsLocationEntity> locations) {
                mGpsLocations.clear();
                //Update your UI here.
                for (GpsLocationEntity gpl : locations) {
                    gpsLocation.setLat(String.valueOf(gpl.getLatitude()));
                    gpsLocation.setLon(String.valueOf(gpl.getLongtitude()));
                    gpsLocation.setId(gpl.getUid());
                    gpsLocation.setMap(gpl.getMapAdd());
                    gpsLocation.setTime(gpl.getAddedTime());
                    Log.d("Time > >> >> > >> ", gpl.getAddedTime() + "");
                    mGpsLocations.add(0, gpsLocation);
                    gpsLocation = new GpsLocation();
                    isLocationThere = true;
                }
                mGpsListAdapter = new GpsListAdapter(GPSActivity.this, R.layout.gps_location_results, mGpsLocations);
                mGpsListLoc.setAdapter(mGpsListAdapter);
                showTapTargetData();
            }
        });
    }

    private void showTapTargetData() {
        if (!sharedPreferences.getBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_GPS_IF_LOCATIONS, false) && isLocationThere) {
            TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.gps_list_loc), "Here you can see all saved locations")
                    .cancelable(false)
                    .outerCircleColor(R.color.colorMaterialYellow)
                    .drawShadow(true)
                    .titleTextDimen(R.dimen.title_text_size)
                    .tintTarget(false), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    // .. which evidently starts the sequence we defined earlier
                    sharedPreferences.edit().putBoolean(Constant.COMPLETED_ONBOARDING_PREF_FOR_GPS_IF_LOCATIONS, true).commit();
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

    public class DatabaseAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Perform pre-adding operation here.
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.gpsLocationDao().delete(gpsLocationEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //To after addition operation here.
        }
    }

}
