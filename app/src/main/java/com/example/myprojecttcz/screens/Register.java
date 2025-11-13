package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail, etPassword, etFName, etLName, etPhone, etUnam;
    private Button btnRegister, toMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void finds(){
        /// get the views
        etEmail = findViewById(R.id.rEmail);
        etPassword = findViewById(R.id.rPassword);
        etFName = findViewById(R.id.rFname);
        etLName = findViewById(R.id.rLname);
        etPhone = findViewById(R.id.rPhonenumber);
        btnRegister = findViewById(R.id.registerbtn);
        toMain = findViewById(R.id.rtomain);


        /// set the click listener
        btnRegister.setOnClickListener(this);
        toMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == toMain){
            Intent intent = new Intent(Register.this,MainActivity.class);
            startActivity(intent);
        }

    }
}