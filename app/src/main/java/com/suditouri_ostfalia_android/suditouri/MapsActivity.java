package com.suditouri_ostfalia_android.suditouri;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener,OnMapReadyCallback {

    //Variablen werden deklariert
    private GoogleMap           mMap;
    private int                 kml;
    private LatLng              mKartenUebersichtLatlng;
    private int                 mKartenUebersichtZoom;
    private double              mKartenUebersichtLat;
    private double              mKartenUebersichtLng;
    private LatLng              mLetzteUserLatLng;
    private float               mLetzterUserZoom;
    private LocationManager     mLocationManager;
    private LocationListener    mLocationListener;
    private Location            mLocation;
    private int                 mKartentyp;
    private boolean             mCheckSudiPunktDistanz;
    private int mMinDistanceChangeForUpdates;
    private int mMinTimeBwUpdates;
    private SharedPreferences mSharedPreferences;
    private Menu menu;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int    DEFAULT_MIN_DISTANCE_CHANGE_FOR_UPDATES =          0;  // 5 m
    private static final int    DEFAULT_MIN_TIME_BW_UPDATES             =   1000 * 3;  // 1 sec
    private static final float  DEFAULT_ZOOM = 16;
    private static final String TAG = MapsActivity.class.getSimpleName() + " => ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Methoden aufrufen
        init();
        addEventListener();

        //Abfrage ob GPS Folgen aktiviert ist
        if(mSharedPreferences.getBoolean("key_gps_postion_folgen",true)){
            updateDeviceLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //GPS Intervall und Bewegung wird aus der Einstellung genommen.
        try{
            mMinTimeBwUpdates = 1000 * Integer.parseInt(mSharedPreferences.getString("key_gps_min_intervall",""));
            mMinDistanceChangeForUpdates = Integer.parseInt(mSharedPreferences.getString("key_gps_min_bewegung",""));
            mKartentyp = Integer.parseInt(mSharedPreferences.getString("key_map_type",""));
            mCheckSudiPunktDistanz = mSharedPreferences.getBoolean("key_sudipunkt_distanz",true);
            mMap.setMapType(mKartentyp);
        }catch (Exception e){

        }
        Log.d(TAG,"Distanz "+Integer.toString(mMinDistanceChangeForUpdates));
        Log.d(TAG,"Intervall "+Integer.toString(mMinTimeBwUpdates));
        getLocation();

        try{
            Boolean statusFolgen = mSharedPreferences.getBoolean("key_gps_postion_folgen",true);
            setFollowIcon(statusFolgen);
        }catch (Exception e){

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // Updates werden beim Beenden der Activity nicht weiter angefordert
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menüpunkte aus der "menu_maps" Datei werden der Toolbar hinzugefügt
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_maps, menu);

        Boolean statusFolgen = mSharedPreferences.getBoolean("key_gps_postion_folgen",true);
        setFollowIcon(statusFolgen);

        return true;
    }

    //Beim drücken eines Menüpinktes wird die Methode aufgerufen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Menüpunkt der angeklickt wurde, in die Variable id ablegen
        int id = item.getItemId();
        SharedPreferences.Editor editor = mSharedPreferences.edit();

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
            //Kamera folgt dem Benutzer
            case R.id.menu_action_folgen_an:
                editor.putBoolean("key_gps_postion_folgen",false);
                editor.apply();
                setFollowIcon(false);
                Toast.makeText(MapsActivity.this, "Kamera Zentrierung ausgeschaltet", Toast.LENGTH_LONG).show();
                return true;
            //Kamera folgt nicht mehr dem Benutzer
            case R.id.menu_action_folgen_aus:
                editor.putBoolean("key_gps_postion_folgen",true);
                editor.apply();
                setFollowIcon(true);
                Toast.makeText(MapsActivity.this, "Kamera Zentrierung eingeschaltet", Toast.LENGTH_LONG).show();
                return true;
            //Gesamtansicht der Route
            case R.id.menu_action_mapzoom_out:
                editor.putBoolean("key_gps_postion_folgen",false);
                editor.apply();
                setFollowIcon(false);
                if(mLetzteUserLatLng != null) {
                    setZoomInOutIcon(true);
                }
                Toast.makeText(MapsActivity.this, "Karten Übersicht", Toast.LENGTH_LONG).show();
                moveCamera(mKartenUebersichtLatlng,mKartenUebersichtZoom);
                return true;
            //Von der Gesamtansicht der Route zurück aud den Benutzer
            case R.id.menu_action_mapzoom_in:
                setZoomInOutIcon(false);
                moveCamera(mLetzteUserLatLng,mLetzterUserZoom);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // OnClick Methode für Infofenster der Marker
    @Override
    public void onInfoWindowClick(Marker marker) {

        //Neue Location mit der Marker Position erstellen
        Location markerLocation = new Location("");
        LatLng markerLatLng = marker.getPosition();
        markerLocation.setLatitude(markerLatLng.latitude);
        markerLocation.setLongitude(markerLatLng.longitude);

        //Abfrage ob Distanz abfrage in den Einstellungen aktiviert ist
        if(mCheckSudiPunktDistanz){

            //Abfrage ob der Benutzer maximal 35 Meter vom Punkt entfernt steht
            //Wenn Ja: Beschreibung und Bilder werden in einem neuen Fragment angezeigt
            //Wenn Nein: Meldung das der Benutzer zu weit entfernt ist
            if(mLocation.distanceTo(markerLocation) <= (float)35){
                String titel = marker.getTitle();

                //Neues Intent erstellen und den Titel an die MarkerActivity übergeben
                Intent intent = new Intent(getBaseContext(), MarkerActivity.class);
                intent.putExtra("titel",titel);
                startActivity(intent);
            }else{
                int entfernung = (int ) mLocation.distanceTo(markerLocation);
                entfernung -= 35;
                Toast.makeText(MapsActivity.this, "Bitte "+ entfernung + " Meter näher an den Punkt wandern "  , Toast.LENGTH_LONG).show();
            }
        }else {
            String titel = marker.getTitle();

            //Neues Intent erstellen und den Titel an die MarkerActivity übergeben
            Intent intent = new Intent(getBaseContext(), MarkerActivity.class);
            intent.putExtra("titel",titel);
            startActivity(intent);
        }


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //GPS Permission abfrage
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap = googleMap;
            mMap.setOnInfoWindowClickListener(this);

            try {
                //Kml in die google map laden
                KmlLayer layer = new KmlLayer(mMap, kml, getApplicationContext());
                layer.addLayerToMap();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mKartenUebersichtLatlng, DEFAULT_ZOOM));

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Das UI von der Map verändern.
            //Zoom und Position finden aktivieren, Map Type setzen.
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setMapType(mKartentyp);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        updateDeviceLocation();
    }

    public void init(){
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Das übergebene Intent aus der RoutenActivity auslesen
        Intent sender = getIntent();
        kml = sender.getExtras().getInt("kml");
        mKartenUebersichtLat = sender.getExtras().getDouble("latitude");
        mKartenUebersichtLng = sender.getExtras().getDouble("longitude");
        mKartenUebersichtLatlng = new LatLng(mKartenUebersichtLat, mKartenUebersichtLng);
        mKartenUebersichtZoom = sender.getExtras().getInt("zoom");

        // Location Manager
        mLocationManager = ( LocationManager ) this.getSystemService( LOCATION_SERVICE );

        // SharedPrefences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Map Fragment Setup
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void addEventListener() {

        // Listener für onLocation Changed.
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Wenn GPS Folgen aktiviert ist, wird die Methode updateDeviceLocation() aufgerufen
                if(mSharedPreferences.getBoolean("key_gps_postion_folgen",true)){
                    updateDeviceLocation();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    //Position wird aktualesiert
    public void updateDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            // Die Letzte Position abfragen und in Variablen speichern
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        mLocation = currentLocation;
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), mMap.getCameraPosition().zoom);
                        mLetzteUserLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        mLetzterUserZoom = mMap.getCameraPosition().zoom;
                    } else {
                        Toast.makeText(MapsActivity.this, "Aktuelle Position kann nicht gefunden werden", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (SecurityException e) {
        }
    }

    // Methode um die Position der Camera zu verändern
    private void moveCamera(LatLng latLng, float zoom) {

        Log.d(TAG, "moveCamera() " + "LAT: " + latLng.latitude + " LNG: " + latLng.longitude);

        //Position und Zoom der Kamera ändern
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    // Methode um den LocationManager zu zu deklarieren.
    private void getLocation( ) {
        // Überprüft die Berechtigung
        if( ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }

        // Registriert den Listener mit dem Standort Manager um Ortsaktualisierungen zu erhalten
        if( mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    mMinTimeBwUpdates,
                    mMinDistanceChangeForUpdates,
                    mLocationListener);
    }

    //GPS Icon in der toolbar tauschen
    private void  setFollowIcon(Boolean follow){
        if(follow){
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }else{
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }
    }

    //Zoom Icon in der toolbar tauschen
    private void setZoomInOutIcon(Boolean zoomIcon){
        if(zoomIcon){
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
        }else{
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
        }
    }

}