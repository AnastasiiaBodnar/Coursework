package admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        AdminActivity.UpdateSchedule.updateMasterSchedule();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Toast.makeText(this, "Ви увійшли як адміністратор", Toast.LENGTH_SHORT).show();
        }

    }

    public static class UpdateSchedule {
        public static void updateMasterSchedule() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String masterId = "uRs2tNIwqr1pRfisizBx";


            List<Map<String, Object>> schedule = new ArrayList<>();

            schedule.add(createDaySchedule("понеділок", Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")));
            schedule.add(createDaySchedule("вівторок", Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")));
            schedule.add(createDaySchedule("середа", Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")));
            schedule.add(createDaySchedule("четвер", Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")));
            schedule.add(createDaySchedule("п'ятниця", Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")));
            schedule.add(createDaySchedule("субота", Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00")));
            schedule.add(createDaySchedule("неділя", new ArrayList<>()));

            Map<String, Object> updates = new HashMap<>();
            updates.put("schedule", schedule);

            db.collection("masters").document(masterId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println(" Старий schedule замінено новим успішно!");
                    })
                    .addOnFailureListener(e -> {
                        System.err.println(" Помилка оновлення schedule: " + e.getMessage());
                    });
        }

        private static Map<String, Object> createDaySchedule(String day, List<String> slots) {
            Map<String, Object> daySchedule = new HashMap<>();
            daySchedule.put("day", day);
            daySchedule.put("slots", slots);
            return daySchedule;
        }
    }
}