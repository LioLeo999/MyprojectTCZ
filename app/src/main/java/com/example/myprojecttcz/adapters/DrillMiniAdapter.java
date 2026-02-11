package com.example.myprojecttcz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
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
        // כאן התיקון: שימוש ב-LayoutInflater כדי לקשר לקובץ ה-XML שלך
        // ודא שיש לך קובץ בשם item_drill_mini.xml (או שנה את השם כאן לשם הקובץ שלך)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drill_full_width, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id = drillIds.get(position);

        // איפוס הטקסט לפני הטעינה
        holder.textView.setText("Loading...");

        // שמירת ה-ID בתוך ה-View (כדי לוודא אח"כ שזה עדיין אותו פריט)
        holder.itemView.setTag(id);

        ds.getDrillById(id, new DatabaseService.DrillCallback() {
            @Override
            public void onSuccess(Drill2v drill) {
                // בדיקה 1: האם השורה הזו עדיין קיימת ברשימה?
                if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION) {

                    // בדיקה 2 (מומלצת): האם השורה הזו עדיין אמורה להציג את ה-ID הזה?
                    // זה מונע באגים כשגוללים מהר
                    if (id.equals(holder.itemView.getTag())) {
                        holder.textView.setText("• " + drill.getName());
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    holder.textView.setText("• Error loading");
                }
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
            // קישור ל-ID שמוגדר ב-XML
            textView = itemView.findViewById(R.id.tvDrillName);
        }
    }
}