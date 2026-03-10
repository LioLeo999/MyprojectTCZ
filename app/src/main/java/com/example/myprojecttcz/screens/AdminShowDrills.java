package com.example.myprojecttcz.screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.AdminDrillAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Drill2v; // המודל שלך
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class AdminShowDrills extends BaseActivity {

    private RecyclerView recyclerView;
    private EditText etSearch;
    private AdminDrillAdapter adapter;
    private List<Drill2v> drillList;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_show_drills);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // אתחול משתנים
        recyclerView = findViewById(R.id.recyclerViewadminDrills);
        etSearch = findViewById(R.id.etAdminSearchDrill);
        drillList = new ArrayList<>();
        databaseService = DatabaseService.getInstance(); // שימוש בסרביס שלך

        // הגדרת ה-RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // אתחול האדפטר
        adapter = new AdminDrillAdapter(drillList, new AdminDrillAdapter.OnDrillClickListener() {
            @Override
            public void onDrillClick(Drill2v drill) {
                // מעבר לדף תצוגת דריל - שולחים את ה-ID של הדריל
                Intent intent = new Intent(AdminShowDrills.this, ShowDrill.class);
                intent.putExtra("id", drill.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Drill2v drill) {
                showDeleteDialog(drill);
            }
        });
        recyclerView.setAdapter(adapter);

        // טעינת נתונים ראשונית
        loadDrills();

        // האזנה לשורת החיפוש
        etSearch.addTextChangedListener(new TextWatcher() {
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

    // פונקציה שמשתמשת בסרביס שלך כדי למשוך את כל הדרילים
    private void loadDrills() {
        databaseService.getAllDrills(new DatabaseService.DrillsCallback() {
            @Override
            public void onSuccess(List<Drill2v> drills) {
                drillList.clear();
                drillList.addAll(drills);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminShowDrills.this, "Failed to load drills: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // פונקציית סינון (חיפוש)
    private void filter(String text) {
        List<Drill2v> filteredList = new ArrayList<>();
        for (Drill2v drill : drillList) {
            // אם השם מכיל את הטקסט שחיפשנו (לא רגיש לאותיות גדולות/קטנות)
            if (drill.getName() != null && drill.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(drill);
            }
        }
        adapter.filterList(filteredList);
    }

    // פונקציית מחיקה שמשתמשת בסרביס
    private void showDeleteDialog(Drill2v drill) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Drill")
                .setMessage("Are you sure you want to delete '" + drill.getName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseService.deleteDrill(drill.getId(), new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public User onCompleted(Void object) {
                            Toast.makeText(AdminShowDrills.this, "Drill deleted successfully", Toast.LENGTH_SHORT).show();
                            // מרענן את הרשימה אחרי המחיקה
                            loadDrills();
                            return null;
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(AdminShowDrills.this, "Failed to delete drill", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}