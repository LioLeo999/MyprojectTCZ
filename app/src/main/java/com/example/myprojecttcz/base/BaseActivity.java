package com.example.myprojecttcz.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

        // הערה: לא קוראים כאן ל-setupNavigationSpinner כי ה-Layout עדיין לא קיים!
    }

    /**
     * הפונקציה הזו נקראת ע"י כל האקטיביטיז שיורשות מ-BaseActivity.
     * במקום להחליף את המסך, היא לוקחת את העיצוב של הבן ומכניסה אותו לתוך ה-FrameLayout של האבא.
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // 1. טוענים את ה-Layout של הבסיס (עם ה-Toolbar)
        View fullView = getLayoutInflater().inflate(R.layout.activity_base, null);

        // 2. מוצאים את ה-FrameLayout
        FrameLayout activityContainer = fullView.findViewById(R.id.framelayout);

        // 3. מנפחים את ה-Layout של הבן לתוך ה-Container
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        // 4. מגדירים את המסך המלא כ-Content View
        super.setContentView(fullView);

        // 5. הגדרות Toolbar וכפתור חזור
        initializeToolbar();

        // 6. הפעלת הספינר (רק עכשיו כשהמסך מוכן)
        setupNavigationSpinner();

        // 7. הגדרות Insets (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.basetoolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar); // הופך את ה-Toolbar ל-ActionBar פעיל

            // בדיקה האם להציג כפתור חזור
            if (getSupportActionBar() != null) {
                boolean showBack = shouldShowBackButton();
                getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
                getSupportActionBar().setDisplayShowHomeEnabled(showBack);
            }
        }
    }

    // פונקציה שמאפשרת לאקטיביטיז הבנים להחליט אם להראות חץ חזור או לא
    public boolean shouldShowBackButton() {
        return true;
    }

    // פונקציה שמאפשרת לאקטיביטיז הבנים להחליט אם להראות את "מסך הבית" בתפריט
    protected boolean shouldShowHomeInMenu() {
        return true;
    }

    // טיפול בלחיצה על כפתור החזור ב-Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // סוגר את המסך וחוזר אחורה
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                menuItems.add("Log out");
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
                menuItems.add("Login");
                menuItems.add("Register");
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

            case "Login":
                intent = new Intent(this, LogIn.class);
                break;

            case "Register":
                intent = new Intent(this, Register.class);
                break;

            case "Admin page":
                intent = new Intent(this, AdminPage.class);
                break;

            case "Log out":
                mauth.signOut();
                intent = new Intent(this, LogIn.class);
                // מנקה את ההיסטוריה כדי שלא יוכלו לחזור אחורה
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
        }

        if (intent != null) {
            // בדיקה שאנחנו לא פותחים את אותו מסך שאנחנו כבר נמצאים בו
            if (!this.getClass().getName().equals(intent.getComponent().getClassName())) {
                startActivity(intent);
            }
        }

        // איפוס הספינר לפריט הראשון כדי שייראה יפה בפעם הבאה
        Spinner spinner = findViewById(R.id.nav_spinner);
        if (spinner != null) spinner.setSelection(0);
    }

}