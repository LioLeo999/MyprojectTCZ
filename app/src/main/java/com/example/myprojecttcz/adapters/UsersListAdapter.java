package com.example.myprojecttcz.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private final List<User> userList;
    private final OnUserClickListener onUserClickListener;
    private Context context;

    public UsersListAdapter(@Nullable final OnUserClickListener onUserClickListener) {
        this.userList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UsersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        // הצגת נתוני המשתמש
        holder.tvuname.setText(user.getUname());
        holder.tvfName.setText(user.getFname());
        holder.tvlName.setText(user.getLname());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());

        // הגדרת ראשי תיבות לעיגול התמונה
        String initials = "";
        if (user.getFname() != null && !user.getFname().isEmpty()) initials += user.getFname().charAt(0);
        if (user.getLname() != null && !user.getLname().isEmpty()) initials += user.getLname().charAt(0);
        holder.tvInitials.setText(initials.toUpperCase());

        // ניהול תצוגת תג ה-Admin
        if (user.isadmin()) {
            holder.chipRole.setVisibility(View.VISIBLE);
            holder.chipRole.setText("Admin");
        } else {
            holder.chipRole.setVisibility(View.GONE);
        }

        // לחיצה רגילה (למשל למעבר לפרופיל)
        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) onUserClickListener.onUserClick(user);
        });

        // לחיצה ארוכה - פתיחת תפריט ניהול
        holder.itemView.setOnLongClickListener(v -> {
            showManagementDialog(user, position);
            return true;
        });
    }

    private void showManagementDialog(User user, int position) {
        String adminAction = user.isadmin() ? "בטל הרשאת אדמין" : "הפוך לאדמין";
        String[] options = {adminAction, "מחק משתמש", "ביטול"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ניהול משתמש: " + user.getUname());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // שינוי סטטוס אדמין
                showConfirmDialog("האם לשנות את הרשאות הניהול של " + user.getUname() + "?", () -> {
                    boolean newStatus = !user.isadmin();
                    // שימוש בפונקציית עדכון ב-DatabaseService
                    DatabaseService.getInstance().updateUserAdminStatus(user.getId(), newStatus, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public User onCompleted(Void object) {
                            user.setIsadmin(newStatus);
                            notifyItemChanged(position);
                            Toast.makeText(context, "ההרשאות עודכנו", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(context, "שגיאה בעדכון", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else if (which == 1) { // מחיקת משתמש
                showConfirmDialog("האם אתה בטוח שברצונך למחוק את המשתמש? פעולה זו סופית.", () -> {
                    DatabaseService.getInstance().deleteUser(user.getId(), new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public User onCompleted(Void object) {
                            userList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "משתמש נמחק", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        @Override
                        public void onFailed(Exception e) {
                            Toast.makeText(context, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
        builder.show();
    }

    // פונקציית עזר להצגת דיאלוג אישור לפני פעולה
    private void showConfirmDialog(String message, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("כן", (d, w) -> onConfirm.run())
                .setNegativeButton("לא", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvuname, tvfName, tvlName, tvEmail, tvPhone, tvInitials;
        Chip chipRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvuname = itemView.findViewById(R.id.tv_item_user_name);
            tvfName = itemView.findViewById(R.id.tv_item_first_name);
            tvlName = itemView.findViewById(R.id.tv_item_last_name);
            tvEmail = itemView.findViewById(R.id.tv_item_user_email);
            tvPhone = itemView.findViewById(R.id.tv_item_user_phone);
            tvInitials = itemView.findViewById(R.id.tv_user_initials);
            chipRole = itemView.findViewById(R.id.chip_user_role);
        }
    }
}