package com.example.myprojecttcz.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;


public class Register extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private EditText etEmail, etPassword, etFName, etLName, etPhone, etUname;
    private Button btnRegister;
    private ImageButton toMain;
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
        toMain = findViewById(R.id.rtomain);
        databaseService = databaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
        if (view == btnRegister){
            Log.d(TAG, "onClick: Register button clicked");


            /// get the input from the user
             email = etEmail.getText().toString();
             password = etPassword.getText().toString();
            String fName = etFName.getText().toString();
            String lName = etLName.getText().toString();
            String phone = etPhone.getText().toString();
            String uName = etUname.getText().toString();


            registerUser(uName,fName, lName, phone, email, password);


        }



    }
    private void registerUser(String uname, String fname, String lname, String phone, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            boolean isadmin = false;
            User user = new User(uid, uname, fname, lname, email,phone, password, isadmin);
            createUserInDatabase(user);

        });
        Log.d(TAG, "registerUser: Registering user...");



        /// create a new user object


        /*databaseService.checkIfEmailExists(email, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    Log.e(TAG, "onCompleted: Email already exists");
                    /// show error message to user
                    Toast.makeText(Register.this, "", Toast.LENGTH_SHORT).show();
                    Toast.makeText(Register.this, "Email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    /// proceed to create the user
                    createUserInDatabase(user);
                }
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to check if email exists", e);
                /// show error message to user
                Toast.makeText(Register.this, "Failed to register user bcuz email", Toast.LENGTH_SHORT).show();
            }
        });
*/
    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(Register.this, MainActivity.class);
                /// clear the back stack (clear history) and start the MainActivity

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("email", email);
                editor.putString("password", password);

                editor.commit();





                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }
}