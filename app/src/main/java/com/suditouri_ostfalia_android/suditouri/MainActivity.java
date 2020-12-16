package com.suditouri_ostfalia_android.suditouri;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Variablen deklarieren
    private ImageButton mIb1, mIb2, mIb3, mIb4, mIb5, mIb6, mIb7;
    private View.OnClickListener mIbListener;
    private  Boolean internetstatus;
    private LocationManager mLocationManager;
    private static Context mContext;
    private static final String TAG = MainActivity.class.getSimpleName() + " => ";

    public Activity getActivity() {
        return this;
    }


    //Methode wird beim Start der App aufgerufen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //Methoden aufrufen
        init();
        addEventListener();
        getPersmissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menüpunkte aus der "menu_main" Datei werden der Toolbar hinzugefügt
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Beim drücken eines Menüpunktes wird die Methode aufgerufen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Menüpunkt der angeklickt wurde, in die Variable id ablegen
        int id = item.getItemId();

        //Abfrage welcher Menüpunkt aufegrufen wurde
        switch (id) {
            //Menüpunkt "Hilfe" wurde aufegrufen
            case R.id.menu_action_hilfe:
                startActivity(new Intent(getBaseContext(), HilfeActivity.class));
                return true;
            //Menüpunkt "Einstellungen" wurde aufegrufen
            case R.id.menu_action_settings:
                startActivity(new Intent(getBaseContext(), EinstellungenActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {

        //Toolbar wird gesetzt
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Buttons für KML Karten werden Initialesiert
        mIb1 = (ImageButton) findViewById(R.id.imageButtonKML1);
        mIb2 = (ImageButton) findViewById(R.id.imageButtonKML2);
        mIb3 = (ImageButton) findViewById(R.id.imageButtonKML3);
        mIb4 = (ImageButton) findViewById(R.id.imageButtonKML4);
        mIb5 = (ImageButton) findViewById(R.id.imageButtonKML5);
        mIb6 = (ImageButton) findViewById(R.id.imageButtonKML6);
        mIb7 = (ImageButton) findViewById(R.id.imageButtonKML7);

        mLocationManager = ( LocationManager ) this.getSystemService( LOCATION_SERVICE );

    }

    public void addEventListener() {

        //Die Bilder bekommen eine onClick Funktion
        mIbListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kmlImageButtonEvent(view);
            }
        };

        mIb1.setOnClickListener(mIbListener);
        mIb2.setOnClickListener(mIbListener);
        mIb3.setOnClickListener(mIbListener);
        mIb4.setOnClickListener(mIbListener);
        mIb5.setOnClickListener(mIbListener);
        mIb6.setOnClickListener(mIbListener);
        mIb7.setOnClickListener(mIbListener);

        //FloatingButton wird gedrückt
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.show(fm, "Pfad Dialogbox");
            }
        });
    }

    //Dialog Fragment das angezeigt wird sobald der FloatingButton gedrückt wurde
    //Hier kann der Benutzer eine URL eingeben um eine KML Datei hinzuzufügen
    public static class MyDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_route, null));

            final MainActivity mainActivity = (MainActivity)getActivity();
            builder.setPositiveButton("Route laufen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editText = (EditText) getDialog().findViewById(R.id.dialog_url_eingeben);
                    mainActivity.internetstatus = mainActivity.isNetworkAvailable();
                    if (editText != null) {
                        if(mainActivity.internetstatus){
                            if(mainActivity.mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                                starteActivityMitURL(mContext,editText);
                            }else{
                                Toast.makeText(mainActivity, "Bitte GPS Verbindung herstellen", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(mainActivity, "Bitte Internet Verbindung herstellen", Toast.LENGTH_LONG).show();
                        }
                    } else {
                       // Nicht gefunden
                    }
                }
            });

            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            return builder.create();
        }
    }

    public void kmlImageButtonEvent(View view) {

        //Neues Intent erstellen
        Intent intent = new Intent(getBaseContext(), RoutenActivity.class);

        //Abfrage welcher Pfad ausgewählt wurde
        //Die KML, longitude, latitude und zoom in das Intent speichern
        switch (view.getId()) {
            case R.id.imageButtonKML1:
                intent.putExtra("kml", R.raw.wep1gpx);
                intent.putExtra("longitude", 10.4786296);
                intent.putExtra("latitude", 52.9228125);
                intent.putExtra("zoom", 10);
                break;
            case R.id.imageButtonKML2:
                intent.putExtra("kml", R.raw.wep2gpx);
                intent.putExtra("longitude", 10.4323669);
                intent.putExtra("latitude", 52.875568);
                intent.putExtra("zoom", 11);
                break;
            case R.id.imageButtonKML3:
                intent.putExtra("kml", R.raw.wep3gpx);
                intent.putExtra("longitude", 10.4653259);
                intent.putExtra("latitude", 52.901643);
                intent.putExtra("zoom", 13);
                break;
            case R.id.imageButtonKML4:
                intent.putExtra("kml", R.raw.wep4gpx);
                intent.putExtra("longitude", 10.5025316);
                intent.putExtra("latitude", 52.9181195);
                intent.putExtra("zoom", 12);
                break;
            case R.id.imageButtonKML5:
                intent.putExtra("kml", R.raw.wep5gpx);
                intent.putExtra("longitude", 10.5409837);
                intent.putExtra("latitude", 52.9453549);
                intent.putExtra("zoom", 11);
                break;
            case R.id.imageButtonKML6:
                intent.putExtra("kml", R.raw.wep6gpx);
                intent.putExtra("longitude", 10.4128388);
                intent.putExtra("latitude", 52.8536161);
                intent.putExtra("zoom", 13);
                break;
            case R.id.imageButtonKML7:
                intent.putExtra("kml", R.raw.wep7gpx);
                intent.putExtra("longitude", 10.4415062);
                intent.putExtra("latitude", 52.889071);
                intent.putExtra("zoom", 13);
                break;
        }
        //Die Activity "RoutenActivity" starten
        startActivity(intent);
    }

    public void getPersmissions() {
        //GPS Permission abfragen
        if ((ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //Momentan nicht verwendet android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    //Intent mit der eingegebenen url an die "MapsDynamischActivity" Activity übergeben
    private static void starteActivityMitURL(Context context,EditText editText){
        Intent intent = new Intent(context, MapsDynamischActivity.class);
        intent.putExtra("url", editText.getText().toString());
        Log.d(TAG,editText.getText().toString());
        context.startActivity(intent);
    }

    //Abfrage ob Internet Verügbar ist
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
