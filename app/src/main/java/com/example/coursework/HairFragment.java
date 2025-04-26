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
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            service.setId(document.getId());
                            addServiceCard(service);
                        }
                    } else {
                        Log.w("HairFragment", "Помилка", task.getException());
                    }
                });
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

        bookButton.setOnClickListener(v -> bookService(service));

        servicesContainer.addView(serviceCard);
    }

    private void bookService(Service service) {
        BookingFragment bookingFragment = new BookingFragment();

        Bundle args = new Bundle();
        args.putString("serviceName", service.getName());
        args.putString("category", service.getCategory());
        bookingFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, bookingFragment)
                .addToBackStack(null)
                .commit();
    }

}
