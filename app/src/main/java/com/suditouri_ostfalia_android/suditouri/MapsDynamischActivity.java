package com.suditouri_ostfalia_android.suditouri;
// ROUTE 7   http://navigator.geolife.de/export/tour-901000026-8000.kml
// ROUTE 1   http://navigator.geolife.de/export/tour-901000021-8000.kml

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlGeometry;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MapsDynamischActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Variablen werden deklariert
    private GoogleMap mMap;
    private String mKmlurl;//= "http://navigator.geolife.de/export/tour-901000021-8000.kml";
    private LatLng              mKartenUebersichtLatlng;
    private int                 mKartenUebersichtZoom;
    private double              mKartenUebersichtLat;
    private double              mKartenUebersichtLng;
    private LatLng              mLetzteUserLatLng;
    private float               mLetzterUserZoom;
    private LocationManager     mLocationManager;
    private LocationListener    mLocationListener;
    private int                 mKartentyp;
    private int                 mMinDistanceChangeForUpdates;
    private int                 mMinTimeBwUpdates;
    private SharedPreferences   mSharedPreferences;
    private Menu menu;
    private boolean             mActivityCrash;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int    DEFAULT_MIN_DISTANCE_CHANGE_FOR_UPDATES =          0;  // 5 m
    private static final int    DEFAULT_MIN_TIME_BW_UPDATES             =   1000 * 3;  // 1 sec
    private static final float  DEFAULT_ZOOM = 16;


    private static final String TAG = MapsDynamischActivity.class.getSimpleName() + " => ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_dynamisch);

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

        //GPS Intervall und Bewegung wird aus der Einstellung genommen
        try{
            mMinTimeBwUpdates = 1000 * Integer.parseInt(mSharedPreferences.getString("key_gps_min_intervall",""));
            mMinDistanceChangeForUpdates = Integer.parseInt(mSharedPreferences.getString("key_gps_min_bewegung",""));
            mKartentyp = Integer.parseInt(mSharedPreferences.getString("key_map_type",""));
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
        mLocationManager.removeUpdates(mLocationListener);
        Log.d(TAG,"onDestroy()");
        if(mActivityCrash){
            Toast.makeText(MapsDynamischActivity.this, "Keine Valide URL gefunden", Toast.LENGTH_SHORT).show();
        }
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
                Toast.makeText(MapsDynamischActivity.this, "Kamera Zentrierung ausgeschaltet", Toast.LENGTH_LONG).show();
                return true;
            //Kamera folgt nicht mehr dem Benutzer
            case R.id.menu_action_folgen_aus:
                editor.putBoolean("key_gps_postion_folgen",true);
                editor.apply();
                setFollowIcon(true);
                Toast.makeText(MapsDynamischActivity.this, "Kamera Zentrierung eingeschaltet", Toast.LENGTH_LONG).show();
                return true;
            //Gesamtansicht der Route
            case R.id.menu_action_mapzoom_out:
                editor.putBoolean("key_gps_postion_folgen",false);
                editor.apply();
                setFollowIcon(false);
                if(mLetzteUserLatLng != null) {
                    setZoomInOutIcon(true);
                }
                Toast.makeText(MapsDynamischActivity.this, "Karten Übersicht", Toast.LENGTH_LONG).show();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //GPS Permission abfrage
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap = googleMap;

            try {
                //Kml laden
                retrieveFileFromUrl();

            } catch (Exception e) {
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
    }

    private void init(){

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

        // Intent Informationen
        Intent sender=getIntent();
        mKmlurl = sender.getExtras().getString("url");


        mKartenUebersichtZoom = 12 ;
        // Location Manager
        mLocationManager = ( LocationManager ) this.getSystemService( LOCATION_SERVICE );

        // SharedPrefences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mActivityCrash = false;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void addEventListener() {

        mLocationListener = new LocationListener() {
            @Override
            //Wenn GPS Folgen aktiviert ist, wird die Methode updateDeviceLocation() aufgerufen
            public void onLocationChanged(Location location) {
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
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), mMap.getCameraPosition().zoom);
                        mLetzteUserLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        mLetzterUserZoom = mMap.getCameraPosition().zoom;
                    } else {
                        Toast.makeText(MapsDynamischActivity.this, "unable to get current location", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (SecurityException e) {
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {

        //Position und Zoom der Kamera ändern
        Log.d(TAG, "moveCamera() " + "LAT: " + latLng.latitude + " LNG: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

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





    //Die KML Datei herunterladen
    // Source: https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/com/google/maps/android/utils/demo/KmlDemoActivity.java

    private void retrieveFileFromUrl() {
        new DownloadKmlFile(mKmlurl).execute();
    }

    private class DownloadKmlFile extends AsyncTask<String, Void, byte[]> {
        private final String mUrl;

        public DownloadKmlFile(String url) {
            mUrl = url;
        }

        protected byte[] doInBackground(String... params) {
            try {
                InputStream is =  new URL(mUrl).openStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException e) {
                mActivityCrash = true;
                e.printStackTrace();
                finish();
            }
            return new byte[]{};
        }

        //Die Kml der Google Map hinzufügen
        protected void onPostExecute(byte[] byteArr) {
            try {
                KmlLayer kmlLayer = new KmlLayer(mMap, new ByteArrayInputStream(byteArr),
                        getApplicationContext());
                kmlLayer.addLayerToMap();
                // Placemarks werden, falls vorhanden, gesucht und einer wird als Zentrierungspunkt angelegt
                try{
                    Log.d(TAG,"PLACEMARKS");
                    boolean punktGefunden = false;
                    for(KmlContainer container : kmlLayer.getContainers()){
                        for(KmlContainer nestedContainer : container.getContainers()){
                            try{
                                for(KmlPlacemark placemark : nestedContainer.getPlacemarks()){

                                    KmlPoint point = (KmlPoint) placemark.getGeometry();

                                    LatLng latLng = new LatLng(point.getGeometryObject().latitude,point.getGeometryObject().longitude);
                                    Log.d(TAG,"latitude " + point.getGeometryObject().latitude);
                                    Log.d(TAG,"longitude " + point.getGeometryObject().longitude);

                                    punktGefunden = true;
                                    mKartenUebersichtLatlng = latLng;
                                    updateDeviceLocation();
                                    break;
                                }
                            }catch (Exception e){
                                if(punktGefunden){
                                    Log.d(TAG,"Punkt Gefunden" + Boolean.toString(punktGefunden));
                                }else{
                                    // Keine Placemarks gefunden, Optionen werden beschränkt
                                    Log.d(TAG,"Fehler beim Suchen von Punkten " + Boolean.toString(punktGefunden));
                                    menu.getItem(2).setVisible(false);
                                    menu.getItem(3).setVisible(false);
                                    Toast.makeText(MapsDynamischActivity.this,"Fehler beim suchen von Zentrierungs Punkten. Karten Zentrierung wurde deaktiviert", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                }
            } catch (XmlPullParserException e) {
                Toast.makeText(MapsDynamischActivity.this, "Fehler beim Laden der Karte", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(MapsDynamischActivity.this, "Fehler beim Laden der Karte", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}
