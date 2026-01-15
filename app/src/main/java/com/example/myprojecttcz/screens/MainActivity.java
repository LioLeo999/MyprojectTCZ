package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    Button registerbtn, loginbtn, drillsBtn, chatsBtn, tsetsBtn,toadmin,logoutbtn;
    User nuser = new User();
    FirebaseAuth auth;
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

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
        showBtns();
    }
    private void finds(){
        registerbtn = findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(this);
        loginbtn = findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(this);
        drillsBtn = findViewById(R.id.drillsB);
        drillsBtn.setOnClickListener(this);
        chatsBtn = findViewById(R.id.chatsB);
        tsetsBtn = findViewById(R.id.tsetsB);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        toadmin = findViewById(R.id.maintomasterpage);
        toadmin.setOnClickListener(this);
        logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(this);
    }
    private void showBtns(){

            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                databaseService.getUser(currentUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public User onCompleted(User object) {
                        nuser = object;
                        return object;
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
                // משתמש מחובר → להציג כפתורים
                // registerbtn.setVisibility(View.GONE);
                //loginbtn.setVisibility(View.GONE);
                //tsetsBtn.setVisibility(View.VISIBLE);
                //logoutbtn.setVisibility(View.VISIBLE);



                //registerbtn.setVisibility(View.VISIBLE);
                //loginbtn.setVisibility(View.VISIBLE);
                //tsetsBtn.setVisibility(View.GONE);
                //toadmin.setVisibility(View.GONE);
                //logoutbtn.setVisibility(View.GONE);
            }


    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    // 🔥 4 - להוריד אותו כשהמסך נסגר
    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.registerbtn)
        {
            Intent go = new Intent(MainActivity.this, Register.class);
            startActivity(go);
        }
        if(v.getId() == R.id.loginbtn){
            Intent go = new Intent(MainActivity.this,LogIn.class);
            startActivity(go);

        }
        if (v.getId() == R.id.maintomasterpage){
            Intent go = new Intent(MainActivity.this, AdminPage.class);
            startActivity(go);
        }
        if (v.getId() == R.id.logoutbtn){
            FirebaseAuth.getInstance().signOut();
        }
        if (v == drillsBtn){
            Intent go = new Intent(MainActivity.this, MaagarDrills.class);
            startActivity(go);
        }
    }
}