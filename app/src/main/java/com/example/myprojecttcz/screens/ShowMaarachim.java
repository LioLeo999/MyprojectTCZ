package com.example.myprojecttcz.screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

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
    private ArrayList<MaarachImun> fullMaarachList = new ArrayList<>();
    private DatabaseService ds;
    private String uid;
    private EditText etSearchMaarach;

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

        // יצירת האדאפטר עם ה-Listener החדש!
        adapter = new TrainingSetAdapter(this, maarachList, new TrainingSetAdapter.OnMaarachClickListener() {
            @Override
            public void onMaarachClick(MaarachImun maarach) {
                // כאן אתה יכול להוסיף את הקוד לכניסה לתוך מערך האימון אם יש לך (למשל Intent)
            }

            @Override
            public void onDeleteClick(MaarachImun maarach) {
                showDeleteDialog(maarach); // קריאה לפונקציית המחיקה
            }
        });
        rvTrainingSets.setAdapter(adapter);

        // אתחול תיבת החיפוש
        etSearchMaarach = findViewById(R.id.etSearchMaarach);
        setupSearchListener();

        // טעינת נתונים
        loadData();

        // כפתור הוספת מערך חדש
        findViewById(R.id.btnaddmaarach).setOnClickListener(v -> showCreateMaarachDialog());
    }

    private void setupSearchListener() {
        etSearchMaarach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        ArrayList<MaarachImun> filteredList = new ArrayList<>();
        for (MaarachImun item : fullMaarachList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        maarachList.clear();
        maarachList.addAll(filteredList);
        adapter.notifyDataSetChanged();
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
                    fullMaarachList.clear();

                    maarachList.addAll(user.getMaarachim().values());
                    fullMaarachList.addAll(user.getMaarachim().values());

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

    // הפונקציה החדשה שמוחקת את המערך!
    private void showDeleteDialog(MaarachImun maarach) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Training Set")
                .setMessage("Are you sure you want to delete '" + maarach.getName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    ds.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                        @Override
                        public User onCompleted(User currentUser) {
                            if (currentUser != null && currentUser.getMaarachim() != null) {

                                // מחיקת המערך מהרשימה של המשתמש
                                currentUser.getMaarachim().remove(maarach.getId());

                                // עדכון המשתמש במסד הנתונים
                                ds.updateUser(currentUser, new DatabaseService.DatabaseCallback<Void>() {
                                    @Override
                                    public User onCompleted(Void object) {
                                        Toast.makeText(ShowMaarachim.this, "Training Set deleted", Toast.LENGTH_SHORT).show();

                                        // מחיקה מהרשימות המקומיות כדי לעדכן את המסך מיד
                                        maarachList.remove(maarach);
                                        fullMaarachList.remove(maarach);
                                        adapter.notifyDataSetChanged();

                                        return null;
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        Toast.makeText(ShowMaarachim.this, "Failed to delete from DB", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            return null;
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(ShowMaarachim.this, "Error accessing user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
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

                    currentUser.getMaarachim().put(newMaarach.getId(), newMaarach);

                    ds.updateUser(currentUser, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public User onCompleted(Void object) {
                            Toast.makeText(ShowMaarachim.this, "Set '" + name + "' Created", Toast.LENGTH_SHORT).show();

                            maarachList.add(newMaarach);
                            fullMaarachList.add(newMaarach);

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
    @Override
    protected void onResume() {
        super.onResume();

        // הלוג הזה יעזור לך לראות ב-Logcat שהעמוד אכן התרענן
        Log.d("Lifecycle", "onResume: Refreshing data from RTDB");

        // קריאה לפונקציה שלך שמביאה את הנתונים העדכניים מהפיירבייס
        loadData();
    }
}