package com.example.myprojecttcz.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.ChatListAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Chat;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton fabAddChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);

        recyclerView = findViewById(R.id.recycler_view_chats_list);
        fabAddChat = findViewById(R.id.fab_add_chat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatList = new ArrayList<>();

        readChats();

        fabAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddChatDialog();
            }
        });
    }

    private void showAddChatDialog() {
        DatabaseService.getInstance().getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public User onCompleted(List<User> allUsers) {
                List<User> otherUsers = new ArrayList<>();
                List<String> userNames = new ArrayList<>();

                // מסננים החוצה את המשתמש הנוכחי
                for (User u : allUsers) {
                    if (!u.getId().equals(currentUserId)) {
                        otherUsers.add(u);
                        userNames.add(u.getUname() != null ? u.getUname() : "משתמש " + u.getId().substring(0,4));
                    }
                }

                // יצירת מבנה הדיאלוג
                LinearLayout layout = new LinearLayout(ChatsListActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 40, 50, 10);

                // שדה טקסט לשם השיחה
                final EditText titleBox = new EditText(ChatsListActivity.this);
                titleBox.setHint("הכנס שם לשיחה...");
                layout.addView(titleBox);

                // רשימה נפתחת לבחירת משתמש
                final Spinner userSpinner = new Spinner(ChatsListActivity.this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatsListActivity.this, android.R.layout.simple_spinner_dropdown_item, userNames);
                userSpinner.setAdapter(adapter);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 30, 0, 0);
                userSpinner.setLayoutParams(params);

                layout.addView(userSpinner);

                // בניית הדיאלוג
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatsListActivity.this);
                builder.setTitle("צור צ'אט חדש");
                builder.setView(layout);

                builder.setPositiveButton("צור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String chatTitle = titleBox.getText().toString().trim();
                        int selectedPosition = userSpinner.getSelectedItemPosition();

                        if (chatTitle.isEmpty()) {
                            Toast.makeText(ChatsListActivity.this, "חובה להכניס שם לשיחה", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (selectedPosition != android.widget.AdapterView.INVALID_POSITION) {
                            User selectedUser = otherUsers.get(selectedPosition);
                            createNewPrivateChat(selectedUser, chatTitle);
                        }
                    }
                });

                builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ChatsListActivity.this, "שגיאה בטעינת משתמשים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewPrivateChat(User targetUser, String title) {
        String newChatId = DatabaseService.getInstance().generateChatId();

        List<String> members = new ArrayList<>();
        members.add(currentUserId);
        members.add(targetUser.getId());

        Chat newChat = new Chat(newChatId, members, false, title);

        DatabaseService.getInstance().createNewChat(newChat, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                Intent intent = new Intent(ChatsListActivity.this, ChatActivity.class);
                intent.putExtra("CHAT_ID", newChatId);
                startActivity(intent);
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ChatsListActivity.this, "שגיאה ביצירת צ'אט", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readChats() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null) {
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