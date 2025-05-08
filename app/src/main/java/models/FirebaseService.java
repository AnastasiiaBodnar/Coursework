package models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseService {
    private static FirebaseService instance;
    private final FirebaseFirestore db;
    public FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    protected FirebaseService(FirebaseAuth auth, FirebaseFirestore db) {
        this.db = db;
    }
    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }
    public FirebaseFirestore getDb() {
        return db;
    }
}