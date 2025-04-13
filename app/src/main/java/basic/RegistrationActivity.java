package basic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etName, etSurname, etPhone, etLogin, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        // Пошук елементів за ID
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
        String email = etLogin.getText().toString().trim(); // ← використовується як логін
        String password = etPassword.getText().toString().trim();

        // Перевірка полів
        if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Будь ласка, заповніть усі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль повинен містити щонайменше 6 символів", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Реєстрація успішна!", Toast.LENGTH_SHORT).show();

                        // Тут можеш зберегти додаткові дані (name, surname, phone) у Firebase Realtime Database або Firestore
                        // або перейти на інший екран:
                        // startActivity(new Intent(this, LoginActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, "Помилка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
