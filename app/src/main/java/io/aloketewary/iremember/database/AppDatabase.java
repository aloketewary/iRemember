package io.aloketewary.iremember.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import io.aloketewary.iremember.dao.GpsLocationDao;
import io.aloketewary.iremember.model.GpsLocationEntity;

/**
 * Created by AlokeT on 2/7/2018.
 */

@Database(entities = {GpsLocationEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GpsLocationDao gpsLocationDao();
}
