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
import com.example.myprojecttcz.screens.ShowTrainingSet; // הוספתי את האימפורט הזה

import java.util.ArrayList;
import java.util.List;

public class TrainingSetAdapter
        extends RecyclerView.Adapter<TrainingSetAdapter.SetViewHolder> {

    private final Context context;
    private final List<MaarachImun> sets;

    public TrainingSetAdapter(Context context, List<MaarachImun> sets) {
        this.context = context;
        this.sets = sets;
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

        // RecyclerView פנימי של drill IDs (שומר על מה שהיה לך)
        // שים לב: תוודא שיש לך את הקלאס DrillMiniAdapter, אם לא - תצטרך ליצור אותו או להסיר את הקטע הזה
        DrillMiniAdapter drillAdapter = new DrillMiniAdapter(drillIds);

        holder.rvDrills.setLayoutManager(new LinearLayoutManager(context));
        holder.rvDrills.setAdapter(drillAdapter);

        // --- כאן השינוי: לחיצה על הכרטיס מעבירה לעמוד העריכה המלא ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowTrainingSet.class);
            intent.putExtra("maarach_id", set.getId()); // שליחת ה-ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sets != null ? sets.size() : 0;
    }

    // ---------------- ViewHolder ----------------

    static class SetViewHolder extends RecyclerView.ViewHolder {

        TextView tvSetName;
        RecyclerView rvDrills;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetName = itemView.findViewById(R.id.tvSetName); // שם המערך
            rvDrills = itemView.findViewById(R.id.rvDrills);     // רשימה פנימית קטנה
        }
    }
}