package com.suditouri_ostfalia_android.suditouri.MarkerFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.suditouri_ostfalia_android.suditouri.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private ImageButton mImageButton1,mImageButton2,mImageButton3,mImageButton4,mImageButton5,mImageButton6;


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);

        mImageButton1 = (ImageButton) view.findViewById(R.id.imageButton1);
        mImageButton2 = (ImageButton) view.findViewById(R.id.imageButton2);
        mImageButton3 = (ImageButton) view.findViewById(R.id.imageButton3);

        mImageButton4 = (ImageButton) view.findViewById(R.id.imageButton4);
        mImageButton5 = (ImageButton) view.findViewById(R.id.imageButton5);
        mImageButton6 = (ImageButton) view.findViewById(R.id.imageButton6);

        // Inflate the layout for this fragment
        return view;
    }


}
