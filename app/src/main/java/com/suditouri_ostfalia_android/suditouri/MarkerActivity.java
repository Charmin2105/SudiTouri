package com.suditouri_ostfalia_android.suditouri;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

public class MarkerActivity extends AppCompatActivity {

    ///Kümmert sich um den Inhalt der ausgewählten Seite
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //Variablen werden deklariert
    private ViewPager mViewPager;
    public static String Titel = null;
    public static String PACKAGE_NAME;
    private ImageButton mImageButton;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private boolean mImageIsZoomed;
    private static final String TAG = MarkerActivity.class.getSimpleName() + " => ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

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

        //Den übergebenen Tiitel aus der MapsActivity auslesen
        Intent sender = getIntent();
        Titel = sender.getExtras().getString("titel");

        //Debug Meldung
        Log.d("-->", Titel);

        //Adapter wird erstellt, der ein Fragment für jeden der drei primären Abschnitte der Aktivität zurückgibt.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //ViewPager mit dem Adapter einrichten.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        //Quelle: https://developer.android.com/training/animation/zoom
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mImageIsZoomed = false;
        PACKAGE_NAME = getApplicationContext().getPackageName();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menüpunkte aus der "menu_marker" Datei werden der Toolbar hinzugefügt
        getMenuInflater().inflate(R.menu.menu_marker, menu);
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


    public static class PlaceholderFragment extends Fragment {

        //Welche Seite aufgerufen ist wird hier gespeichert
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        //Gibt eine neue Instance zurück mit der angegebenen sectionNumber
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            //Debug Meldung
            Log.d("-->", "CreateView" + Titel);

            //Abfrage welches Fragment gezeigt werden soll
            //Danach wird das entsprechende Fragment angezeigt
            if (Titel.toString().equalsIgnoreCase("<h3>Start</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_start, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Hardau-Spiel</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_hardau_spiel, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Station 9</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_station9, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Pferdchen-Spiel</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_pferdchen_spiel, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Parkplatz - Übersichtstafel</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_parkplatz, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Station 10</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_station10, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Baum-Spiel</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_baum_spiel, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Station 10</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_laufhuette, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Bach-Spiel</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_bach_spiel, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Wasser-Spiel</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_wasser_spiel, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Station 11</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_station11, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Lauftreffhütte Am Elmensteg</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_laufhuette, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Station 12</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_station12, container, false);
                return rootView;
            } else if (Titel.toString().equalsIgnoreCase("<h3>Station 13</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_station13, container, false);
                return rootView;
            }else if (Titel.toString().equalsIgnoreCase("<h3>Ziel</h3>")) {
                View rootView = inflater.inflate(R.layout.fragment_ziel, container, false);
                return rootView;
            }

            View rootView = inflater.inflate(R.layout.fragment_dummy, container, false);
            return rootView;
        }
    }

    //Gibt das entsprechende Fragment zurück
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //Gibt ein Placeholder Fragment zurück
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // 1 Fragment soll immer geladen werden
            return 1;
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Quelle: https://developer.android.com/training/animation/zoom
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------

    //Bei Klick auf ein Bild wird die Methode aufgerufen
    public void imageOnClick(View v) {

        //Abfrage ob das bild schon Groß angeziegt wird
        //Wenn nicht, Bild wird vergrößern
        if (mImageIsZoomed == false) {
            mImageButton = (ImageButton) v;
            int resID = getResources().getIdentifier(mImageButton.getTag().toString(), "drawable", getPackageName());
            zoomImageFromThumb(mImageButton, resID);
            mImageIsZoomed = true;
        }

    }

    //Bei Klcik auf ein Video
    public void videoOnClick(View v){

        //Intent wird erstellt und das angeklickte Video wird an die VideoActivity übergeben
        Intent intent = new Intent(getBaseContext(), VideoActivity.class);
        intent.putExtra("video",R.raw.rieselvideo_720p);
        startActivity(intent);
    }

    //Methode um ein Bild zu vergrößern
    private void zoomImageFromThumb(final View thumbView, int imageResId) {

        //Animation abbrechen wenn eine Aktiv ist
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        //Großes Bild wird geladen
        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                mImageIsZoomed = false;
                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
