package io.aloketewary.iremember;

/**
 * Created by AlokeT on 2/6/2018.
 */

public class GpsLocation {
    String lat, lon, map, time;
    int id;

    public GpsLocation(String lat, String lon, String map, String time, int id) {
        this.lat = lat;
        this.lon = lon;
        this.map = map;
        this.time = time;
        this.id = id;
    }

    public GpsLocation() {

    }

    public String getMap() {

        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
