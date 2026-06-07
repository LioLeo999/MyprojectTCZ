package com.example.myprojecttcz.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.MessageAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Chat;
import com.example.myprojecttcz.model.Message;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity {
    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvChatTitleTop;

    private MessageAdapter messageAdapter;
    private List<Message> mMessages;

    private String currentUserId;
    private String chatId;
    private DatabaseService databaseService;

    // משתנה ששומר את נתוני הצ'אט כדי שנדע מי המשתתפים בו
    private Chat currentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        databaseService = DatabaseService.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatId = getIntent().getStringExtra("CHAT_ID");

        recyclerView = findViewById(R.id.recycler_view_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        tvChatTitleTop = findViewById(R.id.tv_chat_title_top);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadChatTitle();
        readMessages();

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                btnSend.setEnabled(false); // מניעת לחיצות כפולות
                sendMessage(currentUserId, chatId, msg);
                etMessage.setText("");
                btnSend.setEnabled(true);
            } else {
                Toast.makeText(ChatActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatTitle() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentChat = snapshot.getValue(Chat.class);
                if (currentChat != null) {
                    if (currentChat.isForum()) {
                        tvChatTitleTop.setText("Forum: " + currentChat.getTitle());
                    } else {
                        tvChatTitleTop.setText(currentChat.getTitle() != null && !currentChat.getTitle().isEmpty() ? currentChat.getTitle() : "Private Chat");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage(String sender, String chatRoomId, String messageContent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Message newMessage = new Message();
        newMessage.setId(reference.push().getKey());
        newMessage.setSenderId(sender);

        // --- תיקון חשוב: מציאת מזהה המשתמש השני (המקבל) כדי שהשרת ידע למי לשלוח התראה ---
        String receiverId = "";
        if (currentChat != null && currentChat.getMembers() != null) {
            for (String memberId : currentChat.getMembers()) {
                if (!memberId.equals(currentUserId)) {
                    receiverId = memberId;
                    break;
                }
            }
        }
        newMessage.setReceiverId(receiverId);
        // -------------------------------------------------------------------------

        newMessage.setContent(messageContent);
        long currentTime = System.currentTimeMillis();
        newMessage.setTimestamp(currentTime);

        // שמירת ההודעה במסד הנתונים (השרת יזהה את זה אוטומטית וישלח התראה ל-receiverId)
        reference.child("Chats").child(chatRoomId).child("messages").child(newMessage.getId()).setValue(newMessage);
        reference.child("Chats").child(chatRoomId).child("lastMessageTime").setValue(currentTime);
    }

    private void readMessages() {
        mMessages = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(chatId).child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    mMessages.add(message);
                }
                messageAdapter = new MessageAdapter(ChatActivity.this, mMessages);
                recyclerView.setAdapter(messageAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
