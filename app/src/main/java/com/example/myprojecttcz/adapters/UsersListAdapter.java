package com.example.myprojecttcz.adapters;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UsersListAdapter extends ArrayAdapter<User> {

    private Context context;
    private ArrayList<User> usersList;

    // בנאי (Constructor)
    public UsersListAdapter(Context context, ArrayList<User> list) {
        super(context, 0, list);
        this.context = context;
        this.usersList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 1. בדיקה אם צריך ליצור View חדש או למחזר קיים
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
        }

        // 2. שליפת המשתמש הנוכחי
        User currentUser = usersList.get(position);

        // 3. קישור לרכיבים ב-XML
        TextView tvName = listItem.findViewById(R.id.tvFullName);
        TextView tvEmail = listItem.findViewById(R.id.tvEmail);
        ImageButton btnDelete = listItem.findViewById(R.id.btnDelete);

        // 4. הצבת הנתונים (מניעת קריסה אם המשתמש null)
        if (currentUser != null) {
            String fullName = currentUser.getFname() + " " + currentUser.getLname();
            tvName.setText(fullName);
            tvEmail.setText(currentUser.getEmail());

            // בדיקה אם המשתמש הוא אדמין כדי להציג אייקון או צבע שונה (אופציונלי)
            if (currentUser.isIsadmin()) {
                tvName.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                tvName.setTextColor(context.getResources().getColor(android.R.color.black));
            }

            // 5. לוגיקה של כפתור המחיקה
            btnDelete.setOnClickListener(v -> {
                showDeleteConfirmationDialog(currentUser);
            });
        }

        return listItem;
    }

    // פונקציה שמציגה דיאלוג אישור לפני מחיקה (מומלץ מאוד!)
    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(context)
                .setTitle("מחיקת משתמש")
                .setMessage("האם אתה בטוח שברצונך למחוק את " + user.getFname() + "?")
                .setPositiveButton("כן, מחק", (dialog, which) -> {
                    deleteUserFromFirebase(user);
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    // הפונקציה שמוחקת בפועל מ-Firebase
    private void deleteUserFromFirebase(User user) {
        if (user.getId() == null) {
            Toast.makeText(context, "שגיאה: למשתמש אין מזהה (ID)", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(user.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "המשתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                    // הערה: אנחנו לא צריכים למחוק ידנית מה-usersList כאן,
                    // כי ה-Activity מאזין לשינויים ב-Firebase והוא יעדכן את הרשימה לבד.
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "נכשל במחיקה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
