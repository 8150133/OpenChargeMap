package pt.ipp.estg.openchargesmap.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import pt.ipp.estg.openchargesmap.ChargementHistoric;

@Dao
public interface ChargementHistoricDao {
    @Query("SELECT * FROM ChargementHistoric where nomePosto= :nomePosto and morada= :morada and preco= :preco")
    ChargementHistoric getChargementHistoric (String nomePosto, String morada, float preco);

    @Insert
    void insert(ChargementHistoric chargementHistoric);

    @Update
    void update(ChargementHistoric chargementHistoric);

    @Delete
    void delete(ChargementHistoric chargementHistoric);
}
