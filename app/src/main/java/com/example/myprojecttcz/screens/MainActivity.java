package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button registerbtn, loginbtn, drillsBtn, chatsBtn, tsetsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        finds();
    }
    private void finds(){
        registerbtn = findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(this);
        loginbtn = findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(this);
        drillsBtn = findViewById(R.id.drillsB);
        chatsBtn = findViewById(R.id.chatsB);
        tsetsBtn = findViewById(R.id.tsetsB);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.registerbtn)
        {
            Intent go = new Intent(MainActivity.this,Register.class);
            startActivity(go);
        }
        if(v.getId() == R.id.loginbtn){
            Intent go = new Intent(MainActivity.this,LogIn.class);
            startActivity(go);

        }
    }
}