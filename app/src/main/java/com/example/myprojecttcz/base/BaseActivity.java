package com.example.myprojecttcz.base;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Chat;
import com.example.myprojecttcz.model.Message;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.screens.AdminPage;
import com.example.myprojecttcz.screens.LogIn;
import com.example.myprojecttcz.screens.MaagarDrills;
import com.example.myprojecttcz.screens.MainActivity;
import com.example.myprojecttcz.screens.Odot;
import com.example.myprojecttcz.screens.Register;
import com.example.myprojecttcz.screens.ShowMaarachim;
import com.example.myprojecttcz.screens.UserProfile;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
    protected DatabaseService databaseService;
    protected FirebaseAuth mauth;

    // משתנים עבור מערכת ההתראות
    private DatabaseReference chatsRef;
    private ChildEventListener chatsListener;
    private long listenerStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // אתחול שירותים
        databaseService = DatabaseService.getInstance();
        mauth = FirebaseAuth.getInstance();

        // בקשת הרשאה להתראות (לאנדרואיד 13 ומעלה)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // מתחילים להאזין להודעות רק כשהמשתמש מחובר והמסך מוצג
        if (mauth.getCurrentUser() != null) {
            startListeningForNewMessages();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // מפסיקים להאזין כשהמסך מוסתר כדי למנוע כפילויות בהתראות
        stopListeningForNewMessages();
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

    public void updateUIForUser() {
        Button btnLogin = findViewById(R.id.loginbtn);
        Button btnRegister = findViewById(R.id.registerbtn);
        Button btnLogout = findViewById(R.id.logoutbtn);

        // הגנה מפני קריסה
        if (btnLogin == null || btnLogout == null || btnRegister == null) return;

        if (mauth.getCurrentUser() != null) {
            // -- משתמש מחובר --
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
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);

            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, LogIn.class);
                startActivity(intent);
            });

            btnRegister.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, Register.class);
                startActivity(intent);
            });
        }
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
                menuItems.add("About");
                menuItems.add("Profile info");
                menuItems.add("Training sets");

                databaseService.getUser(mauth.getUid(), new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public User onCompleted(User object) {
                        User currentUser = object;
                        if (currentUser != null && currentUser.isadmin())
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
            case "About":
                intent = new Intent(this, Odot.class );
                break;

            case "Admin page":
                intent = new Intent(this, AdminPage.class);
                break;

        }

        if (intent != null) {
            if (!this.getClass().getName().equals(intent.getComponent().getClassName())) {
                startActivity(intent);
            }
        }
        Spinner spinner = findViewById(R.id.nav_spinner);
        if (spinner != null) spinner.setSelection(0);
    }

    // =========================================================
    // פונקציות לניהול התראות והודעות צ'אט
    // =========================================================

    private void startListeningForNewMessages() {
        if (mauth.getCurrentUser() == null) return;

        String currentUserId = mauth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");

        // זמן תחילת ההאזנה - כדי לא להקפיץ התראות על הודעות היסטוריות
        listenerStartTime = System.currentTimeMillis();

        chatsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot chatSnapshot, @Nullable String previousChildName) {
                Chat chat = chatSnapshot.getValue(Chat.class);
                // אם המשתמש הוא חלק מהצ'אט הזה
                if (chat != null && chat.getMembers() != null && chat.getMembers().contains(currentUserId)) {

                    // מאזינים לכל הודעה שנוספת לצ'אט הזה
                    chatSnapshot.child("messages").getRef().addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot msgSnapshot, @Nullable String previousChildName) {
                            Message msg = msgSnapshot.getValue(Message.class);

                            // מוודאים שההודעה תקינה, שהיא לא ממני, ושהיא חדשה לגמרי
                            if (msg != null &&
                                    msg.getSenderId() != null &&
                                    !msg.getSenderId().equals(currentUserId) &&
                                    msg.getTimestamp() > listenerStartTime) {

                                // מקפיצים את ההתראה
                                showLocalNotification("הודעה חדשה באפליקציה", msg.getContent());

                                // מעדכנים את הזמן כדי למנוע קפיצות כפולות לאותה הודעה
                                listenerStartTime = msg.getTimestamp();
                            }
                        }

                        @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                        @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                        @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };

        chatsRef.addChildEventListener(chatsListener);
    }

    private void stopListeningForNewMessages() {
        if (chatsRef != null && chatsListener != null) {
            chatsRef.removeEventListener(chatsListener);
        }
    }

    public void showLocalNotification(String title, String messageText) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "chat_notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "הודעות צ'אט",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // אם האייקון לא מופיע טוב, תוכל להחליף ל- R.drawable.ic_launcher_foreground או אייקון אחר
                .setContentTitle(title)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}