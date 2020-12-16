package com.suditouri_ostfalia_android.suditouri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class HilfeActivity extends AppCompatActivity {
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilfe);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mListView = findViewById(R.id.listview);
        addOnClickListener();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menüpunkte aus der "menu_hilfe" Datei werden der Toolbar hinzugefügt
        getMenuInflater().inflate(R.menu.menu_hilfe, menu);
        return true;
    }

    //Beim drücken eines Menüpinktes wird die Methode aufgerufen
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
                return true;
            //Menüpunkt "Einstellungen" wurde aufegrufen
            case R.id.menu_action_settings:
                startActivity(new Intent(getBaseContext(),EinstellungenActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Welche Activity gestartet werden soll
    private void addOnClickListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (mListView.getItemAtPosition(i).toString()){
                    case "Impressum":
                        startActivity(new Intent(getBaseContext(),ImpressumActivity.class));
                        return;
                    case "Bedienungsanleitung":
                        startActivity(new Intent(getBaseContext(),AnleitungActivity.class));
                        return;
                    case "Über":
                        startActivity(new Intent(getBaseContext(),UeberActivity.class));
                        return;
                }
            }
        });
    }
}
