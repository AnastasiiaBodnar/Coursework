package com.example.coursework;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String serviceId = args.getString("serviceId");
            String serviceName = args.getString("serviceName");

        }

        return view;
    }

}