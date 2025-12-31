package com.example.myprojecttcz.screens;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapter.UsersListAdapter;
import com.example.myprojecttcz.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersTable extends AppCompatActivity {

    // רכיבי תצוגה
    private EditText etSearch;
    private Spinner spinnerFilter;
    private ListView listView;
    private TextView tvEmpty;

    // נתונים
    private ArrayList<User> allUsersList;   // הרשימה המקורית המלאה
    private ArrayList<User> displayList;    // הרשימה המוצגת (אחרי סינון)
    private UsersListAdapter adapter;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_table);
        initView();
    }
    public void initView(){

        // 1. אתחול רכיבים
        etSearch = findViewById(R.id.etSearchUser);
        spinnerFilter = findViewById(R.id.spinnerRoleFilter);
        listView = findViewById(R.id.usersListView);
        tvEmpty = findViewById(R.id.tvEmptyState);

        // 2. הגדרת הרשימות והאדפטר
        allUsersList = new ArrayList<>();
        displayList = new ArrayList<>();

        // אנחנו מחברים לאדפטר את רשימת התצוגה (displayList)
        adapter = new UsersListAdapter(this, displayList);
        listView.setAdapter(adapter);

        // 3. הגדרת הספינר (מסנן לפי תפקיד)
        String[] filters = {"הכל", "מנהלים", "שחקנים"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        // 4. חיבור ל-Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        fetchUsers();

        // 5. הגדרת מאזינים (Listeners) למסננים
        setupFilters();
    }
    private void fetchUsers() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsersList.clear(); // מנקים את המקור
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setId(postSnapshot.getKey());
                        allUsersList.add(user);
                    }
                }
                // אחרי שקיבלנו נתונים חדשים, מפעילים את הסינון (שגם יציג אותם)
                filterUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersTable.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilters() {
        // מאזין לשינוי טקסט בחיפוש
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(); // קוראים לפונקציית הסינון בכל שינוי אות
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // מאזין לבחירה בספינר
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterUsers(); // קוראים לפונקציית הסינון כשמשנים קטגוריה
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // הפונקציה המרכזית שמסננת את המידע
    private void filterUsers() {
        String searchText = etSearch.getText().toString().toLowerCase().trim();
        String selectedFilter = spinnerFilter.getSelectedItem().toString();

        displayList.clear(); // מנקים את רשימת התצוגה

        for (User user : allUsersList) {
            // בדיקה 1: האם השם מתאים לחיפוש? (אם החיפוש ריק - מתאים להכל)
            boolean nameMatch = user.getFname().toLowerCase().contains(searchText) ||
                    user.getLname().toLowerCase().contains(searchText);

            // בדיקה 2: האם התפקיד מתאים למה שנבחר בספינר?
            boolean roleMatch = true;
            if (selectedFilter.equals("מנהלים")) {
                roleMatch = user.isIsadmin(); // בודק אם הוא אדמין
            } else if (selectedFilter.equals("שחקנים")) {
                roleMatch = !user.isIsadmin(); // בודק אם הוא לא אדמין
            }

            // אם שתי הבדיקות עברו - מוסיפים לרשימת התצוגה
            if (nameMatch && roleMatch) {
                displayList.add(user);
            }
        }

        // מעדכנים את המסך
        adapter.notifyDataSetChanged();

        // הצגת הודעה אם הרשימה ריקה
        if (displayList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}