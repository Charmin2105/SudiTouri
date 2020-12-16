package com.suditouri_ostfalia_android.suditouri;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private int mVideoId;

    private VideoView mVideoView;
    private MediaController mMediaController;

    private static final String TAG = VideoActivity.class.getSimpleName() + " => ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        init();
        addEventListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menüpunkte aus der "menu_video" Datei werden der Toolbar hinzugefügt
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menüpunkt der angeklickt wurde, in die Variable id ablegen
        int id = item.getItemId();

        //Abfrage welcher Menüpunkt aufegrufen wurde
        switch (id) {
            //Menüpunkt "Startseite" wurde aufegrufen
            case R.id.menu_action_startseite:
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                return true;
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
    public  void init(){

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

        // Video ID wird aus Inetent gerufen
        Intent sender = getIntent();
        mVideoId = sender.getIntExtra("video",R.raw.rieselvideo_720p);

        // VideoView wird initialisiert
        mVideoView = (VideoView) findViewById(R.id.videoview01);
        mMediaController = new MediaController(this){
            @Override
            public void show(){
                super.show(0);
            }
        };
        mMediaController.setAnchorView(mVideoView);

        mVideoView.setMediaController(mMediaController);
        // Video wird der VideoView zugefügt
        mVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+mVideoId));

        // Video wird gestartet
        mVideoView.start();


    }
    public void addEventListener(){


    }

}
