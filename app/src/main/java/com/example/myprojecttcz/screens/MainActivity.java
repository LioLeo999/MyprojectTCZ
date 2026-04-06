package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    // משתנים לרכיבי המסך
    // שים לב: drillsBtn ו-chatsBtn הם View כי ב-XML הם CardView
    private View drillsBtn, chatsBtn;

    // כפתורים רגילים
    private Button registerbtn, loginbtn, tsetsBtn, logoutbtn, adminBtn, toOdotBtn;

    // כפתור צף (הפלוס למטה בצד)

    // משתני Firebase ו-Database
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // התאמה למסכים מקצה לקצה (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול השירותים
        databaseService = DatabaseService.getInstance();
        auth = FirebaseAuth.getInstance();

        finds();
        setupAuthStateListener();
    }

    private void finds() {
        // חיבור הכרטיסים הגדולים (מוגדרים כ-View כדי לקלוט לחיצה מ-CardView)
        drillsBtn = findViewById(R.id.drillsB);
        drillsBtn.setOnClickListener(this);

        chatsBtn = findViewById(R.id.chatsB);
        chatsBtn.setOnClickListener(this);

        // חיבור הכפתורים הרגילים
        tsetsBtn = findViewById(R.id.tsetsB);
        tsetsBtn.setOnClickListener(this);

        adminBtn = findViewById(R.id.adminBtn); // כפתור המנהל
        adminBtn.setOnClickListener(this);

        registerbtn = findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(this);

        loginbtn = findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(this);

        logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(this);

        toOdotBtn = findViewById(R.id.toOdotBtn);
        toOdotBtn.setOnClickListener(this);
        // חיבור הכפתור הצף
    }

    private void setupAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // משתמש מחובר
                    updateUIForLoggedInUser(user);
                } else {
                    // משתמש מנותק
                    updateUIForLoggedOutUser();
                }
            }
        };
    }

    private void updateUIForLoggedInUser(FirebaseUser firebaseUser) {
        // הסתרת כפתורי התחברות והרשמה
        registerbtn.setVisibility(View.GONE);
        loginbtn.setVisibility(View.GONE);

        // הצגת כפתורי תוכן והתנתקות
        logoutbtn.setVisibility(View.VISIBLE);
        chatsBtn.setVisibility(View.VISIBLE);
        tsetsBtn.setVisibility(View.VISIBLE); // מציג את Training Sets

        // בדיקה האם המשתמש הוא מנהל
        String uid = firebaseUser.getUid();
        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User user) {
                if (user != null && user.isadmin()) { // או user.isAdmin() תלוי איך כתבת במודל
                    adminBtn.setVisibility(View.VISIBLE);
                } else {
                    adminBtn.setVisibility(View.GONE);
                }
                return user;
            }

            @Override
            public void onFailed(Exception e) {
                // במקרה של שגיאה, נחמיר ולא נציג את כפתור המנהל
                adminBtn.setVisibility(View.GONE);
            }
        });
    }

    private void updateUIForLoggedOutUser() {
        // מצב אורח
        registerbtn.setVisibility(View.VISIBLE);
        loginbtn.setVisibility(View.VISIBLE);

        logoutbtn.setVisibility(View.GONE);
        tsetsBtn.setVisibility(View.GONE); // אורח לא רואה את מערכי האימון שלו
        adminBtn.setVisibility(View.GONE); // ובוודאי לא את כפתור המנהל
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.registerbtn) {
            startActivity(new Intent(MainActivity.this, Register.class));
        }
        else if (id == R.id.loginbtn) {
            startActivity(new Intent(MainActivity.this, LogIn.class));
        }
        else if (id == R.id.logoutbtn) {
            auth.signOut();
            // יצירת Activity מחדש כדי לרענן את ה-State
            recreate();
        }
        else if (id == R.id.drillsB) { // לחיצה על הכרטיס הירוק הגדול
            startActivity(new Intent(MainActivity.this, MaagarDrills.class));
        }
        else if (id == R.id.chatsB) { // לחיצה על הכרטיס הלבן (צ'אטים)
            Intent go = new Intent(MainActivity.this, ChatsListActivity.class);
            startActivity(go);
        }
        else if (id == R.id.tsetsB) { // Training Sets
            startActivity(new Intent(MainActivity.this, ShowMaarachim.class));
        }
        else if (id == R.id.adminBtn) { // כפתור המנהל (מופיע רק לאדמין)
            startActivity(new Intent(MainActivity.this, AdminPage.class));
        } else if (id == R.id.toOdotBtn) {
            startActivity(new Intent(MainActivity.this, Odot.class));
        }


    }

    // הגדרות BaseActivity - ביטול חץ חזור וכפתור הבית במסך הראשי
    @Override
    public boolean shouldShowBackButton() {
        return false;
    }

    @Override
    protected boolean shouldShowHomeInMenu() {
        return false;
    }
}