package com.example.coursework;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import models.Service;

public class HairFragment extends Fragment {
    private FirebaseFirestore db;
    private LinearLayout servicesContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hair, container, false);
        db = FirebaseFirestore.getInstance();
        servicesContainer = view.findViewById(R.id.services);

        loadServices();
        return view;
    }

    private void loadServices() {
        db.collection("services")
                .whereEqualTo("category", "hair")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        servicesContainer.removeAllViews();

                        addCategoryHeader("Hair");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            service.setId(document.getId());
                            addServiceCard(service);
                        }
                    } else {
                        Log.w("HairFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    private void addCategoryHeader(String categoryName) {
        TextView categoryHeader = new TextView(getContext());
        categoryHeader.setText(categoryName);
        categoryHeader.setTextSize(24);
        categoryHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.logo_text));
        categoryHeader.setTypeface(ResourcesCompat.getFont(getContext(), R.font.josefinslab_bold));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 24, 0, 16);
        categoryHeader.setLayoutParams(params);

        servicesContainer.addView(categoryHeader);
    }

    private void addServiceCard(Service service) {
        View serviceCard = LayoutInflater.from(getContext())
                .inflate(R.layout.item_service, servicesContainer, false);

        TextView nameView = serviceCard.findViewById(R.id.service_name);
        TextView priceView = serviceCard.findViewById(R.id.service_price);
        TextView descView = serviceCard.findViewById(R.id.service_description);
        Button bookButton = serviceCard.findViewById(R.id.book_button);

        nameView.setText(service.getName());
        priceView.setText(service.getPrice());

        if (service.getDescription() != null && !service.getDescription().isEmpty()) {
            descView.setText(service.getDescription());
            descView.setVisibility(View.VISIBLE);
        } else {
            descView.setVisibility(View.GONE);
        }

        if (isAdmin()) {
            bookButton.setText("Edit");
            bookButton.setOnClickListener(v -> showEditDialog(service));

            serviceCard.setOnLongClickListener(v -> {
                showDeleteDialog(service);
                return true;
            });
        } else {
            bookButton.setOnClickListener(v -> bookService(service));
        }

        servicesContainer.addView(serviceCard);
    }

    private boolean isAdmin() {
        return false;
    }

    private void showEditDialog(Service service) {
    }

    private void showDeleteDialog(Service service) {
    }

    private void bookService(Service service) {
    }
}