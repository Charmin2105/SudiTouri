package com.suditouri_ostfalia_android.suditouri.MarkerFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suditouri_ostfalia_android.suditouri.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaufhuetteFragment extends Fragment {

    public LaufhuetteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_laufhuette, container, false);
    }

}
