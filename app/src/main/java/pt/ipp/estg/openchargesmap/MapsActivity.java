package pt.ipp.estg.openchargesmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    private double lat;
    private double lang;
    RecyclerView rvPostos;
    PostosAdapter postosAdapter;
    List<AdressInfo> adressInfo;
    TextView cityName;
    ImageView map;
    private static final String BASE_URL = "https://api.openchargemap.io/v2/";

    public void CallAPI() {


        Map<String, String> data = new HashMap<>();
        data.put("output", "json");
        data.put("maxresults", "100");
        data.put("compact", "true");
        data.put("verbose", "false");
        data.put("latitude", Double.toString(lat));
        data.put("longitude", Double.toString(lang));


        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        OpenChargeMapAPI ocmAPI = retrofit.create(OpenChargeMapAPI.class);
        Call<List<PostosCarregamento>> call = ocmAPI.GetPostos(data);

        call.enqueue(new Callback<List<PostosCarregamento>>() {
            @Override
            public void onResponse(Call<List<PostosCarregamento>> call, Response<List<PostosCarregamento>> response) {
                ArrayList<PostosCarregamento> postos = new ArrayList<>();
                postos = (ArrayList<PostosCarregamento>) response.body();
                Log.d("TAG", "Nothing Went Wrong" + response.body().toString());
                addPosts(postos);


            }

            @Override
            public void onFailure(Call<List<PostosCarregamento>> call, Throwable t) {
                Log.e("TAG", "Something went wrong" + t.getMessage());
                Toast.makeText(MapsActivity.this, "Erro", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        logout = (ImageButton) findViewById(R.id.logoutIV);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intLogout = new Intent(MapsActivity.this, Login.class);
                MapsActivity.this.startActivity(intLogout);
            }
        });

        postsRecyclerView = (ImageButton) findViewById(R.id.recyclerView);
        postsRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intRecycler = new Intent(MapsActivity.this, PostosActivity.class);
                MapsActivity.this.startActivity(intRecycler);
            }
        });

        addChargementInfo = (ImageButton) findViewById(R.id.addChargementInfo);
        addChargementInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intAdd = new Intent(MapsActivity.this, ChargementHistoric.class);
                MapsActivity.this.startActivity(intAdd);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation();


    }

    private void addUserMarker(double lat, double lang) {
        LatLng latLng = new LatLng(lat, lang);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        if (userMarker != null) {
            userMarker.remove();
        }
        userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("UserLocation").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        mMap.animateCamera(myLocation);
    }

    private void updatePosition(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lang = location.getLongitude();
            addUserMarker(lat, lang);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updatePosition(location);
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
        updatePosition(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, (android.location.LocationListener) locationListener);
        CallAPI();

    }

    private void addPosts(List<PostosCarregamento> pc) {
        for (int i = 0; i < pc.size(); i++) {
            LatLng latLng = new LatLng(pc.get(i).adressInfo.getLatitude(), pc.get(i).adressInfo.getLongitude());

            postsMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                return true;
            }
        });

    }
}