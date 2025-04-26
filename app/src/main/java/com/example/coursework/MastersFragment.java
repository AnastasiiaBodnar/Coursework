package com.example.coursework;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;
import java.util.List;

import adapter.MastersAdapter;
import models.Master;

public class MastersFragment extends Fragment {

    private ViewPager2 viewPager;
    private DotsIndicator dotsIndicator;
    private MastersAdapter mastersAdapter;
    private List<Master> mastersList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_masters, container, false);

        viewPager = view.findViewById(R.id.viewPager);

        setupViewPager();
        loadMastersFromFirebase();

        return view;
    }

    private void setupViewPager() {
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);

        mastersAdapter = new MastersAdapter(mastersList, getContext());
        viewPager.setAdapter(mastersAdapter);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager.setPageTransformer(compositePageTransformer);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }


    private void loadMastersFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("masters")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mastersList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Log.d("Firestore", "Document data: " + document.getData());
                                Master master = document.toObject(Master.class);
                                master.setId(document.getId());
                                mastersList.add(master);
                            } catch (Exception e) {
                                Log.e("Firestore", "Помилка документа " + document.getId(), e);
                                Log.e("Firestore", "Дані документа: " + document.getData());
                            }
                        }
                        if (mastersAdapter != null) {
                            mastersAdapter.notifyDataSetChanged();
                        }
                        if (dotsIndicator != null && viewPager != null) {
                            dotsIndicator.setViewPager2(viewPager);
                        }
                    } else {
                        Log.e("Firestore", "Помилка завантаження даних", task.getException());
                    }
                });
    }
}