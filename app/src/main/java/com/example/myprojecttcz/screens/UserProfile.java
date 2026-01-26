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
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.example.myprojecttcz.utils.Validator;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserFirstName, etUserLastName, etUserEmail, etUserPhone, etUserPassword, etUsername;
    private TextView tvUserDisplayName, tvUserDisplayEmail;
    private Button btnUpdateProfile;
    private View adminBadge;
    String selectedUid = "";
    User selectedUser;
    User currentUser = new User();
    boolean isCurrentUser;
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

        initViews();
        getUserData();

        Log.d(TAG, "Selected user: " + selectedUid);

        showUserProfile();
        // הערה: מחקתי מפה את ה-setEnabled כי זה חייב לקרות בתוך showUserProfile אחרי שהמידע נטען
    }

    public void initViews() {
        databaseService = DatabaseService.getInstance();
        validator = new Validator();
        mAuth = FirebaseAuth.getInstance();

        selectedUid = getIntent().getStringExtra("USER_UID"); // admin only
        if (selectedUid == null) {
            selectedUid = "";
        }

        if (selectedUid.equals("")) { // אם מגיע מהמשתמש עצמו
            String myId = mAuth.getUid();
            selectedUid = myId;
            currentId = myId;
            isCurrentUser = true;
        } else if (selectedUid.equals(mAuth.getUid())) { // אם המשתמש שהתקבל הוא האדמין שמחובר
            currentId = selectedUid;
            isCurrentUser = true;
        } else { // אם זה משתמש שמחובר שנכנס לא דרך הטבלה
            currentId = selectedUid;
            isCurrentUser = false; // שים לב: כאן זה כנראה לא המשתמש הנוכחי אלא אדמין שצופה באחר
        }

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
        adminBadge = findViewById(R.id.admin_badge);

        btnUpdateProfile.setOnClickListener(this);
    }

    private void getUserData() {
        // משיג את המשתמש הנוכחי שמחובר לאפליקציה (כדי לבדוק אם הוא אדמין)
        String uid = mAuth.getUid();
        if(uid == null) {
            // טיפול במקרה של ניתוק פתאומי
            return;
        }

        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User user) {
                currentUser = user;
                // קריאה חוזרת ל-showUserProfile כדי לעדכן הרשאות אם הנתונים הגיעו אחרי
                if(selectedUser != null) {
                    // רענון כפתורים אם המסך כבר נטען
                    boolean canEdit = isCurrentUser || (currentUser != null && currentUser.isadmin());
                    btnUpdateProfile.setVisibility(canEdit ? View.VISIBLE : View.GONE);
                }
                return user;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UserProfile.this, "User not logged in", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserProfile.this, LogIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showUserProfile() {
        databaseService.getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User user) {
                if (user == null) {
                    Log.e(TAG, "User not found in database");
                    Toast.makeText(UserProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return user;
                }
                selectedUser = user;

                // הצבת הנתונים
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
                } else {
                    adminBadge.setVisibility(View.GONE);
                }

                // בדיקת הרשאות עריכה
                boolean canEdit = isCurrentUser || (currentUser != null && currentUser.isadmin());

                // הגדרת שדות ברי עריכה
                etUsername.setEnabled(canEdit);
                etUserFirstName.setEnabled(canEdit);
                etUserLastName.setEnabled(canEdit);
                etUserPhone.setEnabled(canEdit);

                // --- כאן התיקון החשוב ---
                // אימייל וסיסמה נעולים תמיד
                etUserEmail.setEnabled(false);
                etUserPassword.setEnabled(false);

                // הופך אותם לקצת שקופים כדי שייראה ויזואלית שהם נעולים
                etUserEmail.setAlpha(0.6f);
                etUserPassword.setAlpha(0.6f);

                btnUpdateProfile.setVisibility(canEdit ? View.VISIBLE : View.GONE);
                return user;
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
            }
        });
    }

    private void updateUserProfile() {
        if (selectedUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isCurrentUser && (currentUser == null || !currentUser.isadmin())) {
            Toast.makeText(this, "You are not authorized to update this profile", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = etUserFirstName.getText().toString();
        String lastName = etUserLastName.getText().toString();
        String phone = etUserPhone.getText().toString();
        String userName = etUsername.getText().toString();

        // --- תיקון: לוקחים את האימייל והסיסמה המקוריים מהאובייקט, לא מהמסך ---
        String originalEmail = selectedUser.getEmail();
        String originalPassword = selectedUser.getPassword();

        // שולחים לוולידציה את הערכים המקוריים (כדי שהיא תעבור)
        if (!isValid(firstName, lastName, phone, originalEmail, originalPassword)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        // מעדכנים באובייקט רק את מה שמותר לשנות
        selectedUser.setUname(userName);
        selectedUser.setFname(firstName);
        selectedUser.setLname(lastName);
        selectedUser.setPhone(phone);

        // לא נוגעים באימייל ובסיסמה באובייקט - הם נשארים כמו שהיו

        Log.d(TAG, "Updating user profile in DB");
        updateUserInDatabase(selectedUser);
    }

    private void updateUserInDatabase(User user) {
        Log.d(TAG, "Updating user in database: " + user.getId());
        databaseService.updateUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void result) {
                Log.d(TAG, "User profile updated successfully");
                Toast.makeText(UserProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                // מרעננים את המסך כדי לוודא שהכל מוצג נכון
                showUserProfile();
                return null;
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
        // הבדיקות האלו יעברו תמיד כי אנחנו שולחים את המייל/סיסמה המקוריים והתקינים
        if (!validator.isEmailValid(email)) {
            etUserEmail.setError("Email is required");
            return false;
        }
        if (!validator.isPasswordValid(password)) {
            etUserPassword.setError("Password is required");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_edit_profile) {
            updateUserProfile();
        }
    }
}