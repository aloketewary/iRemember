package io.aloketewary.iremember;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.aloketewary.iremember.database.AppDatabase;
import io.aloketewary.iremember.model.GpsLocationEntity;


/**
 * Created by AlokeT on 2/6/2018.
 */

public class GpsListAdapter extends ArrayAdapter<GpsLocation> {
    private LayoutInflater mLayoutInflater;
    private ArrayList<GpsLocation> locations;
    private int mViewResourceId;
    Geocoder geocoder;
    List<Address> addresses;

    AppDatabase appDatabase;
    GpsLocationEntity gpsLocationEntity = new GpsLocationEntity();

    private ViewBinderHelper binderHelper;

    public GpsListAdapter(Context context, int tvResourceId, List<GpsLocation> objects) {
        super(context, R.layout.row_list, objects);
        mLayoutInflater = LayoutInflater.from(context);
        binderHelper = new ViewBinderHelper();

        // uncomment if you want to open only one row at a time
        binderHelper.setOpenOnlyOne(true);
        mViewResourceId = tvResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.row_list, parent, false);

            holder = new ViewHolder();
            holder.swipeLayout = convertView.findViewById(R.id.swipe_layout);
            holder.frontView = convertView.findViewById(R.id.front_layout);
            holder.deleteView = convertView.findViewById(R.id.delete_layout);
            holder.textView = convertView.findViewById(R.id.gps_time_view);
            holder.textView2 = convertView.findViewById(R.id.gps_lat_text);
            holder.textView3 = convertView.findViewById(R.id.gps_lon_text);
            holder.textView4 = convertView.findViewById(R.id.map_address);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GpsLocation item = getItem(position);
        if (item != null) {
            binderHelper.bind(holder.swipeLayout, item.getTime());

            holder.textView.setText(item.getTime());
            holder.textView2.setText(item.getLat());
            holder.textView3.setText(item.getLon());
            holder.textView4.setText(item.getMap());
            holder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appDatabase = Room.databaseBuilder(v.getContext(),
                            AppDatabase.class, "location-db").build();
                    gpsLocationEntity.setUid(item.getId());
                    new DatabaseAsync().execute();
                    // Refresh main activity upon close of dialog box
                    ((GPSActivity)getContext()).recreate();
                }
            });
            holder.frontView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   navigateTo(view, item);
                }
            });
        }

        return convertView;
    }

    public void navigateTo(View view, GpsLocation item) {
        // do your code
        String lat = item.getLat();
        String lng = item.getLon();
        // Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
            view.getContext().startActivity(mapIntent);
        }
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    private class ViewHolder {
        SwipeRevealLayout swipeLayout;
        View frontView;
        View deleteView;
        TextView textView, textView2, textView3, textView4;
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
