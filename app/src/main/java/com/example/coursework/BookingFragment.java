package com.example.coursework;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import models.Master;
import models.ScheduleItem;

public class BookingFragment extends Fragment {

    private static final String TAG = "BookingFragment";

    private String serviceName;
    private String category;
    private String selectedMasterId;
    private Timestamp selectedTimestamp;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LinearLayout mastersContainer;
    private LinearLayout timeSlotsContainer;
    private Button btnConfirm;
    private TextView tvServiceName;
    private CalendarView calendarView;
    private Calendar selectedDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            serviceName = getArguments().getString("serviceName");
            category = getArguments().getString("category");
        }

        // Инит вьюшек
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
        Log.d(TAG, "loadMastersByCategory() category=" + category);
        mastersContainer.removeAllViews();

        db.collection("masters")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener((QuerySnapshot query) -> {
                    Log.d(TAG, "Masters query returned: " + query.size());
                    if (query.isEmpty()) {
                        Toast.makeText(getContext(), "Немає майстрів для " + category, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (QueryDocumentSnapshot doc : query) {
                        Master master = doc.toObject(Master.class);
                        master.setId(doc.getId());

                        Button btn = new Button(getContext());
                        btn.setText(master.getName());
                        btn.setOnClickListener(v -> {
                            selectedMasterId = master.getId();
                            timeSlotsContainer.removeAllViews();
                            selectedTimestamp = null;
                            btnConfirm.setEnabled(false);
                            loadTimeSlotsForMaster(master.getId());
                        });

                        mastersContainer.addView(btn);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading masters", e);
                    Toast.makeText(getContext(), "Помилка при завантаженні майстрів", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadTimeSlotsForMaster(String masterId) {
        Log.d(TAG, "loadTimeSlotsForMaster() id=" + masterId);
        timeSlotsContainer.removeAllViews();

        db.collection("masters").document(masterId).get()
                .addOnSuccessListener(doc -> {
                    Master master = doc.toObject(Master.class);
                    if (master == null) return;
                    List<ScheduleItem> schedule = master.getSchedule();
                    if (schedule != null) {
                        List<String> avail = getAvailableSlotsForSelectedDay(schedule, selectedDate);
                        Log.d(TAG, "Available slots count=" + avail.size());
                        removeBusySlotsAndShow(masterId, avail);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading master schedule", e));
    }

    private List<String> getAvailableSlotsForSelectedDay(List<ScheduleItem> schedule, Calendar date) {
        List<String> result = new ArrayList<>();
        String dayName = new SimpleDateFormat("EEEE", new Locale("uk")).format(date.getTime()).toLowerCase();
        for (ScheduleItem ds : schedule) {
            if (ds.getDay().equalsIgnoreCase(dayName)) {
                for (String slot : ds.getSlots()) {
                    if (isFutureSlot(date, slot)) {
                        result.add(slot);
                    }
                }
                break;
            }
        }
        return result;
    }

    private void removeBusySlotsAndShow(String masterId, List<String> slots) {
        db.collection("bookings")
                .whereEqualTo("masterId", masterId)
                .get()
                .addOnSuccessListener(query -> {
                    Set<String> busy = new HashSet<>();
                    for (DocumentSnapshot d : query) {
                        Timestamp ts = d.getTimestamp("timestamp");
                        if (ts != null && isSameDay(ts.toDate(), selectedDate.getTime())) {
                            String t = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(ts.toDate());
                            busy.add(t);
                        }
                    }
                    for (String slot : slots) {
                        if (!busy.contains(slot)) {
                            Button btn = new Button(getContext());
                            btn.setText(slot);
                            btn.setOnClickListener(v -> {
                                selectedTimestamp = buildTimestamp(selectedDate, slot);
                                btnConfirm.setEnabled(true);
                            });
                            timeSlotsContainer.addView(btn);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading busy slots", e));
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isFutureSlot(Calendar date, String slot) {
        Calendar c = (Calendar) date.clone();
        String[] p = slot.split(":");
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(p[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(p[1]));
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis() > System.currentTimeMillis();
    }

    private Timestamp buildTimestamp(Calendar day, String time) {
        String[] p = time.split(":");
        Calendar c = (Calendar) day.clone();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(p[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(p[1]));
        c.set(Calendar.SECOND, 0);
        return new Timestamp(c.getTime());
    }

    private void confirmBooking() {
        if (selectedTimestamp == null || selectedMasterId == null) {
            Toast.makeText(getContext(), "Оберіть майстра й годину", Toast.LENGTH_SHORT).show();
            return;
        }
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
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
