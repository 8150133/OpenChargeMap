package pt.ipp.estg.openchargesmap;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class ChargementHistoric implements Serializable {

    private String nomePosto;
    private String morada;
    @PrimaryKey
    @NonNull
    private int preco;


    public String getNomePosto() {
        return nomePosto;
    }

    public void setNomePosto(String nomePosto) {
        this.nomePosto = nomePosto;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    @NonNull
    public int getPreco() {
        return preco;
    }

    public void setPreco(@NonNull int preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "ChargementHistoric{" +
                "nomePosto='" + nomePosto + '\'' +
                ", morada='" + morada + '\'' +
                ", preco=" + preco +
                '}';
    }
}
