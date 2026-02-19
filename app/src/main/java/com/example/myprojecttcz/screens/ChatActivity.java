package com.example.myprojecttcz.screens;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.MessageAdapter;
import com.example.myprojecttcz.base.BaseActivity;
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

    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;

    private MessageAdapter messageAdapter;
    private List<Message> mMessages;

    private String currentUserId;
    private String chatId; // ה-ID של הצ'אט או הפורום שאנחנו נמצאים בו כרגע

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // תצטרך ליצור קובץ כזה עם RecyclerView, EditText וכפתור

        recyclerView = findViewById(R.id.recycler_view_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true); // מתחיל מההודעה האחרונה למטה
        recyclerView.setLayoutManager(linearLayoutManager);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // מקבלים את ה-ChatId מהמסך הקודם דרך Intent
        chatId = getIntent().getStringExtra("CHAT_ID");

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString();
            if (!msg.equals("")) {
                sendMessage(currentUserId, chatId, msg);
            } else {
                Toast.makeText(ChatActivity.this, "אי אפשר לשלוח הודעה ריקה", Toast.LENGTH_SHORT).show();
            }
            etMessage.setText("");
        });

        readMessages();
    }

    private void sendMessage(String sender, String chatRoomId, String messageContent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Message newMessage = new Message();
        newMessage.setId(reference.push().getKey()); // יצירת מזהה ייחודי להודעה
        newMessage.setSenderId(sender);
        newMessage.setReceiverId(chatRoomId); // במקרה זה ה-Receiver הוא בעצם חדר הצ'אט
        newMessage.setContent(messageContent);
        newMessage.setTimestamp(System.currentTimeMillis());

        // שומרים את ההודעה תחת הצ'אט הספציפי במסד הנתונים
        reference.child("Chats").child(chatRoomId).child("messages").child(newMessage.getId()).setValue(newMessage);
    }

    private void readMessages() {
        mMessages = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(chatId).child("messages");

        // מאזינים לכל שינוי או הודעה חדשה
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}