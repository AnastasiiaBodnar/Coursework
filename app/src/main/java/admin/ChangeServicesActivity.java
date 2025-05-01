package admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import adapter.ServicesAdapter;
import models.Service;

public class ChangeServicesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ServicesAdapter adapter;
    private List<Service> serviceList = new ArrayList<>();
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_services);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.servicesRecyclerView);
        btnAdd = findViewById(R.id.btnAddService);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServicesAdapter(serviceList, this);
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> showServiceDialog(null));

        loadServices();
    }

    private void loadServices() {
        serviceList.clear();
        db.collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Service s = doc.toObject(Service.class);
                        s.setId(doc.getId());
                        serviceList.add(s);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    public void deleteService(String id) {
        db.collection("services").document(id).delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Видалено", Toast.LENGTH_SHORT).show();
                    loadServices();
                });
    }

    public void showServiceDialog(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(service == null ? "Нова послуга" : "Редагувати послугу");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_service, null);
        EditText name = dialogView.findViewById(R.id.etName);
        EditText price = dialogView.findViewById(R.id.etPrice);
        EditText category = dialogView.findViewById(R.id.etCategory);
        EditText subcategory = dialogView.findViewById(R.id.etSubcategory);
        EditText desc = dialogView.findViewById(R.id.etDescription);

        if (service != null) {
            name.setText(service.getName());
            price.setText(service.getPrice());
            category.setText(service.getCategory());
            subcategory.setText(service.getSubcategory());
            desc.setText(service.getDescription());
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Зберегти", (dialog, which) -> {
            Map<String, Object> data = new HashMap<>();
            data.put("name", name.getText().toString());
            data.put("price", price.getText().toString());
            data.put("category", category.getText().toString());
            data.put("subcategory", subcategory.getText().toString());
            data.put("description", desc.getText().toString());

            if (service == null) {
                db.collection("services").add(data).addOnSuccessListener(doc -> loadServices());
            } else {
                db.collection("services").document(service.getId()).update(data).addOnSuccessListener(doc -> loadServices());
            }
        });
        builder.setNegativeButton("Скасувати", null);
        builder.show();
    }
}
