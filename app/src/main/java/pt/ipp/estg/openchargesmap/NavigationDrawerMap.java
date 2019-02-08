package pt.ipp.estg.openchargesmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import pt.ipp.estg.openchargesmap.API.AdressInfo;
import pt.ipp.estg.openchargesmap.API.OpenChargeMapAPI;
import pt.ipp.estg.openchargesmap.API.PostosCarregamento;
import pt.ipp.estg.openchargesmap.API.PostsList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavigationDrawerMap extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "TAG" ;
    private Button signOutButton;
    EditText emailinput;


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer_map);
        Log.d("testes","entrei");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail = (TextView) headerView.findViewById(R.id.emailTextView);


        Intent emailIntent= getIntent();
        String emailReceived = emailIntent.getStringExtra("UserEmail");
        navEmail.setText(emailReceived);
        Log.d("testes",emailReceived);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        /////API/////

        // criar instância retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OpenChargeMapAPI.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // classe que implementa o contrato da interface
        // instância da interface que retorna uma classe
        OpenChargeMapAPI ocmAPI = retrofit.create(OpenChargeMapAPI.class);

        //objeto que chama api
        Call<PostsList> requestPosts = ocmAPI.postsCatalog();

        //Classe anónima Callback que implementa PostosCarregamento de forma assíncrona
        requestPosts.enqueue(new Callback<PostsList>() {
            @Override
            public void onResponse(Call<PostsList> call, Response<PostsList> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG, "Erro: " +response.code());
                } else {
                    //requisição retornada com sucesso
                    //obtém todo o objeto JSON e converte em objeto
                    PostsList postsList = response.body();

                    for(PostosCarregamento pc : postsList.posts){
                        Log.i(TAG, String.format("%d: ",pc.ID));

                        for(AdressInfo ai:pc.AdressInfo){
                            Log.i(TAG, String.format("%s:\n%s:\n%s:\n%f:\n%f: ",ai.getTitle(),ai.getAdressLine1(),ai.getTown(),ai.getLatitude(),ai.getLongitude()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PostsList> call, Throwable t) {
                Log.e(TAG,"Erro: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.userLogOut) {
            Intent intLogout = new Intent(NavigationDrawerMap.this, Login.class);
            NavigationDrawerMap.this.startActivity(intLogout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
