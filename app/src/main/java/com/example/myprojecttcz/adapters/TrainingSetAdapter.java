package com.example.myprojecttcz.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.MaarachImun;
import com.example.myprojecttcz.screens.ShowTrainingSet;

import java.util.ArrayList;
import java.util.List;

public class TrainingSetAdapter extends RecyclerView.Adapter<TrainingSetAdapter.SetViewHolder> {

    private final Context context;
    private final List<MaarachImun> sets;
    private final OnMaarachClickListener listener; // הוספנו משתנה ל-Listener

    // 1. הגדרת הממשק ללחיצות
    public interface OnMaarachClickListener {
        void onMaarachClick(MaarachImun maarach);
        void onDeleteClick(MaarachImun maarach); // לחיצה ארוכה
    }

    // 2. עדכון הבנאי שיקבל גם את ה-Listener
    public TrainingSetAdapter(Context context, List<MaarachImun> sets, OnMaarachClickListener listener) {
        this.context = context;
        this.sets = sets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_trainingset, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        MaarachImun set = sets.get(position);

        // שם מערך האימון
        holder.tvSetName.setText(set.getName());

        // הגנה מ-null (חשוב מאוד עם Firebase)
        ArrayList<String> drillIds =
                set.getDrillsid() != null ? set.getDrillsid() : new ArrayList<>();

        // RecyclerView פנימי של drill IDs
        DrillMiniAdapter drillAdapter = new DrillMiniAdapter(drillIds);

        holder.rvDrills.setLayoutManager(new LinearLayoutManager(context));
        holder.rvDrills.setAdapter(drillAdapter);

        // --- לחיצה רגילה (קצרה): כניסה לעמוד העריכה המלא ---
        holder.itemView.setOnClickListener(v -> {
            // שומר על הקוד המקורי שלך!
            Intent intent = new Intent(context, ShowTrainingSet.class);
            intent.putExtra("maarach_id", set.getId()); // שליחת ה-ID
            context.startActivity(intent);

            // במקביל קורא גם ל-Listener ליתר ביטחון
            if (listener != null) {
                listener.onMaarachClick(set);
            }
        });

        // --- 3. לחיצה ארוכה: קריאה למחיקה ---
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(set);
            }
            return true; // מחזיר true כדי לא להפעיל גם את המעבר עמוד (הלחיצה הרגילה) בטעות
        });
    }

    @Override
    public int getItemCount() {
        if (sets != null) {
            return sets.size();
        }
        return 0;
    }

    // ---------------- ViewHolder ----------------

    static class SetViewHolder extends RecyclerView.ViewHolder {

        TextView tvSetName;
        RecyclerView rvDrills;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetName = itemView.findViewById(R.id.tvSetName);
            rvDrills = itemView.findViewById(R.id.rvDrills);
        }
    }
}