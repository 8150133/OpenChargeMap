package pt.ipp.estg.openchargesmap.API;

public class AdressInfo {
    private String Title;
    private String AdressLine1;
    private String Town;
    private float Latitude;
    private float Longitude;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAdressLine1() {
        return AdressLine1;
    }

    public void setAdressLine1(String adressLine1) {
        AdressLine1 = adressLine1;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public float getLatitude() {
        return Latitude;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }
}
