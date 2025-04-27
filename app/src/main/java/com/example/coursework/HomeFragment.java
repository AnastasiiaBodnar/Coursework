package com.example.coursework;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.example.coursework.R;

import java.util.ArrayList;
import java.util.List;

import adapter.HomeCarouselAdapter;

public class HomeFragment extends Fragment {

    private ViewPager2 homeCarousel;
    private DotsIndicator homeDots;
    private HomeCarouselAdapter adapter;
    private List<String> urls = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        homeCarousel = view.findViewById(R.id.homeCarousel);
        homeDots     = view.findViewById(R.id.homeDots);


        setupCarousel();

        loadCarouselImages();

        return view;
    }

    private void setupCarousel() {
        adapter = new HomeCarouselAdapter(urls);
        homeCarousel.setAdapter(adapter);
        homeDots.setViewPager2(homeCarousel);
    }

    private void loadCarouselImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("carousel")
                .get()
                .addOnSuccessListener(this::onCarouselLoaded)
                .addOnFailureListener(e ->
                        Log.e("HomeFragment", "Error loading carousel images", e)
                );
    }

    private void onCarouselLoaded(QuerySnapshot qs) {
        urls.clear();
        for (DocumentSnapshot doc : qs.getDocuments()) {
            String imageUrl = doc.getString("url");
            if (imageUrl != null) {
                urls.add(imageUrl);
            }
        }
        adapter.notifyDataSetChanged();
    }

}
