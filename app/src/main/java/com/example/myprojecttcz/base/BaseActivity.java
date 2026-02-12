package com.example.myprojecttcz.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button; // הוספתי ייבוא לכפתור
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.screens.AdminPage;
import com.example.myprojecttcz.screens.LogIn;
import com.example.myprojecttcz.screens.MaagarDrills;
import com.example.myprojecttcz.screens.MainActivity;
import com.example.myprojecttcz.screens.Register;
import com.example.myprojecttcz.screens.ShowMaarachim;
import com.example.myprojecttcz.screens.UserProfile;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
    protected DatabaseService databaseService;
    protected FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // אתחול שירותים
        databaseService = DatabaseService.getInstance();
        mauth = FirebaseAuth.getInstance();

        // הסרתי מפה את updateUIForUser - הוא ייקרא ב-setContentView
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View fullView = getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.framelayout);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        initializeToolbar();
        setupNavigationSpinner();

        // קריאה לפונקציה רק כשהמסך מוכן
        updateUIForUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.basetoolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                boolean showBack = shouldShowBackButton();
                getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
                getSupportActionBar().setDisplayShowHomeEnabled(showBack);
            }
        }
    }

    public boolean shouldShowBackButton() {
        return true;
    }

    protected boolean shouldShowHomeInMenu() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ... (פונקציות ה-Spinner נשארות זהות) ...

    // --- הפונקציה המעודכנת ---
    public void updateUIForUser() {
        Button btnLogin = findViewById(R.id.loginbtn);
        Button btnRegister = findViewById(R.id.registerbtn); // כפתור חדש
        Button btnLogout = findViewById(R.id.logoutbtn);

        // הגנה מפני קריסה
        if (btnLogin == null || btnLogout == null || btnRegister == null) return;

        if (mauth.getCurrentUser() != null) {
            // -- משתמש מחובר --
            // מסתירים את Login ו-Register, מראים את Logout
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);

            btnLogout.setOnClickListener(v -> {
                mauth.signOut();
                Intent intent = new Intent(BaseActivity.this, LogIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });

        } else {
            // -- משתמש לא מחובר (אורח) --
            // מראים את Login ו-Register, מסתירים את Logout
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);

            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, LogIn.class);
                startActivity(intent);
            });

            // לחיצה על Register
            btnRegister.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, Register.class);
                startActivity(intent);
            });
        }
    }

    // יש להעתיק גם את setupNavigationSpinner ו-handleNavigationSelection מהקוד הקודם שלך אם הם לא מופיעים כאן
    // (קיצרתי כדי לחסוך מקום, אך בקוד שלך תשאיר אותם)

    private void setupNavigationSpinner() {
        Toolbar toolbar = findViewById(R.id.basetoolbar);
        if (toolbar == null) return;

        ImageView menuIcon = toolbar.findViewById(R.id.menu_icon);
        Spinner spinner = toolbar.findViewById(R.id.nav_spinner);

        if (spinner != null && menuIcon != null) {

            // 1. יצירת רשימה דינמית
            ArrayList<String> menuItems = new ArrayList<>();
            menuItems.add("Menu"); // כותרת (פריט 0 שלא עושה כלום)

            // בדיקה האם להוסיף את מסך הבית לרשימה
            if (shouldShowHomeInMenu()) {
                menuItems.add("Home page");
            }

            // --- בדיקת משתמש מחובר ---
            if (mauth.getCurrentUser() != null) {
                // >> משתמש מחובר <<
                menuItems.add("Drills");
                menuItems.add("Profile info");
                menuItems.add("Training sets");
                // menuItems.add("Log out"); // הסרתי כי יש כפתור
                databaseService.getUser(mauth.getUid(), new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public User onCompleted(User object) {
                        User currentUser = object;
                        if (currentUser.isadmin())
                            menuItems.add("Admin page");
                        return object;
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            } else {
                // >> אורח (לא מחובר) <<
                menuItems.add("Drills");
                // menuItems.add("Login"); // הסרתי כי יש כפתור
                // menuItems.add("Register");
            }

            // 2. יצירת ה-Adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    menuItems
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            // לחיצה על התמונה -> פותחת את הספינר
            menuIcon.setOnClickListener(v -> spinner.performClick());

            // 3. טיפול בבחירה מהרשימה
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = menuItems.get(position);
                    handleNavigationSelection(selectedItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private void handleNavigationSelection(String selection) {
        Intent intent = null;

        switch (selection) {
            case "Home page":
                intent = new Intent(this, MainActivity.class);
                break;

            case "Drills":
                intent = new Intent(this, MaagarDrills.class);
                break;

            case "Profile info":
                intent = new Intent(this, UserProfile.class);
                break;

            case "Training sets":
                intent = new Intent(this, ShowMaarachim.class);
                break;

            case "Admin page":
                intent = new Intent(this, AdminPage.class);
                break;

            // הסרתי את Login ו-Log out מהסוויץ' כי הם מטופלים בכפתורים החדשים
        }

        if (intent != null) {
            if (!this.getClass().getName().equals(intent.getComponent().getClassName())) {
                startActivity(intent);
            }
        }
        Spinner spinner = findViewById(R.id.nav_spinner);
        if (spinner != null) spinner.setSelection(0);
    }
}