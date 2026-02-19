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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        // נטען קובץ עיצוב שונה בהתאם לשולח
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
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView showMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            // נניח שיש לך TextView בשם text_message_body בשני קובצי ה-XML
            showMessage = itemView.findViewById(R.id.text_message_body);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        // אם ה-ID של שולח ההודעה זהה ל-ID שלי, זו הודעה ימנית (שלי)
        if (mMessages.get(position).getSenderId().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}