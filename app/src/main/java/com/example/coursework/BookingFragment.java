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
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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

    private Button selectedMasterButton = null;
    private Button selectedTimeButton   = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            serviceName = getArguments().getString("serviceName");
            category    = getArguments().getString("category");
        }

        tvServiceName     = view.findViewById(R.id.tvServiceName);
        mastersContainer  = view.findViewById(R.id.mastersContainer);
        timeSlotsContainer= view.findViewById(R.id.timeSlotsContainer);
        btnConfirm        = view.findViewById(R.id.btnConfirm);
        calendarView      = view.findViewById(R.id.calendarView);

        tvServiceName.setText("Запис на: " + serviceName);

        selectedDate = Calendar.getInstance();
        selectedDate.add(Calendar.DAY_OF_MONTH, 1);
        calendarView.setDate(selectedDate.getTimeInMillis());

        calendarView.setOnDateChangeListener((cv, year, month, day) -> {
            selectedDate.set(year, month, day);
            clearTimeSelection();
            if (selectedMasterId != null) {
                loadTimeSlotsForMaster(selectedMasterId);
            }
        });

        btnConfirm.setOnClickListener(v -> confirmBooking());

        loadMastersByCategory();

        return view;
    }

    private void loadMastersByCategory() {
        mastersContainer.removeAllViews();
        db.collection("masters")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener((QuerySnapshot query) -> {
                    for (QueryDocumentSnapshot doc : query) {
                        Master master = doc.toObject(Master.class);
                        master.setId(doc.getId());

                        Button btn = new Button(getContext());
                        styleDefault(btn);

                        btn.setText(master.getName());
                        btn.setOnClickListener(v -> {
                            if (selectedMasterButton != null) {
                                styleDefault(selectedMasterButton);
                            }
                            selectedMasterButton = btn;
                            styleSelected(btn);

                            selectedMasterId = master.getId();
                            clearTimeSelection();
                            loadTimeSlotsForMaster(master.getId());
                        });

                        mastersContainer.addView(btn);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading masters", e);
                    Toast.makeText(getContext(),
                            "Помилка при завантаженні майстрів",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadTimeSlotsForMaster(String masterId) {
        timeSlotsContainer.removeAllViews();
        db.collection("masters").document(masterId)
                .get()
                .addOnSuccessListener(doc -> {
                    Master master = doc.toObject(Master.class);
                    if (master == null || master.getSchedule() == null) return;
                    List<String> avail = getAvailableSlotsForSelectedDay(
                            master.getSchedule(), selectedDate
                    );
                    removeBusySlotsAndShow(avail);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading schedule", e));
    }

    private List<String> getAvailableSlotsForSelectedDay(
            List<ScheduleItem> schedule, Calendar date
    ) {
        List<String> result = new ArrayList<>();
        String dayName = new SimpleDateFormat("EEEE", new Locale("uk"))
                .format(date.getTime()).toLowerCase();
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

    private void removeBusySlotsAndShow(List<String> slots) {
        db.collection("bookings")
                .whereEqualTo("masterId", selectedMasterId)
                .get()
                .addOnSuccessListener(query -> {
                    Set<String> busy = new HashSet<>();
                    for (DocumentSnapshot d : query) {
                        Timestamp ts = d.getTimestamp("timestamp");
                        if (ts != null && isSameDay(ts.toDate(), selectedDate.getTime())) {
                            busy.add(new SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(ts.toDate()));
                        }
                    }
                    for (String slot : slots) {
                        if (!busy.contains(slot)) {
                            Button btn = new Button(getContext());
                            styleDefault(btn);
                            btn.setText(slot);
                            btn.setOnClickListener(v -> {

                                if (selectedTimeButton != null) {
                                    styleDefault(selectedTimeButton);
                                }
                                selectedTimeButton = btn;
                                styleSelected(btn);

                                selectedTimestamp = buildTimestamp(selectedDate, slot);
                                btnConfirm.setEnabled(true);
                            });
                            timeSlotsContainer.addView(btn);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading busy slots", e));
    }

    private void confirmBooking() {
        if (selectedTimestamp == null || selectedMasterId == null) {
            Toast.makeText(getContext(),
                    "Оберіть майстра й годину",
                    Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(),
                            "Запис підтверджено!",
                            Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Помилка: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void styleDefault(Button btn) {
        btn.setBackgroundResource(R.drawable.rounded_button);
        btn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        btn.setTypeface(ResourcesCompat.getFont(getContext(), R.font.nyght_serif));
        btn.setAllCaps(false);
    }

    private void styleSelected(Button btn) {
        btn.setBackgroundResource(R.drawable.rounded_button_selected);
        btn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        btn.setTypeface(ResourcesCompat.getFont(getContext(), R.font.nyght_serif));
        btn.setAllCaps(false);
    }

    private void clearTimeSelection() {
        if (selectedTimeButton != null) {
            styleDefault(selectedTimeButton);
            selectedTimeButton = null;
        }
        btnConfirm.setEnabled(false);
        selectedTimestamp = null;
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
}
