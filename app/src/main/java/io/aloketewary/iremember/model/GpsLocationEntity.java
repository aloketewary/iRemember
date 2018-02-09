package io.aloketewary.iremember.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by AlokeT on 2/7/2018.
 */

@Entity(tableName = "gps_locations")
public class GpsLocationEntity {

    public GpsLocationEntity() {
    }

    public GpsLocationEntity(int uid, double latitude, double longtitude, String addedTime, String mapAdd) {
        this.uid = uid;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.addedTime = addedTime;
        this.mapAdd = mapAdd;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "device_lat")
    private double latitude;

    @ColumnInfo(name = "device_lng")
    private double longtitude;

    @ColumnInfo(name = "add_time")
    private String addedTime;

    @ColumnInfo(name = "map_add")
    private String mapAdd;

    public String getMapAdd() {
        return mapAdd;
    }

    public void setMapAdd(String mapAdd) {
        this.mapAdd = mapAdd;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }
}
