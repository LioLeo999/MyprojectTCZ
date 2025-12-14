package com.example.myprojecttcz.adapters;

// Context – בשביל Intent, Glide, resources
import android.content.Context;
import android.content.Intent;

// ניפוח XML
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Views
import android.widget.ImageView;
import android.widget.TextView;

// RecyclerView
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Glide לטעינת GIF
import com.bumptech.glide.Glide;

// קבצי הפרויקט
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.screens.ShowDrill;

import java.util.List;

/**
 * Adapter להצגת מאגר drills ב-Grid
 * כל כרטיס מציג שם + GIF
 * לחיצה על הכרטיס מעבירה ל-ShowDrill
 */
public class DrillAdapter
        extends RecyclerView.Adapter<DrillAdapter.DrillViewHolder> {

    // Context – נדרש ל-Intent ו-Glide
    private Context context;

    // רשימת הדרילים שמוצגים במסך
    private List<Drill2v> drillList;

    /**
     * constructor
     * @param context Activity שממנה מפעילים את ה-RecyclerView
     * @param drillList רשימת Drill2v מהדאטאבייס
     */
    public DrillAdapter(Context context, List<Drill2v> drillList) {
        this.context = context;
        this.drillList = drillList;
    }

    /**
     * יוצר כרטיס חדש (קורא ל-XML)
     */
    @NonNull
    @Override
    public DrillViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_drill, parent, false);

        return new DrillViewHolder(view);
    }

    /**
     * ממלא כרטיס בנתונים של Drill אחד
     */
    @Override
    public void onBindViewHolder(
            @NonNull DrillViewHolder holder, int position) {

        Drill2v drill = drillList.get(position);

        // שם התרגיל
        holder.tvName.setText(drill.getName());

        // טעינת GIF
        Glide.with(context)
                .asGif()
                .load(drill.getGif())
                .into(holder.imgGif);

        // לחיצה על כל הכרטיס
        holder.itemView.setOnClickListener(v -> {

            // מניעת לחיצה כפולה
            v.setEnabled(false);

            // מעבר למסך ShowDrill עם id של התרגיל
            Intent intent = new Intent(context, ShowDrill.class);
            intent.putExtra("id", drill.getId());
            context.startActivity(intent);

            // החזרת אפשרות הלחיצה
            v.postDelayed(() -> v.setEnabled(true), 600);
        });
    }

    /**
     * מספר הפריטים ברשימה
     */
    @Override
    public int getItemCount() {
        return drillList.size();
    }

    /**
     * ViewHolder
     * מחזיק את ה-Views של כרטיס אחד
     */
    static class DrillViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView imgGif;

        public DrillViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            imgGif = itemView.findViewById(R.id.imgGif);
        }
    }
}
