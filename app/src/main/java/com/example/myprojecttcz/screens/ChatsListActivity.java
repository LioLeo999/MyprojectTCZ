package com.example.myprojecttcz.screens;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.ChatListAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private List<Chat> chatList;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list); // תצטרך ליצור XML עם RecyclerView בלבד

        recyclerView = findViewById(R.id.recycler_view_chats_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatList = new ArrayList<>();

        readChats();
    }

    private void readChats() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();

                // עוברים על כל הצ'אטים שיש ב-DB
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null) {
                        // מוסיפים לרשימה רק אם זה פורום שפתוח לכולם,
                        // או שהמשתמש הנוכחי נמצא ברשימת ה-members של הצ'אט הפרטי
                        if (chat.isForum() || (chat.getMembers() != null && chat.getMembers().contains(currentUserId))) {
                            chatList.add(chat);
                        }
                    }
                }

                chatListAdapter = new ChatListAdapter(ChatsListActivity.this, chatList);
                recyclerView.setAdapter(chatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}