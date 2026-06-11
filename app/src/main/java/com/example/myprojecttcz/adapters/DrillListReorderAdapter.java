package com.example.myprojecttcz.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.screens.ShowDrill;

import java.util.Collections;
import java.util.List;

public class DrillListReorderAdapter extends RecyclerView.Adapter<DrillListReorderAdapter.DrillViewHolder> {

    private Context context;
    private List<Drill2v> drills;
    private OnDrillDeleteListener deleteListener; // הוספת המאזין למחיקה

    // ממשק שיאפשר לאקטיביטי לטפל במחיקה
    public interface OnDrillDeleteListener {
        void onDeleteClick(int position);
    }

    // עדכון הבנאי כך שיקבל גם את המאזין
    public DrillListReorderAdapter(Context context, List<Drill2v> drills, OnDrillDeleteListener deleteListener) {
        this.context = context;
        this.drills = drills;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public DrillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drill_full_width, parent, false);
        return new DrillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrillViewHolder holder, int position) {
        Drill2v drill = drills.get(position);

        holder.tvDrillName.setText(drill.getName());

        // לחיצה למעבר לעמוד הדריל הספציפי
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowDrill.class);
            intent.putExtra("id", drill.getId());
            context.startActivity(intent);
        });

        // טיפול בלחיצה על כפתור הפח
        holder.ivDelete.setOnClickListener(v -> {
            // יצירת חלונית הדיאלוג
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Drill") // כותרת החלונית
                    .setMessage("Are you sure you want to delete this drill?") // הודעת האישור
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // רק אם המשתמש לחץ "Yes", נבצע את המחיקה
                        if (deleteListener != null) {
                            int currentPosition = holder.getAdapterPosition();
                            // מוודאים שהמיקום תקין ולא השתנה פתאום
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                deleteListener.onDeleteClick(currentPosition);
                            }
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // אם המשתמש לחץ "No", פשוט סוגרים את החלונית בלי לעשות כלום
                        dialog.dismiss();
                    })
                    .show(); // מציג את הדיאלוג על המסך
        });
    }


    @Override
    public int getItemCount() {
        return drills.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(drills, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<Drill2v> getDrills() {
        return drills;
    }

    static class DrillViewHolder extends RecyclerView.ViewHolder {
        TextView tvDrillName;
        ImageView ivDelete; // הוספת כפתור המחיקה

        public DrillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDrillName = itemView.findViewById(R.id.tvDrillName);
            ivDelete = itemView.findViewById(R.id.deleteDrillFromTrainigSet);
        }
    }
}