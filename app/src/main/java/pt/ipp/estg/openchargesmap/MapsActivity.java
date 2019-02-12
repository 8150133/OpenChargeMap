package pt.ipp.estg.openchargesmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import android.location.LocationListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ipp.estg.openchargesmap.API.AdressInfo;
import pt.ipp.estg.openchargesmap.API.OpenChargeMapAPI;
import pt.ipp.estg.openchargesmap.API.PostosAdapter;
import pt.ipp.estg.openchargesmap.API.PostosCarregamento;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static final int REQUEST_USER_LOCATION_CODE = 99;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Marker currentLocation;
    private Marker userMarker;
    private Marker postsMarker;
    private ImageButton logout;
    private ImageButton postsRecyclerView;
    private ImageButton addChargementInfo;
    double lati = 0.0;
    double longi = 0.0;
    double latitude = 0.0;
    double longitude = 0.0;
    RecyclerView rvPostos;
    PostosAdapter postosAdapter;
    List<AdressInfo> adressInfo;
    TextView cityName;
    ImageView map;
    private static final String BASE_URL = "https://api.openchargemap.io/v2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation();
    }

    private void addPostMarker(List<PostosCarregamento> pc) {

        for (int i = 0; i < pc.size(); i++) {
            LatLng latLng = new LatLng(pc.get(i).adressInfo.getLatitude(), pc.get(i).adressInfo.getLongitude());


            postsMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng));

            postsMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                return true;
            }
        });

    }

    private void addMarker(double lati, double longi) {
        LatLng coordenadas = new LatLng(lati, longi);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (userMarker != null) userMarker.remove();
        userMarker = mMap.addMarker(new MarkerOptions().position(coordenadas).title("UserLocation"));
        mMap.animateCamera(myLocation);
    }

    private void updateUserPosition(Location location) {
        if (location != null) {
            lati = location.getLatitude();
            longi = location.getLongitude();
            addMarker(lati, longi);
        }
    }

    private void updatePostPosition(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateUserPosition(location);
            updatePostPosition(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void myLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateUserPosition(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListener);
        callAPI();
    }

    public void callAPI() {


        Map<String, String> data = new HashMap<>();
        data.put("output", "json");
        data.put("maxresults", "100");
        data.put("compact", "true");
        data.put("verbose", "false");
        data.put("latitude", Double.toString(lati));
        data.put("longitude", Double.toString(longi));

//        Intent intent = new Intent(this, POI_info.class);
//        String latitude = Double.toString(lat);
//        intent.putExtra("latitude", latitude);
//        String longitude = Double.toString(lng);
//        intent.putExtra("longitude",longitude);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.openchargemap.io/v2/").addConverterFactory(GsonConverterFactory.create()).build();
        OpenChargeMapAPI ocmAPI = retrofit.create(OpenChargeMapAPI.class);
        Call<List<PostosCarregamento>> call = ocmAPI.GetPostos(data);

        call.enqueue(new Callback<List<PostosCarregamento>>() {
            @Override
            public void onResponse(Call<List<PostosCarregamento>> call, Response<List<PostosCarregamento>> response) {
                ArrayList<PostosCarregamento> pc = new ArrayList<>();
                pc = (ArrayList<PostosCarregamento>) response.body();
                Log.d("TAG", "Information Received" + response.body().toString());
                Toast.makeText(MapsActivity.this, "Received!", Toast.LENGTH_SHORT).show();
                addPostMarker(pc);

            }

            @Override
            public void onFailure(Call<List<PostosCarregamento>> call, Throwable t) {
                Log.e("TAG", "Information Not Received" + t.getMessage());
                Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}