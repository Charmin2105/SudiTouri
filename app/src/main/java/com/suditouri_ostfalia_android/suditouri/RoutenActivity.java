package com.suditouri_ostfalia_android.suditouri;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class RoutenActivity extends AppCompatActivity implements OnMapReadyCallback{

    //Variablen werden deklariert
    private GoogleMap   mMap;
    private String      mMapName;
    private String      mMapDesc;
    private int         mKml;
    private LatLng      mLatlng;
    private double      mLat;
    private double      mLng;
    private int         mZoom;
    private FloatingActionButton mFab;
    LocationManager mManager;
    private static final String TAG = RoutenActivity.class.getSimpleName() + " => ";

    //Methode wird beim Starten der App aufgerufen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routen);

        //Methode init() aufrufen
        init();
        //Methode addEventListener() aufrufen
        addEventListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menüpunkte aus der "menu_main" Datei werden der Toolbar hinzugefügt
        getMenuInflater().inflate(R.menu.menu_route_eins, menu);
        return true;
    }

    //Beim drücken eines Menüpunktes wird die Methode aufgerufen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menüpunkt der angeklickt wurde, in die Variable id ablegen
        int id = item.getItemId();

        //Abfrage welcher Menüpunkt aufegrufen wurde
        switch (id)
        {
            //Menüpunkt "Startseite" wurde aufegrufen
            case R.id.menu_action_startseite:
                startActivity(new Intent(getBaseContext(),MainActivity.class));
                return true;
            //Menüpunkt "Hilfe" wurde aufegrufen
            case R.id.menu_action_hilfe:
                startActivity(new Intent(getBaseContext(),HilfeActivity.class));
                return true;
            //Menüpunkt "Einstellungen" wurde aufegrufen
            case R.id.menu_action_settings:
                startActivity(new Intent(getBaseContext(),EinstellungenActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //GPS Permission Abfrage
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap = googleMap;


            try {
                //Kml wird zur Map hinzugefügt
                KmlLayer layer = new KmlLayer(mMap, mKml, getApplicationContext());
                layer.addLayerToMap();

                //Kamera wird verschoben
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatlng, mZoom));

                //Die Methode GetKmlData() aufrufen
                //@layer: KML Datei wird übergeben
                GetKmlData(layer);
                UpdateTextViews();

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Kompass und Zoom Button auf der Karte anzeigen
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(Marker marker) {
                   return true;
                }});

        } else {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void init(){

        // Toolbar setzen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // FloatingActionButton
        mFab = findViewById(R.id.fab_explore);

        // Intent Informationen auslesen und in Variablen speicchern
        Intent sender=getIntent();
        mKml = sender.getExtras().getInt("kml");
        mLat = sender.getExtras().getDouble("latitude");
        mLng = sender.getExtras().getDouble("longitude");
        mLatlng = new LatLng(mLat,mLng);
        mZoom = sender.getExtras().getInt("zoom");

        // Map Fragment Setup
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapz);
        mapFragment.getMapAsync(this);
    }

    public void addEventListener(){

        //FloatingButton registrieren
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Die Methode fabEvent() aufrufen
                fabEvent();
            }
        });

    }

    public void fabEvent(){

        mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );

        //Abfragen ob GPS aktiviert ist
        boolean statusOfGPS = mManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(statusOfGPS){

            //Neues Intent erstellen
            Intent intent = new Intent(getBaseContext(), MapsActivity.class);

            //KML, latitude, longitude und zooom in das Intent speichern
            intent.putExtra("kml",mKml);
            intent.putExtra("latitude",mLat);
            intent.putExtra("longitude",mLng);
            intent.putExtra("zoom",mZoom);

            //Die Activity "MapsActivity" starten
            startActivity(intent);
        }else {
            //Meldung an Benutzer wenn GPS ausgeschaltet
            Toast.makeText(RoutenActivity.this,"Bitte GPS einschalten", Toast.LENGTH_SHORT).show();
        }
    }

    public void GetKmlData(KmlLayer kmlLayer){

        //Nsme und Descrition aus der KML lesen
        for(KmlContainer container : kmlLayer.getContainers()){
            if(container.hasProperty("name")){
                mMapName = container.getProperty("name");
            }
            if(container.hasProperty("description")){
                mMapDesc = container.getProperty("description");
            }
        }
    }

    public void UpdateTextViews(){

        //Den Namen und die Beschreibung aus der KML Datei in die TextView's setzen.
        TextView textView;
        textView  = ( TextView ) findViewById( R.id.textView2 );
        textView.setText(Html.fromHtml(mMapDesc));
        textView  = ( TextView ) findViewById( R.id.textView );
        textView.setText(Html.fromHtml(mMapName));
    }
}
