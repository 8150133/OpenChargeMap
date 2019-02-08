package pt.ipp.estg.openchargesmap.API;

import java.util.List;

public class PostosCarregamento {
    public int ID;
    public List<AdressInfo> AdressInfo;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<pt.ipp.estg.openchargesmap.API.AdressInfo> getAdressInfo() {
        return AdressInfo;
    }

    public void setAdressInfo(List<pt.ipp.estg.openchargesmap.API.AdressInfo> adressInfo) {
        AdressInfo = adressInfo;
    }
}
