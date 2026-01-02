package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.example.myprojecttcz.utils.SharedPreferencesUtil;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private ImageButton tomainbtn;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if user is already logged in
        if (SharedPreferencesUtil.isUserLoggedIn(this)) {
            Intent mainIntent = new Intent(LogIn.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
            return;
        }

        finds();
    }

    private void finds() {
        tomainbtn = findViewById(R.id.tomainbtn);
        tomainbtn.setOnClickListener(this);
        etEmail = findViewById(R.id.emailli);
        etPassword = findViewById(R.id.passli);
        btnLogin = findViewById(R.id.btnLogIn);
        btnLogin.setOnClickListener(this);
        btnRegister = findViewById(R.id.gotoRegister);
        btnRegister.setOnClickListener(this);
        databaseService = DatabaseService.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tomainbtn) {
            Intent go = new Intent(this, MainActivity.class);
            startActivity(go);
        } else if (view.getId() == btnLogin.getId()) {
            Log.d(TAG, "onClick: Login button clicked");

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "onClick: Logging in user...");
            loginUser(email, password);
        } else if (view.getId() == btnRegister.getId()) {
            Intent registerIntent = new Intent(LogIn.this, Register.class);
            startActivity(registerIntent);
        }
    }

    private void loginUser(String email, String password) {
        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "onCompleted: User logged in with UID: " + uid);
                // Now, get the full user object
                databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User user) {
                        if (user != null) {
                            Log.d(TAG, "Successfully fetched user data.");
                            // Save user to SharedPreferences
                            SharedPreferencesUtil.saveUser(LogIn.this, user);

                            // Redirect to main activity
                            Toast.makeText(LogIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(LogIn.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            // This case is unlikely if UID is correct, but good to handle
                            Log.e(TAG, "onCompleted: Failed to fetch user data, user is null");
                            Toast.makeText(LogIn.this, "Login failed: Could not fetch user details.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e(TAG, "onFailed: Failed to retrieve user data after login", e);
                        Toast.makeText(LogIn.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to log in", e);
                etPassword.setError("Invalid email or password");
                etPassword.requestFocus();
                Toast.makeText(LogIn.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
