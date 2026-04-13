package com.example.myprojecttcz.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Chat;
import com.example.myprojecttcz.screens.ChatActivity;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context context;
    private List<Chat> mChats;
    private OnChatLongClickListener longClickListener; // מאזין חדש ללחיצה ארוכה

    // ממשק (Interface) המאפשר ל-Activity לטפל בלוגיקת המחיקה
    public interface OnChatLongClickListener {
        void onChatLongClick(Chat chat);
    }

    // עדכון הבנאי כך שיקבל גם את המאזין
    public ChatListAdapter(Context context, List<Chat> mChats, OnChatLongClickListener longClickListener) {
        this.context = context;
        this.mChats = mChats;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChats.get(position);

        // הצגת כותרת הצ'אט/פורום
        if (chat.isForum()) {
            holder.chatTitle.setText("Forum: " + chat.getTitle());
        } else {
            if (chat.getTitle() != null && !chat.getTitle().isEmpty()) {
                holder.chatTitle.setText(chat.getTitle());
            } else {
                holder.chatTitle.setText("Private Chat");
            }
        }

        // לחיצה רגילה - כניסה לצ'אט
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("CHAT_ID", chat.getId());
            context.startActivity(intent);
        });

        // לחיצה ארוכה - קריאה למאזין שיטפל במחיקה
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onChatLongClick(chat);
            }
            return true; // החזרת true כדי לציין שהאירוע טופל
        });
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chatTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            chatTitle = itemView.findViewById(R.id.tv_chat_title);
        }
    }
}