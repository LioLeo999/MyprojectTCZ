package com.example.myprojecttcz.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.screens.ShowDrill;

import java.util.Collections;
import java.util.List;

public class DrillListReorderAdapter extends RecyclerView.Adapter<DrillListReorderAdapter.DrillViewHolder> {

    private Context context;
    private List<Drill2v> drills;

    public DrillListReorderAdapter(Context context, List<Drill2v> drills) {
        this.context = context;
        this.drills = drills;
    }

    @NonNull
    @Override
    public DrillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // משתמשים ב-XML החדש שיצרנו לרוחב מלא
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
    }

    @Override
    public int getItemCount() {
        return drills.size();
    }

    // פונקציה קריטית לגרירה - מחליפה שני פריטים ברשימה
    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(drills, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    // מחזיר את הרשימה המעודכנת כדי שנוכל לשמור אותה
    public List<Drill2v> getDrills() {
        return drills;
    }

    static class DrillViewHolder extends RecyclerView.ViewHolder {
        TextView tvDrillName;

        public DrillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDrillName = itemView.findViewById(R.id.tvDrillName);
        }
    }
}