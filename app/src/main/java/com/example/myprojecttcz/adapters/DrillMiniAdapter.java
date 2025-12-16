package com.example.myprojecttcz.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.services.DatabaseService;

import java.util.List;

public class DrillMiniAdapter
        extends RecyclerView.Adapter<DrillMiniAdapter.DrillViewHolder> {

    private final List<String> drillIds;
    private final DatabaseService db;

    public DrillMiniAdapter(List<String> drillIds) {
        this.drillIds = drillIds;
        this.db = DatabaseService.getInstance();
    }

    @NonNull
    @Override
    public DrillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(8, 4, 8, 4);
        tv.setTextSize(14f);
        return new DrillViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull DrillViewHolder holder, int position) {

        String drillId = drillIds.get(position);

        // טקסט זמני (שיהיה משהו עד שה־DB חוזר)
        holder.textView.setText("Loading...");

        db.getDrillById(drillId, new DatabaseService.DrillCallback() {
            @Override
            public void onSuccess(Drill2v drill) {
                holder.textView.setText(drill.getName());
            }

            @Override
            public void onError(Exception e) {
                holder.textView.setText("Unknown drill");
            }
        });
    }

    @Override
    public int getItemCount() {
        return drillIds != null ? drillIds.size() : 0;
    }

    // ---------------- ViewHolder ----------------

    static class DrillViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public DrillViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
