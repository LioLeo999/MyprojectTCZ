package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.screens.LogIn;
import com.example.myprojecttcz.services.DatabaseService;
import com.example.myprojecttcz.utils.SharedPreferencesUtil;
import com.example.myprojecttcz.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;


public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserFirstName, etUserLastName, etUserEmail, etUserPhone, etUserPassword, etUsername;
    private TextView tvUserDisplayName, tvUserDisplayEmail;
    private Button btnUpdateProfile, btnSignOut;
    private View adminBadge;
    String selectedUid="";
    User selectedUser;
    User currentUser=new User();
    boolean isCurrentUser = false;
    DatabaseService databaseService;
    Validator validator;
    private FirebaseAuth mAuth;
    String currentId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseService = DatabaseService.getInstance();
        validator = new Validator();

        selectedUid = getIntent().getStringExtra("USER_UID");// admin only

        mAuth=FirebaseAuth.getInstance();//Login

        if (selectedUid.equals("")){
            currentId = mAuth.getUid();
        }
        else{
            currentId = selectedUid;
        }
        databaseService.getUser(currentId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                currentUser=user;
                // Toast.makeText(UserProfile.this, user.toString(),LENGTH_LONG).show();



                if (!currentUser.isadmin()) {
                    Toast.makeText(UserProfile.this, "You are not authorized to view this profile", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailed(Exception e) {

                Toast.makeText(UserProfile.this, "User not logged in", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserProfile.this, LogIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            }
        });






        Log.d(TAG, "Selected user: " + selectedUid);

        // Initialize the EditText fields
        etUsername = findViewById(R.id.et_user_user_name);
        etUserFirstName = findViewById(R.id.et_user_first_name);
        etUserLastName = findViewById(R.id.et_user_last_name);
        etUserEmail = findViewById(R.id.et_user_email);
        etUserPhone = findViewById(R.id.et_user_phone);
        etUserPassword = findViewById(R.id.et_user_password);
        tvUserDisplayName = findViewById(R.id.tv_user_display_name);
        tvUserDisplayEmail = findViewById(R.id.tv_user_display_email);
        btnUpdateProfile = findViewById(R.id.btn_edit_profile);
        btnSignOut = findViewById(R.id.btn_sign_out);
        adminBadge = findViewById(R.id.admin_badge);

        btnUpdateProfile.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        if (!isCurrentUser) {
            btnSignOut.setVisibility(View.GONE);
        }

        showUserProfile();
    }





    private void showUserProfile() {
        databaseService.getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user == null) {
                    Log.e(TAG, "User not found in database");
                    Toast.makeText(UserProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                selectedUser = user;
                etUsername.setText(user.getUname());
                etUserFirstName.setText(user.getFname());
                etUserLastName.setText(user.getLname());
                etUserEmail.setText(user.getEmail());
                etUserPhone.setText(user.getPhone());
                etUserPassword.setText(user.getPassword());

                String displayName = user.getFname() + " " + user.getLname();
                tvUserDisplayName.setText(displayName);
                tvUserDisplayEmail.setText(user.getEmail());

                if (user.isadmin()) {
                    adminBadge.setVisibility(View.VISIBLE);
                    Log.d(TAG, "User is admin, showing admin badge");
                } else {
                    adminBadge.setVisibility(View.GONE);
                    Log.d(TAG, "User is not admin, hiding admin badge");
                }

                boolean canEdit = isCurrentUser || currentUser.isadmin();
                Log.d(TAG,canEdit+" ");
                etUsername.setEnabled(canEdit);
                etUserFirstName.setEnabled(canEdit);
                etUserLastName.setEnabled(canEdit);
                etUserEmail.setEnabled(canEdit);
                etUserPhone.setEnabled(canEdit);
                etUserPassword.setEnabled(canEdit);
                btnUpdateProfile.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
            }
        });
    }

    private void updateUserProfile() {
        if (selectedUser == null) {
            Log.e(TAG, "User not found");
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isCurrentUser && !currentUser.isadmin()) {
            Log.e(TAG, "User not authorized to update this profile");
            Toast.makeText(this, "You are not authorized to update this profile", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = etUserFirstName.getText().toString();
        String lastName = etUserLastName.getText().toString();
        String phone = etUserPhone.getText().toString();
        String email = etUserEmail.getText().toString();
        String password = etUserPassword.getText().toString();

        if (!isValid(firstName, lastName, phone, email, password)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        selectedUser.setFname(firstName);
        selectedUser.setLname(lastName);
        selectedUser.setPhone(phone);
        selectedUser.setEmail(email);
        selectedUser.setPassword(password);

        Log.d(TAG, "Updating user profile");
        Log.d(TAG, "Selected user UID: " + selectedUser.getId());
        Log.d(TAG, "Is current user: " + isCurrentUser);
        Log.d(TAG, "User email: " + selectedUser.getEmail());
        Log.d(TAG, "User password: " + selectedUser.getPassword());



        updateUserInDatabase(selectedUser);
    }

    private void updateUserInDatabase(User user) {
        Log.d(TAG, "Updating user in database: " + user.getId());
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Log.d(TAG, "User profile updated successfully");
                Toast.makeText(UserProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                showUserProfile();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating user profile", e);
                Toast.makeText(UserProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String firstName, String lastName, String phone, String email, String password) {
        if (!validator.isNameValid(firstName)) {
            etUserFirstName.setError("First name is required");
            etUserFirstName.requestFocus();
            return false;
        }
        if (!validator.isNameValid(lastName)) {
            etUserLastName.setError("Last name is required");
            etUserLastName.requestFocus();
            return false;
        }
        if (!validator.isPhoneValid(phone)) {
            etUserPhone.setError("Phone number is required");
            etUserPhone.requestFocus();
            return false;
        }
        if (!validator.isEmailValid(email)) {
            etUserEmail.setError("Email is required");
            etUserEmail.requestFocus();
            return false;
        }
        if (!validator.isPasswordValid(password)) {
            etUserPassword.setError("Password is required");
            etUserPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void signOut() {
        Log.d(TAG, "Sign out button clicked");
        SharedPreferencesUtil.signOutUser(UserProfile.this);

        Log.d(TAG, "User signed out, redirecting to MainActivity");
        Intent landingIntent = new Intent(UserProfile.this, MainActivity.class);
        landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(landingIntent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_edit_profile) {
            updateUserProfile();
            return;
        }
        if(v.getId() == R.id.btn_sign_out) {
            signOut();
        }
    }
}
