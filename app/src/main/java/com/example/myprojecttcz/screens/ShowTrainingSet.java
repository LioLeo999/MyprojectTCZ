package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.DrillListReorderAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.model.MaarachImun;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ShowTrainingSet extends BaseActivity implements View.OnClickListener {

    private RecyclerView rvDrillsList;
    private DrillListReorderAdapter adapter;
    private List<Drill2v> drillsList = new ArrayList<>();

    private DatabaseService ds;
    private String currentMaarachId;
    private MaarachImun currentMaarach;
    private Button btnGoToMaagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_training_set);

        // 1. קבלת ID
        currentMaarachId = getIntent().getStringExtra("maarach_id");
        if (currentMaarachId == null) {
            Toast.makeText(this, "Error: No set ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ds = DatabaseService.getInstance();

        btnGoToMaagar = findViewById(R.id.btnGoToMaagar);
        btnGoToMaagar.setOnClickListener(this);

        // 2. הגדרת RecyclerView
        rvDrillsList = findViewById(R.id.rvDrillsList);
        rvDrillsList.setLayoutManager(new LinearLayoutManager(this));

        // יצירת האדפטר שלך
        adapter = new DrillListReorderAdapter(this, drillsList);
        rvDrillsList.setAdapter(adapter);

        // 3. הפעלת מנגנון הגרירה
        setupDragAndDrop();

        // 4. טעינת נתונים
        loadData();
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder source,
                                  @NonNull RecyclerView.ViewHolder target) {

                int fromPos = source.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                adapter.moveItem(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                saveOrderToFirebase();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvDrillsList);
    }

    private void loadData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        ds.getMaarachImun(uid, currentMaarachId, new DatabaseService.DatabaseCallback<MaarachImun>() {
            @Override
            public User onCompleted(MaarachImun maarach) {
                if (maarach != null) {
                    currentMaarach = maarach;
                    if (maarach.getDrillsid() != null && !maarach.getDrillsid().isEmpty()) {
                        fetchDrills(maarach.getDrillsid());
                    } else {
                        drillsList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ShowTrainingSet.this, "Failed to load set", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDrills(ArrayList<String> drillIds) {
        ds.getAllDrills(new DatabaseService.DrillsCallback() {
            @Override
            public void onSuccess(List<Drill2v> allDrills) {
                drillsList.clear();

                // משתנים למעקב אחרי מחיקות (Lazy Cleanup)
                boolean needsCleanup = false;
                ArrayList<String> updatedValidIds = new ArrayList<>();

                // שמירה על הסדר המקורי לפי רשימת ה-IDs
                for (String id : drillIds) {
                    boolean drillFound = false;

                    for (Drill2v drill : allDrills) {
                        if (drill.getId().equals(id)) {
                            drillsList.add(drill);
                            updatedValidIds.add(id); // שומרים רק את מי שבאמת קיים!
                            drillFound = true;
                            break;
                        }
                    }

                    // אם סיימנו לחפש במאגר והדריל לא קיים יותר (האדמין מחק אותו)
                    if (!drillFound) {
                        needsCleanup = true;
                        Log.w("ShowTrainingSet", "Drill ID " + id + " was deleted from the main DB. Will auto-clean.");
                    }
                }

                // עדכון התצוגה למשתמש
                adapter.notifyDataSetChanged();

                // הפעלת הניקוי האוטומטי אם מצאנו דרילים שנמחקו
                if (needsCleanup) {
                    currentMaarach.setDrillsid(updatedValidIds); // מעדכנים מקומית לאובייקט

                    String uid = FirebaseAuth.getInstance().getUid();
                    if (uid != null) {
                        // הפעלת פונקציית העדכון בסרביס
                        ds.updateMaarachDrillsList(uid, currentMaarachId, updatedValidIds);
                    }
                }
            }

            @Override
            public void onError(String error) {
                // אם יש בעיית רשת, השגיאה תגיע לפה ולא נבצע ניקוי בטעות!
                Toast.makeText(ShowTrainingSet.this, "Error fetching drills", Toast.LENGTH_SHORT).show();
                Log.e("ShowTrainingSet", "Failed to get drills from DB: " + error);
            }
        });
    }

    private void saveOrderToFirebase() {
        if (currentMaarach == null) return;

        ArrayList<String> newIds = new ArrayList<>();
        for (Drill2v d : adapter.getDrills()) {
            newIds.add(d.getId());
        }

        currentMaarach.setDrillsid(newIds);
        String uid = FirebaseAuth.getInstance().getUid();

        ds.createMaarachImun(uid, currentMaarach, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ShowTrainingSet.this, "Failed to save order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btnGoToMaagar) {
            Intent go = new Intent(this, MaagarDrills.class);
            startActivity(go);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume: Refreshing data from RTDB");
        loadData();
    }
}