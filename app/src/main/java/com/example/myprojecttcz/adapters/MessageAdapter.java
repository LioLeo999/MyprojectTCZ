package com.example.myprojecttcz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Message;
import com.example.myprojecttcz.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0; // הודעה נכנסת (של מישהו אחר)
    public static final int MSG_TYPE_RIGHT = 1; // הודעה יוצאת (שלך)

    private Context context;
    private List<Message> mMessages;
    private FirebaseUser fuser;

    public MessageAdapter(Context context, List<Message> mMessages) {
        this.context = context;
        this.mMessages = mMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.showMessage.setText(message.getContent());

        // אם מדובר בהודעה של מישהו אחר (צד שמאל), holder.senderName לא יהיה null
        if (holder.senderName != null) {
            // ניגשים לטבלת "Users" ב-Firebase לפי ה-senderId של ההודעה
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(message.getSenderId());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // מושכים את המשתמש מהדאטה בייס
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getUname() != null) {
                        // מציגים את ה-uname של המשתמש
                        holder.senderName.setText(user.getUname());
                    } else {
                        holder.senderName.setText("משתמש"); // ברירת מחדל אם אין שם
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView showMessage;
        public TextView senderName; // הוספנו משתנה לשם המשתמש

        public ViewHolder(View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.text_message_body);
            // מנסים למצוא את ה-TextView של השם. ב-chat_item_right זה פשוט יהיה null כי לא שמנו אותו שם.
            senderName = itemView.findViewById(R.id.tv_sender_name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessages.get(position).getSenderId().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}