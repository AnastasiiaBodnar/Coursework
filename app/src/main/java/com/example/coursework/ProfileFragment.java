package com.example.coursework;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adapter.BookingAdapter;
import models.Booking;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView profileName, profilePhone;
    private RecyclerView bookingsRecyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.profileName);
        profilePhone = view.findViewById(R.id.profilePhone);
        bookingsRecyclerView = view.findViewById(R.id.bookingsRecyclerView);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);

        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingsRecyclerView.setAdapter(adapter);

        loadUserProfile();
        loadUserBookings();

        return view;
    }

    private void loadUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String firstName = document.getString("firstName");
                            String lastName  = document.getString("lastName");
                            String phone     = document.getString("phone");
                            profileName.setText(firstName + " " + lastName);
                            profilePhone.setText(phone);
                        } else {
                            Toast.makeText(getContext(),
                                    "Дані користувача не знайдено.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("ProfileFragment",
                                "Помилка отримання даних",
                                task.getException());
                        Toast.makeText(getContext(),
                                "Помилка завантаження профілю",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserBookings() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookingList.clear();
                    adapter.notifyDataSetChanged();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Timestamp ts = doc.getTimestamp("timestamp");
                        if (ts == null) continue;
                        Date dateObj = ts.toDate();
                        String date = new SimpleDateFormat("dd.MM.yyyy", new Locale("uk"))
                                .format(dateObj);
                        String time = new SimpleDateFormat("HH:mm", new Locale("uk"))
                                .format(dateObj);

                        String serviceName = doc.getString("serviceName");
                        String status      = doc.getString("status");
                        String masterId    = doc.getString("masterId");
                        if (masterId == null) masterId = "";

                        db.collection("masters")
                                .document(masterId)
                                .get()
                                .addOnSuccessListener(masterDoc -> {
                                    String masterName = masterDoc.getString("name");
                                    if (masterName == null) masterName = "";

                                    Booking booking = new Booking(
                                            serviceName != null ? serviceName : "",
                                            date,
                                            time,
                                            masterName,
                                            status != null ? status : ""
                                    );
                                    bookingList.add(booking);
                                    adapter.notifyItemInserted(bookingList.size() - 1);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ProfileFragment",
                                            "Помилка завантаження майстра",
                                            e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Не вдалося завантажити записи",
                            Toast.LENGTH_SHORT).show();
                });
    }
}
