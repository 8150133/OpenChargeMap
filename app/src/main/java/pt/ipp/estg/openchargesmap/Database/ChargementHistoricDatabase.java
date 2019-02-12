package pt.ipp.estg.openchargesmap.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pt.ipp.estg.openchargesmap.ChargementHistoric;


@Database(entities = {ChargementHistoric.class}, version = 1, exportSchema = false)

public abstract class ChargementHistoricDatabase extends RoomDatabase {
    public abstract ChargementHistoricDao getChargementHistoricDao();
}
