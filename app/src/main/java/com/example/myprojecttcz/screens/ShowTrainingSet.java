package com.example.myprojecttcz.screens;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.DrillListReorderAdapter; // האדפטר שלך
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.model.MaarachImun;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ShowTrainingSet extends BaseActivity {

    private RecyclerView rvDrillsList;
    private DrillListReorderAdapter adapter; // שימוש באדפטר ששלחת
    private List<Drill2v> drillsList = new ArrayList<>();

    private DatabaseService ds;
    private String currentMaarachId;
    private MaarachImun currentMaarach;

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

        // 2. הגדרת RecyclerView
        rvDrillsList = findViewById(R.id.rvDrillsList);
        rvDrillsList.setLayoutManager(new LinearLayoutManager(this));

        // יצירת האדפטר שלך
        adapter = new DrillListReorderAdapter(this, drillsList);
        rvDrillsList.setAdapter(adapter);

        // 3. הפעלת מנגנון הגרירה (חשוב!)
        setupDragAndDrop();

        // 4. טעינת נתונים
        loadData();
    }

    private void setupDragAndDrop() {
        // מגדירים תמיכה בגרירה למעלה (UP) ולמטה (DOWN)
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder source,
                                  @NonNull RecyclerView.ViewHolder target) {

                int fromPos = source.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                // *** כאן אנחנו קוראים לפונקציה שכתבת באדפטר ***
                adapter.moveItem(fromPos, toPos);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // לא עושים כלום בגרירה לצדדים
            }

            // הפונקציה הזו נקראת כשהמשתמש *עוזב* את הפריט (סיים לגרור)
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // זה הזמן לשמור את הסדר החדש ב-Firebase
                saveOrderToFirebase();
            }
        };

        // חיבור ה-Helper ל-RecyclerView
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
                // שמירה על הסדר המקורי לפי רשימת ה-IDs
                for (String id : drillIds) {
                    for (Drill2v drill : allDrills) {
                        if (drill.getId().equals(id)) {
                            drillsList.add(drill);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ShowTrainingSet.this, "Error fetching drills", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveOrderToFirebase() {
        if (currentMaarach == null) return;

        // יצירת רשימת IDs חדשה לפי הסדר הנוכחי באדפטר
        // אנחנו משתמשים ב-getDrills() שכתבת באדפטר
        ArrayList<String> newIds = new ArrayList<>();
        for (Drill2v d : adapter.getDrills()) {
            newIds.add(d.getId());
        }

        currentMaarach.setDrillsid(newIds);
        String uid = FirebaseAuth.getInstance().getUid();

        ds.createMaarachImun(uid, currentMaarach, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                // נשמר בהצלחה (שקט)
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ShowTrainingSet.this, "Failed to save order", Toast.LENGTH_SHORT).show();
            }
        });
    }
}