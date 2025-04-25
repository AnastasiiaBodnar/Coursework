package com.example.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import java.util.*;
import models.Master;
import models.ScheduleItem;

public class BookingFragment extends Fragment {

    private String serviceName, category;
    private String selectedMasterId;
    private String selectedTimeString;
    private Timestamp selectedTimestamp;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LinearLayout mastersContainer, timeSlotsContainer;
    private Button btnConfirm;
    private TextView tvServiceName;
    private CalendarView calendarView;
    private Calendar selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            serviceName = getArguments().getString("serviceName");
            category = getArguments().getString("category");
        }

        tvServiceName = view.findViewById(R.id.tvServiceName);
        mastersContainer = view.findViewById(R.id.mastersContainer);
        timeSlotsContainer = view.findViewById(R.id.timeSlotsContainer);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        calendarView = view.findViewById(R.id.calendarView);

        tvServiceName.setText("Запис на: " + serviceName);

        selectedDate = Calendar.getInstance();
        selectedDate.add(Calendar.DAY_OF_MONTH, 1);
        calendarView.setDate(selectedDate.getTimeInMillis());

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            timeSlotsContainer.removeAllViews();
            selectedTimeString = null;
            selectedTimestamp = null;
            btnConfirm.setEnabled(false);
            if (selectedMasterId != null) {
                loadTimeSlotsForMaster(selectedMasterId);
            }
        });

        btnConfirm.setOnClickListener(v -> confirmBooking());

        loadMastersByCategory();

        return view;
    }

    private void loadMastersByCategory() {
        db.collection("masters")
                .whereEqualTo("specializations", category)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Master master = doc.toObject(Master.class);
                        master.setId(doc.getId());

                        Button btn = new Button(getContext());
                        btn.setText(master.getName());
                        btn.setOnClickListener(v -> {
                            selectedMasterId = master.getId();
                            loadTimeSlotsForMaster(master.getId());
                        });

                        mastersContainer.addView(btn);
                    }
                });
    }

    private void loadTimeSlotsForMaster(String masterId) {
        timeSlotsContainer.removeAllViews();

        db.collection("masters").document(masterId).get()
                .addOnSuccessListener(doc -> {
                    Master master = doc.toObject(Master.class);
                    master.setId(doc.getId());

                    List<String> availableSlots = generateSlots(master.getSchedule(), selectedDate);

                    db.collection("bookings")
                            .whereEqualTo("masterId", masterId)
                            .get()
                            .addOnSuccessListener(bookings -> {
                                Set<String> busySlots = new HashSet<>();
                                for (DocumentSnapshot b : bookings.getDocuments()) {
                                    Timestamp ts = b.getTimestamp("timestamp");
                                    if (ts != null) {
                                        Calendar booked = Calendar.getInstance();
                                        booked.setTime(ts.toDate());
                                        if (isSameDay(booked, selectedDate)) {
                                            String slot = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(booked.getTime());
                                            busySlots.add(slot);
                                        }
                                    }
                                }

                                for (String slot : availableSlots) {
                                    if (!busySlots.contains(slot)) {
                                        Button btn = new Button(getContext());
                                        btn.setText(slot);
                                        btn.setOnClickListener(v -> {
                                            selectedTimeString = slot;
                                            selectedTimestamp = buildTimestamp(selectedDate, slot);
                                            btnConfirm.setEnabled(true);
                                        });
                                        timeSlotsContainer.addView(btn);
                                    }
                                }
                            });
                });
    }

    private List<String> generateSlots(List<ScheduleItem> schedule, Calendar targetDay) {
        List<String> slots = new ArrayList<>();
        String dayOfWeek = new SimpleDateFormat("EEEE", new Locale("uk")).format(targetDay.getTime()).toLowerCase();

        for (ScheduleItem item : schedule) {
            if (item.getDay().toLowerCase().contains(dayOfWeek)) {
                int startHour = Integer.parseInt(item.getStart().split(":" )[0]);
                int endHour = Integer.parseInt(item.getEnd().split(":" )[0]);

                for (int h = startHour; h < endHour; h++) {
                    slots.add(String.format(Locale.getDefault(), "%02d:00", h));
                }
            }
        }
        return slots;
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private Timestamp buildTimestamp(Calendar day, String time) {
        String[] parts = time.split(":" );
        int hour = Integer.parseInt(parts[0]);

        Calendar result = (Calendar) day.clone();
        result.set(Calendar.HOUR_OF_DAY, hour);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);

        return new Timestamp(result.getTime());
    }

    private void confirmBooking() {
        Map<String, Object> booking = new HashMap<>();
        booking.put("userId", auth.getCurrentUser().getUid());
        booking.put("serviceName", serviceName);
        booking.put("category", category);
        booking.put("masterId", selectedMasterId);
        booking.put("timestamp", selectedTimestamp);
        booking.put("status", "підтверджено");

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(getContext(), "Запис підтверджено!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
