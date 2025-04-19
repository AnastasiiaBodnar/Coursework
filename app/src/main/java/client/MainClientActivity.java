package client;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.coursework.HomeFragment;
import com.example.coursework.MastersFragment;
import com.example.coursework.ProfileFragment;
import com.example.coursework.R;
import com.example.coursework.ServicesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainClientActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainClient), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.navHome){

                    loadFragment(new HomeFragment(),false);

                } else if (itemId == R.id.navServices) {
                    loadFragment(new ServicesFragment(),false);
                }
                else if (itemId == R.id.navMasters) {
                    loadFragment(new MastersFragment(),false);
                }
                else {
                    loadFragment(new ProfileFragment(),false);
                }
                return true;
            }
        });

        loadFragment(new HomeFragment(),true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isAppInitialized){
            fragmentTransaction.add(R.id.frameLayout, fragment);
        }
        else{
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }


        fragmentTransaction.commit();

    }
}