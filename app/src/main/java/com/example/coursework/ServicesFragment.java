package com.example.coursework;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServicesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        view.findViewById(R.id.haircuts_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new HairFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.makeup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new MakeUPFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.manicure_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new NailFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}