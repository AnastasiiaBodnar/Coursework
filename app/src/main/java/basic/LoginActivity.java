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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import admin.AdminActivity;
import client.MainClientActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText etLogin, etPassword;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Будь ласка, заповніть усі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль повинен містити щонайменше 6 символів", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserRole(user.getUid());
                    } else {
                        Toast.makeText(this, "Помилка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserRole(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            if (role != null && role.equals("admin")) {

                                Toast.makeText(LoginActivity.this, "Вітаємо, адміністратор!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Вхід успішний!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainClientActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Вхід успішний!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainClientActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Помилка при отриманні даних користувача", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}