package basic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.example.coursework.NavActivity;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText etName, etSurname, etPhone, etLogin, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etPhone = findViewById(R.id.etPhone);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Будь ласка, заповніть усі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль повинен містити щонайменше 6 символів", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("firstName", name);
                        userData.put("lastName", surname);
                        userData.put("phone", phone);
                        userData.put("email", email);
                        userData.put("role", "client");

                        db.collection("users").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegistrationActivity.this, "Реєстрація успішна!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistrationActivity.this, NavActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegistrationActivity.this, "Помилка при збереженні даних: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(this, "Помилка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}