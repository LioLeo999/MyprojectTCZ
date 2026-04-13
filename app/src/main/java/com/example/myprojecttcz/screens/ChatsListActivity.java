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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private List<Chat> chatList;
    private String currentUserId;
    private FloatingActionButton fabAddChat;
    private FloatingActionButton fabAddForum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);

        recyclerView = findViewById(R.id.recycler_view_chats_list);
        fabAddChat = findViewById(R.id.fab_add_chat);
        fabAddForum = findViewById(R.id.fab_add_forum);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatList = new ArrayList<>();

        readChats();
        checkIfAdmin();

        fabAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddChatDialog();
            }
        });

        fabAddForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddForumDialog();
            }
        });
    }

    private void checkIfAdmin() {
        DatabaseService.getInstance().getUser(currentUserId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User currentUser) {
                if (currentUser != null && currentUser.isadmin()) {
                    fabAddForum.setVisibility(View.VISIBLE);
                }
                return currentUser;
            }

            @Override
            public void onFailed(Exception e) {
                // התעלם בשקט במקרה של שגיאה
            }
        });
    }

    private void showAddForumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatsListActivity.this);
        builder.setTitle("Create a new general forum");

        LinearLayout layout = new LinearLayout(ChatsListActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText titleBox = new EditText(ChatsListActivity.this);
        titleBox.setHint("Enter the forum name");
        layout.addView(titleBox);

        builder.setView(layout);

        builder.setPositiveButton("Create forum", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String forumTitle = titleBox.getText().toString().trim();

                if (forumTitle.isEmpty()) {
                    Toast.makeText(ChatsListActivity.this, "You have to enter a forum name", Toast.LENGTH_SHORT).show();
                    return;
                }

                createNewForum(forumTitle);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void createNewForum(String title) {
        String newChatId = DatabaseService.getInstance().generateChatId();

        List<String> members = new ArrayList<>();

        Chat newForum = new Chat(newChatId, members, true, title);
        newForum.setLastMessageTime(System.currentTimeMillis());

        DatabaseService.getInstance().createNewChat(newForum, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                Intent intent = new Intent(ChatsListActivity.this, ChatActivity.class);
                intent.putExtra("CHAT_ID", newChatId);
                startActivity(intent);
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ChatsListActivity.this, "Error in creating forum", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddChatDialog() {
        DatabaseService.getInstance().getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public User onCompleted(List<User> allUsers) {
                List<User> otherUsers = new ArrayList<>();
                List<String> userNames = new ArrayList<>();
                String currentUserName = "Me";

                for (User u : allUsers) {
                    if (u.getId().equals(currentUserId)) {
                        currentUserName = u.getUname() != null ? u.getUname() : "User " + u.getId().substring(0, 4);
                    } else {
                        otherUsers.add(u);
                        userNames.add(u.getUname() != null ? u.getUname() : "User " + u.getId().substring(0, 4));
                    }
                }

                final String finalCurrentUserName = currentUserName;

                LinearLayout layout = new LinearLayout(ChatsListActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 40, 50, 10);

                final Spinner userSpinner = new Spinner(ChatsListActivity.this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatsListActivity.this, android.R.layout.simple_spinner_dropdown_item, userNames);
                userSpinner.setAdapter(adapter);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 0);
                userSpinner.setLayoutParams(params);

                layout.addView(userSpinner);

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatsListActivity.this);
                builder.setTitle("Create a new private chat");
                builder.setView(layout);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = userSpinner.getSelectedItemPosition();

                        if (selectedPosition != android.widget.AdapterView.INVALID_POSITION) {
                            User selectedUser = otherUsers.get(selectedPosition);
                            String targetUserName = userNames.get(selectedPosition);

                            String chatTitle = finalCurrentUserName + " & " + targetUserName;
                            createNewPrivateChat(selectedUser, chatTitle);
                        } else {
                            Toast.makeText(ChatsListActivity.this, "Please select a user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                Toast.makeText(ChatsListActivity.this, "Error in loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewPrivateChat(User targetUser, String title) {
        String newChatId = DatabaseService.getInstance().generateChatId();

        List<String> members = new ArrayList<>();
        members.add(currentUserId);
        members.add(targetUser.getId());

        Chat newChat = new Chat(newChatId, members, false, title);
        newChat.setLastMessageTime(System.currentTimeMillis());

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
                Toast.makeText(ChatsListActivity.this, "Error in creating chat", Toast.LENGTH_SHORT).show();
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

                Collections.sort(chatList, new Comparator<Chat>() {
                    @Override
                    public int compare(Chat chat1, Chat chat2) {
                        return Long.compare(chat2.getLastMessageTime(), chat1.getLastMessageTime());
                    }
                });

                // כאן הוספנו את המאזין החדש ללחיצה ארוכה
                chatListAdapter = new ChatListAdapter(ChatsListActivity.this, chatList, new ChatListAdapter.OnChatLongClickListener() {
                    @Override
                    public void onChatLongClick(Chat chat) {
                        handleChatDeletion(chat);
                    }
                });
                recyclerView.setAdapter(chatListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // פונקציה חדשה המטפלת בלוגיקת המחיקה ובדיקת ההרשאות
    private void handleChatDeletion(Chat chat) {
        DatabaseService.getInstance().getUser(currentUserId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User currentUser) {
                boolean isAdmin = currentUser != null && currentUser.isadmin();

                // חסימה: אם זה פורום והמשתמש אינו מנהל
                if (chat.isForum() && !isAdmin) {
                    Toast.makeText(ChatsListActivity.this, "Only admins can delete forums", Toast.LENGTH_SHORT).show();
                    return currentUser;
                }

                // אם הבדיקה עברה, נציג דיאלוג אישור
                String type = chat.isForum() ? "forum" : "chat";
                new AlertDialog.Builder(ChatsListActivity.this)
                        .setTitle("Delete " + type)
                        .setMessage("Are you sure you want to delete this " + type + "?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteChatFromDb(chat.getId());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                return currentUser;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ChatsListActivity.this, "Failed to verify permissions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // פונקציה חדשה שקוראת ל-DatabaseService כדי למחוק את הצ'אט
    private void deleteChatFromDb(String chatId) {
        DatabaseService.getInstance().deleteChat(chatId, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                Toast.makeText(ChatsListActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ChatsListActivity.this, "Error deleting", Toast.LENGTH_SHORT).show();
            }
        });
    }
}