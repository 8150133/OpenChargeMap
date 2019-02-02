package pt.ipp.estg.openchargesmap.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pt.ipp.estg.openchargesmap.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)

public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao getUserDao();

}
