package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myprojecttcz.services.DatabaseService;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.UsersListAdapter;
import com.example.myprojecttcz.model.User;

import java.util.List;

public class UsersTable extends AppCompatActivity {
    private static final String TAG = "UsersListActivity";
    private UsersListAdapter userAdapter;
    private TextView tvUserCount;
    DatabaseService databaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_table);

        databaseService = DatabaseService.getInstance();

        RecyclerView usersList = findViewById(R.id.rv_users_list);
        tvUserCount = findViewById(R.id.tv_user_count);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UsersListAdapter(new UsersListAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                // Handle user click
                Log.d(TAG, "User clicked: " + user);
                Intent intent = new Intent(UsersTable.this, MainActivity.class);
                intent.putExtra("USER_UID", user.getId());
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(User user) {
                // Handle long user click
                Log.d(TAG, "User long clicked: " + user);
            }
        });
        usersList.setAdapter(userAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        databaseService.getUserList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<User> users) {
                userAdapter.setUserList(users);
                tvUserCount.setText("Total users: " + users.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
            }
        });
    }



}