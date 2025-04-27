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
import com.google.firebase.firestore.QuerySnapshot;

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
        adapter = new BookingAdapter(getContext(), bookingList);

        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingsRecyclerView.setAdapter(adapter);

        loadUserProfile();
        loadUserBookings();

        return view;
    }

    private void loadUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String firstName = doc.getString("firstName");
                        String lastName  = doc.getString("lastName");
                        String phone     = doc.getString("phone");
                        profileName.setText((firstName != null ? firstName : "") + " "
                                + (lastName  != null ? lastName  : ""));
                        profilePhone.setText(phone != null ? phone : "");
                    } else {
                        Toast.makeText(getContext(),
                                "Дані користувача не знайдено.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment",
                            "Помилка отримання даних користувача", e);
                    Toast.makeText(getContext(),
                            "Помилка завантаження профілю",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserBookings() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener((QuerySnapshot query) -> {
                    bookingList.clear();
                    adapter.notifyDataSetChanged();

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String bookingId = doc.getId();

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

                        db.collection("masters").document(masterId)
                                .get()
                                .addOnSuccessListener(masterDoc -> {
                                    String masterName = masterDoc.getString("name");
                                    if (masterName == null) masterName = "";

                                    Booking booking = new Booking(
                                            bookingId,
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
                                            "Помилка завантаження майстра", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment",
                            "Не вдалося завантажити записи", e);
                    Toast.makeText(getContext(),
                            "Не вдалося завантажити записи",
                            Toast.LENGTH_SHORT).show();
                });
    }
}
