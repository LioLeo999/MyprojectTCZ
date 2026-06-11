package com.example.myprojecttcz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Drill2v;
import java.util.List;

public class AdminDrillAdapter extends RecyclerView.Adapter<AdminDrillAdapter.DrillViewHolder> {

    private List<Drill2v> drillList;
    private OnDrillClickListener listener;

    public interface OnDrillClickListener {
        void onDrillClick(Drill2v drill);
        void onDrillLongClick(Drill2v drill); // שינינו את השם כאן
    }

    public AdminDrillAdapter(List<Drill2v> drillList, OnDrillClickListener listener) {
        this.drillList = drillList;
        this.listener = listener;
    }

    public void filterList(List<Drill2v> filteredList) {
        this.drillList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DrillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drill_full_width, parent, false);
        return new DrillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrillViewHolder holder, int position) {
        Drill2v drill = drillList.get(position);

        // הצגת השם
        holder.tvDrillName.setText(drill.getName());

        // לחיצה רגילה (קצרה)
        holder.itemView.setOnClickListener(v -> listener.onDrillClick(drill));

        // לחיצה ארוכה
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDrillLongClick(drill); // קורא לפעולה המעודכנת
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return drillList.size();
    }

    static class DrillViewHolder extends RecyclerView.ViewHolder {
        TextView tvDrillName;

        public DrillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDrillName = itemView.findViewById(R.id.tvDrillName);
        }
    }
}