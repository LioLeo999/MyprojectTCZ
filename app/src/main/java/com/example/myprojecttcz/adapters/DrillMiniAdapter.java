package com.example.myprojecttcz.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.services.DatabaseService;

import java.util.List;

public class DrillMiniAdapter extends RecyclerView.Adapter<DrillMiniAdapter.ViewHolder> {

    private final List<String> drillIds;
    private final DatabaseService ds = DatabaseService.getInstance();

    public DrillMiniAdapter(List<String> drillIds) {
        this.drillIds = drillIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יוצר טקסט פשוט לכל שם תרגיל
        TextView tv = new TextView(parent.getContext());
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10, 5, 10, 5);
        tv.setTextSize(14);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id = drillIds.get(position);

        // כאן הקסם: הופכים ID לשם
        ds.getDrillById(id, new DatabaseService.DrillCallback() {
            @Override
            public void onSuccess(Drill2v drill) {
                holder.textView.setText("• " + drill.getName());
            }

            @Override
            public void onError(Exception e) {
                holder.textView.setText("• Unknown Drill");
            }
        });
    }

    @Override
    public int getItemCount() {
        return drillIds != null ? drillIds.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
