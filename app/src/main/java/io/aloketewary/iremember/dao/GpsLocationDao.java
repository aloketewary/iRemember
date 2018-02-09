package io.aloketewary.iremember.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.aloketewary.iremember.model.GpsLocationEntity;

/**
 * Created by AlokeT on 2/7/2018.
 */
@Dao
public interface GpsLocationDao {

    @Query("SELECT * FROM gps_locations")
    List<GpsLocationEntity> getAll();

    @Query("SELECT * FROM gps_locations WHERE uid IN (:userIds)")
    List<GpsLocationEntity> loadAllByIds(int[] userIds);

//    @Query("SELECT * FROM gps_location_entity WHERE first_name LIKE :first AND "
//            + "last_name LIKE :last LIMIT 1")
//    GpsLocationEntity findByName(String first, String last);

    @Insert
    void insertOnlySingleRecord(GpsLocationEntity locationEntity);

    @Query("SELECT * FROM gps_locations")
    LiveData<List<GpsLocationEntity>> fetchAllData();

    @Insert
    void insertAll(GpsLocationEntity... locations);

    @Delete
    void delete(GpsLocationEntity locations);
}
