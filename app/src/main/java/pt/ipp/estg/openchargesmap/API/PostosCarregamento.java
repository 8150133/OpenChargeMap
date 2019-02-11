package pt.ipp.estg.openchargesmap.API;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Text;

import java.util.List;

import pt.ipp.estg.openchargesmap.R;

public class PostosCarregamento {
    @SerializedName("ID")
    @Expose
    public int ID;

    @SerializedName("AddressInfo")
    @Expose
    public AdressInfo adressInfo;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public AdressInfo getAdressInfo() {
        return adressInfo;
    }

    public void setAdressInfo(AdressInfo adressInfo) {
        this.adressInfo = adressInfo;
    }


}

