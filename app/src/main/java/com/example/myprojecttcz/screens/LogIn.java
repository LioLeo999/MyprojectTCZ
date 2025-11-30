package com.example.myprojecttcz.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.myprojecttcz.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.android.gms.common.util.SharedPreferencesUtils;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/// Activity for logging in the user
/// This activity is used to log in the user
/// It contains fields for the user to enter their email and password
/// It also contains a button to log in the user
/// When the user is logged in, they are redirected to the main activity


public class LogIn extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    ImageButton tomainbtn;
    private String email, pass;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseService databaseService;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
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
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        finds();
        email=sharedpreferences.getString("email","");
        pass=sharedpreferences.getString("password","");
        etEmail.setText(email);
        etPassword.setText(pass);

    }
    private void finds(){
        tomainbtn=findViewById(R.id.tomainbtn);
        tomainbtn.setOnClickListener(this);
        etEmail = findViewById(R.id.emailli);
        etPassword = findViewById(R.id.passli);
        btnLogin = findViewById(R.id.btnLogIn);
        btnLogin.setOnClickListener(this);
        btnRegister = findViewById(R.id.gotoRegister);
        btnRegister.setOnClickListener(this);
        databaseService.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tomainbtn)
        {
            Intent go = new Intent(this, MainActivity.class);
            startActivity(go);
        }
        if(view.getId() == btnLogin.getId()){
            Log.d(TAG, "onClick: Login button clicked");

            /// get the email and password entered by the user
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("email", email);
            editor.putString("password", password);

            editor.commit();

            /// log the email and password
            Log.d(TAG, "onClick: Email: " + email);
            Log.d(TAG, "onClick: Password: " + password);

            Log.d(TAG, "onClick: Validating input...");
            /// Validate input


            Log.d(TAG, "onClick: Logging in user...");

            /// Login user
            loginUser(email, password);
        } else if (view.getId() == btnRegister.getId()) {
            /// Navigate to Register Activity
            Intent registerIntent = new Intent(LogIn.this, Register.class);
            startActivity(registerIntent);

        }
    }

    private void loginUser(String email, String password) {
        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            /// Callback method called when the operation is completed
            @Override
            public void onCompleted(String  uid) {
                Log.d(TAG, "onCompleted: User logged in: " + uid.toString());
                /// save the user data to shared preferences
                // SharedPreferencesUtil.saveUser(LoginActivity.this, user);
                /// Redirect to main activity and clear back stack to prevent user from going back to login screen
                Intent mainIntent = new Intent(LogIn.this, MainActivity.class);
                /// Clear the back stack (clear history) and start the MainActivity
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: Failed to retrieve user data", e);
                /// Show error message to user
                etPassword.setError("Invalid email or password");
                etPassword.requestFocus();
                /// Sign out the user if failed to retrieve user data
                /// This is to prevent the user from being logged in again
                ///SharedPreferencesUtil.signOutUser(LogIn.this);
            }
        });
    }

}