package com.example.myprojecttcz.screens;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.TrainingSetAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.MaarachImun;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowMaarachim extends BaseActivity {

    private RecyclerView rvTrainingSets;
    private TrainingSetAdapter adapter;
    private ArrayList<MaarachImun> maarachList = new ArrayList<>();
    private DatabaseService ds;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_maarachim);

        // אתחול שירותים
        ds = DatabaseService.getInstance();
        uid = FirebaseAuth.getInstance().getUid();

        // אתחול ה-RecyclerView
        rvTrainingSets = findViewById(R.id.rvTrainingSets);
        rvTrainingSets.setLayoutManager(new LinearLayoutManager(this));

        // יצירת האדאפטר עם הרשימה הריקה (תתמלא בהמשך)
        adapter = new TrainingSetAdapter(this, maarachList);
        rvTrainingSets.setAdapter(adapter);

        // טעינת נתונים ראשונית מה-Firebase
        loadData();

        // כפתור הוספת מערך חדש
        findViewById(R.id.btnaddmaarach).setOnClickListener(v -> showCreateMaarachDialog());
    }

    private void loadData() {
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ds.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User user) {
                if (user != null && user.getMaarachim() != null) {
                    maarachList.clear();

                    // תיקון: המרה מ-HashMap ל-ArrayList עבור האדפטר
                    maarachList.addAll(user.getMaarachim().values());

                    adapter.notifyDataSetChanged();
                }
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ShowMaarachim.this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateMaarachDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Training Set");

        final EditText input = new EditText(this);
        input.setHint("Enter set name...");

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 60;
        params.rightMargin = 60;
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                createNewSet(name);
            } else {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void createNewSet(String name) {
        if (uid == null) return;

        String newId = ds.generateMaarachId(uid);
        MaarachImun newMaarach = new MaarachImun(newId, name, "", new ArrayList<>());

        ds.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User currentUser) {
                if (currentUser != null) {
                    if (currentUser.getMaarachim() == null) {
                        currentUser.setMaarachim(new HashMap<>());
                    }

                    // תיקון: שימוש ב-put עם ה-ID כמפתח
                    currentUser.getMaarachim().put(newMaarach.getId(), newMaarach);

                    ds.updateUser(currentUser, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public User onCompleted(Void object) {
                            Toast.makeText(ShowMaarachim.this, "Set '" + name + "' Created", Toast.LENGTH_SHORT).show();

                            maarachList.add(newMaarach);
                            adapter.notifyItemInserted(maarachList.size() - 1);
                            rvTrainingSets.scrollToPosition(maarachList.size() - 1);

                            return null;
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(ShowMaarachim.this, "Save failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ShowMaarachim.this, "Fetch user failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}