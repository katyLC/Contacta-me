package com.example.contactame.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contactame.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CitaProcesoFragment extends Fragment {


    public CitaProcesoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cita_proceso, container, false);

        return view;

    }

}
