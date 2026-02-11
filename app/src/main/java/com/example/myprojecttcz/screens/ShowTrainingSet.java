package com.example.myprojecttcz.screens;

import android.os.Bundle;
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

public class ShowTrainingSet extends BaseActivity {

    private RecyclerView rvDrillsList;
    private DrillListReorderAdapter adapter;
    private List<Drill2v> drillsList = new ArrayList<>();

    private DatabaseService ds;
    private String currentMaarachId;
    private MaarachImun currentMaarach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_training_set);

        // 1. קבלת ה-ID מהאינטנט
        currentMaarachId = getIntent().getStringExtra("maarach_id");
        if (currentMaarachId == null) {
            Toast.makeText(this, "Error: No set ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ds = DatabaseService.getInstance();

        // 2. הגדרת ה-RecyclerView
        rvDrillsList = findViewById(R.id.rvDrillsList);
        rvDrillsList.setLayoutManager(new LinearLayoutManager(this));

        // ודא ש-DrillListReorderAdapter קיים בפרויקט שלך ומקבל את הרשימה הזו
        adapter = new DrillListReorderAdapter(this, drillsList);
        rvDrillsList.setAdapter(adapter);

        // 3. הפעלת מנגנון הגרירה
        setupDragAndDrop();

        // 4. טעינת הנתונים
        loadData();
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                // ביצוע ההחלפה באדפטר (הנחה שהמתודה קיימת באדפטר שלך)
                adapter.moveItem(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // לא רלוונטי לגרירה אנכית
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // שמירה ב-Firebase רק כשהמשתמש משחרר את הפריט
                saveOrderToFirebase();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvDrillsList);
    }

    private void loadData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // שליפת המערך הספציפי לפי ID
        // זה עובד מצוין גם עם המבנה החדש של HashMap כי אנחנו ניגשים ישירות לנתיב ה-ID
        ds.getMaarachImun(uid, currentMaarachId, new DatabaseService.DatabaseCallback<MaarachImun>() {
            @Override
            public User onCompleted(MaarachImun maarach) {
                if (maarach != null) {
                    currentMaarach = maarach;
                    // אם הרשימה ריקה, מונעים קריסה
                    if (maarach.getDrillsid() != null && !maarach.getDrillsid().isEmpty()) {
                        fetchDrills(maarach.getDrillsid());
                    } else {
                        // אם אין תרגילים, מנקים את הרשימה
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
        // משיכת כל הדרילים וסינון לפי הרשימה שיש לנו
        ds.getAllDrills(new DatabaseService.DrillsCallback() {
            @Override
            public void onSuccess(List<Drill2v> allDrills) {
                drillsList.clear();

                // אלגוריתם לשמירה על הסדר המקורי של drillIds
                // עוברים על רשימת ה-IDs (שקובעת את הסדר)
                for (String id : drillIds) {
                    // עבור כל ID, מחפשים את האובייקט המתאים ברשימה הגדולה
                    for (Drill2v drill : allDrills) {
                        if (drill.getId().equals(id)) {
                            drillsList.add(drill);
                            break; // מצאנו, עוברים ל-ID הבא
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
        // הנחה: ל-Adapter יש מתודה getDrills() שמחזירה את הרשימה המסודרת
        ArrayList<String> newIds = new ArrayList<>();
        for (Drill2v d : adapter.getDrills()) {
            newIds.add(d.getId());
        }

        currentMaarach.setDrillsid(newIds);
        String uid = FirebaseAuth.getInstance().getUid();

        // עדכון ב-Firebase
        // הפונקציה createMaarachImun כותבת לנתיב הספציפי של ה-ID, ולכן זה תקין
        ds.createMaarachImun(uid, currentMaarach, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                // נשמר בהצלחה (אפשר להוסיף לוג או טוסט קטן אם רוצים)
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ShowTrainingSet.this, "Failed to save order", Toast.LENGTH_SHORT).show();
            }
        });
    }
}