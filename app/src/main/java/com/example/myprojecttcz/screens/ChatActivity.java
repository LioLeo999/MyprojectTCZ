package com.example.myprojecttcz.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.MessageAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Chat;
import com.example.myprojecttcz.model.Message;
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
    private TextView tvChatTitleTop; // המשתנה לכותרת

    private MessageAdapter messageAdapter;
    private List<Message> mMessages;

    private String currentUserId;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        tvChatTitleTop = findViewById(R.id.tv_chat_title_top); // חיבור הכותרת

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatId = getIntent().getStringExtra("CHAT_ID");

        // קריאה לפונקציות טעינת הנתונים
        loadChatTitle();
        readMessages();

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString();
            if (!msg.equals("")) {
                sendMessage(currentUserId, chatId, msg);
            } else {
                Toast.makeText(ChatActivity.this, "You can't send an epmty message", Toast.LENGTH_SHORT).show();
            }
            etMessage.setText("");
        });
    }

    // פונקציה למשיכת השם של הצ'אט
    private void loadChatTitle() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                if (chat != null) {
                    if (chat.isForum()) {
                        tvChatTitleTop.setText("Forum: " + chat.getTitle());
                    } else {
                        if (chat.getTitle() != null && !chat.getTitle().isEmpty()) {
                            tvChatTitleTop.setText(chat.getTitle());
                        } else {
                            tvChatTitleTop.setText("Private Chat");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendMessage(String sender, String chatRoomId, String messageContent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Message newMessage = new Message();
        newMessage.setId(reference.push().getKey());
        newMessage.setSenderId(sender);
        newMessage.setReceiverId(chatRoomId);
        newMessage.setContent(messageContent);

        // שומרים את הזמן הנוכחי במשתנה כדי להשתמש בו פעמיים
        long currentTime = System.currentTimeMillis();
        newMessage.setTimestamp(currentTime);

        // 1. שומרים את ההודעה החדשה תחת ענף ההודעות של הצ'אט
        reference.child("Chats").child(chatRoomId).child("messages").child(newMessage.getId()).setValue(newMessage);

        // 2. השלב החסר - מעדכנים את זמן ההודעה האחרונה של הצ'אט עצמו כדי שהמיון יעבוד!
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
                    Log.d(TAG, "Time:" + message.getTimestamp());

                }
                messageAdapter = new MessageAdapter(ChatActivity.this, mMessages);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}