package com.example.myprojecttcz.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.myprojecttcz.R;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;


public class Register extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private EditText etEmail, etPassword, etFName, etLName, etPhone, etUname;
    private Button btnRegister;
    private DatabaseService databaseService;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private String email,password;
    private FirebaseAuth mAuth;

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
        finds();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }


    public void finds(){
        /// get the views

        etEmail = findViewById(R.id.rEmail);
        etPassword = findViewById(R.id.rPassword);
        etFName = findViewById(R.id.rFname);
        etLName = findViewById(R.id.rLname);
        etPhone = findViewById(R.id.rPhonenumber);
        etUname = findViewById(R.id.rUname);
        btnRegister = findViewById(R.id.registerbtn);
        // Corrected: Call the static getInstance() method on the class
        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        /// set the click listener
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view == btnRegister){
            Log.d(TAG, "onClick: Register button clicked");


            /// get the input from the user
             email = etEmail.getText().toString().trim();
             password = etPassword.getText().toString().trim();
            String fName = etFName.getText().toString().trim();
            String lName = etLName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String uName = etUname.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || fName.isEmpty() || lName.isEmpty() || phone.isEmpty() || uName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(uName,fName, lName, phone, email, password);
        }
    }
    private void registerUser(String uname, String fname, String lname, String phone, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // FIX 1: Registration check - This code runs only on success
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        boolean isadmin = false;
                        User user = new User(uid, uname, fname, lname, email,phone, password, isadmin);
                        createUserInDatabase(user);
                    } else {
                        Toast.makeText(Register.this, "Registration failed, please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Registration failed - show a message to the user
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Register.this, "Email address is already in use.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");

                // NOT FIXED (as requested): Security issue - saving password in plaintext
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.commit();

                Intent mainIntent = new Intent(Register.this, MainActivity.class);
                // שלא יוכל ללכת אחורה
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                Toast.makeText(Register.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();

                // FIX 2: Handle failure - sign out user to prevent inconsistent state
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    mAuth.signOut();
                }
            }
        });
    }
}
