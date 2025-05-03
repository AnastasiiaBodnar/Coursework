package admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coursework.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import adapter.MastersAdminAdapter;
import models.Master;

public class ChangeMastersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<Master> masterList = new ArrayList<>();
    private MastersAdminAdapter adapter;
    private Button btnAdd;

    private Uri selectedImageUri;
    private ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_masters);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAddMaster);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MastersAdminAdapter(masterList, this);
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> showMasterDialog(null));

        loadMasters();
    }

    private void loadMasters() {
        masterList.clear();
        db.collection("masters").get().addOnSuccessListener(query -> {
            for (var doc : query) {
                Master master = doc.toObject(Master.class);
                master.setId(doc.getId());
                masterList.add(master);
            }
            adapter.notifyDataSetChanged();
        });
    }

    public void deleteMaster(String id) {
        db.collection("masters").document(id).delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Видалено", Toast.LENGTH_SHORT).show();
                    loadMasters();
                });
    }

    public void showMasterDialog(Master master) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(master == null ? "Новий майстер" : "Редагувати майстра");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_master, null);
        EditText etName = view.findViewById(R.id.etName);
        EditText etCategory = view.findViewById(R.id.etCategory);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);

        if (master != null) {
            etName.setText(master.getName());
            etCategory.setText(master.getCategory());
            Glide.with(this).load(master.getPhotoURL()).into(ivPhoto);
        }

        btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        builder.setView(view);
        builder.setPositiveButton("Зберегти", (dialog, which) -> {
            String name = etName.getText().toString();
            String category = etCategory.getText().toString();

            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("category", category);
            data.put("specialization", category);

            if (master == null) {
                List<Map<String, Object>> schedule = new ArrayList<>();
                List<String> fullDay = Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
                List<String> shortDay = Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00");

                schedule.add(createDay("понеділок", fullDay));
                schedule.add(createDay("вівторок", fullDay));
                schedule.add(createDay("середа", fullDay));
                schedule.add(createDay("четвер", fullDay));
                schedule.add(createDay("пʼятниця", fullDay));
                schedule.add(createDay("субота", shortDay));
                schedule.add(createDay("неділя", new ArrayList<>()));

                data.put("schedule", schedule);
            }

            if (selectedImageUri != null) {
                uploadImageAndSave(data, master);
            } else {
                if (master == null) {
                    db.collection("masters").add(data).addOnSuccessListener(doc -> loadMasters());
                } else {
                    db.collection("masters").document(master.getId()).update(data).addOnSuccessListener(unused -> loadMasters());
                }
            }
        });
        builder.setNegativeButton("Скасувати", null);
        builder.show();
    }

    private Map<String, Object> createDay(String day, List<String> slots) {
        Map<String, Object> dayMap = new HashMap<>();
        dayMap.put("day", day);
        dayMap.put("slots", slots);
        return dayMap;
    }

    private void uploadImageAndSave(Map<String, Object> data, Master master) {
        String filename = UUID.randomUUID().toString();
        StorageReference ref = FirebaseStorage.getInstance().getReference("masters/" + filename);

        ref.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    data.put("photoURL", uri.toString());
                    if (master == null) {
                        db.collection("masters").add(data).addOnSuccessListener(doc -> loadMasters());
                    } else {
                        db.collection("masters").document(master.getId()).update(data).addOnSuccessListener(unused -> loadMasters());
                    }
                })
        );
    }

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (ivPhoto != null) {
                        ivPhoto.setImageURI(uri);
                    }
                }
            }
    );
}
