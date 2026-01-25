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
import com.example.myprojecttcz.base.BaseActivity;

public class AdminPage extends BaseActivity implements View.OnClickListener {
    private Button btnuserstable, btnshowdrills, btnaddDrills;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
    }
    public void initView(){
        btnuserstable = findViewById(R.id.userstable);
        btnuserstable.setOnClickListener(this);
        btnshowdrills = findViewById(R.id.showdrills);
        btnshowdrills.setOnClickListener(this);
        btnaddDrills = findViewById(R.id.addDrills);
        btnaddDrills.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {

        if (v.getId() == btnuserstable.getId()){
            Intent go = new Intent(AdminPage.this, UsersTable.class);
            startActivity(go);
        }
        if (v.getId() == btnshowdrills.getId()){

        }
        if (v.getId() == btnaddDrills.getId()){
            Intent go = new Intent(AdminPage.this, AddDrill.class);
            startActivity(go);
        }
    }
}