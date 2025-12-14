package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.DrillAdapter;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.services.DatabaseService;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;


public class MaagarDrills extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btntomain;
    private RecyclerView recyclerView;
    private DrillAdapter adapter;
    private List<Drill2v> drillList;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maagar_drills);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        loaddrills();
    }

    public void initView(){
        btntomain = findViewById(R.id.tomainbtnfrommaagar);
        btntomain.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerDrills);

// Grid של 2 בעמודה
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

// יצירת הרשימה
        drillList = new ArrayList<>();

// יצירת ה-Adapter
        adapter = new DrillAdapter(this, drillList);

// חיבור ה-Adapter ל-RecyclerView
        recyclerView.setAdapter(adapter);

        databaseService = DatabaseService.getInstance();


    }

    public void loaddrills(){
        databaseService.getAllDrills(new DatabaseService.DrillsCallback() {
            @Override
            public void onSuccess(List<Drill2v> drills) {
                drillList.clear();
                drillList.addAll(drills);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                // אפשר Toast / Log
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == btntomain){
            Intent go = new Intent(MaagarDrills.this, MainActivity.class);
            startActivity(go);
        }
    }


}