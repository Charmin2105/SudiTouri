package com.suditouri_ostfalia_android.suditouri.MarkerFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.suditouri_ostfalia_android.suditouri.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZielFragment extends Fragment {
    private ImageView mImageView;


    public ZielFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        // Inflate the layout for this fragment
        return view;
    }

}
