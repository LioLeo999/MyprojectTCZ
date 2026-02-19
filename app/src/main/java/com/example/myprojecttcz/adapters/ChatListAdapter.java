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

    public ChatListAdapter(Context context, List<Chat> mChats) {
        this.context = context;
        this.mChats = mChats;
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

        // נציג את שם הצ'אט. אם זה פורום, נציג את הכותרת שלו.
        if (chat.isForum()) {
            holder.chatTitle.setText("פורום: " + chat.getTitle());
        } else {
            // אם זו שיחה פרטית, אפשר (בהמשך) לשלוף את שם המשתמש השני. בינתיים נכתוב:
            holder.chatTitle.setText("צ'אט פרטי");
        }

        // כשלוחצים על הצ'אט - עוברים למסך הצ'אט עצמו ומעבירים לו את ה-ID
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("CHAT_ID", chat.getId());
            context.startActivity(intent);
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
            chatTitle = itemView.findViewById(R.id.tv_chat_title); // צריך ליצור item_chat_list.xml עם TextView זה
        }
    }
}