package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.services.DatabaseService;

public class ShowDrill extends AppCompatActivity implements View.OnClickListener {
    private Intent get;
    private String id;
    private Drill2v drill;
    private ImageView imgGif;


    private TextView tvname, tvexplanation, tvminimumplayers, tvmaximumplayers, tvshots, tvtools, tvage, tvplayerlevel, tvphysicallevel, tvballcolor, tvcourtsize;
    private Button btnvideocoachview, btnincourtview;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_drill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        loadDrill();


    }
    public void initView(){
        // databaseservice
        databaseService = DatabaseService.getInstance();
        // intent
        get = getIntent();
        // textview
        tvname = findViewById(R.id.tvdrillname);
        tvexplanation = findViewById(R.id.tvexplanationdrill);
        tvminimumplayers = findViewById(R.id.tvminplayers);
        tvmaximumplayers = findViewById(R.id.tvmaxplayers);
        tvshots = findViewById(R.id.tvshots);
        tvtools = findViewById(R.id.tvtools);
        tvage = findViewById(R.id.tvage);
        tvplayerlevel = findViewById(R.id.tvplayerlevel);
        tvphysicallevel = findViewById(R.id.tvphysicallevel);
        tvballcolor = findViewById(R.id.tvballcolor);
        tvcourtsize = findViewById(R.id.tvcourtsize);

        //buttons
        btnvideocoachview = findViewById(R.id.btncoachview);
        btnvideocoachview.setOnClickListener(this);
        btnincourtview = findViewById(R.id.btnincourtview);
        btnincourtview.setOnClickListener(this);

        // imgview
        imgGif = findViewById(R.id.imgGif);
        //drill id
        id = get.getStringExtra("id");

        if (id == null) {
            Toast.makeText(this, "No drill id", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadDrill() {

        databaseService.getDrillById(id, new DatabaseService.DrillCallback() {

            @Override
            public void onSuccess(Drill2v d) {
                drill = d;
                fillUI();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ShowDrill.this,
                        "Failed to load drill",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fillUI() {
        tvname.setText(drill.getName());
        tvexplanation.setText(drill.getExplanation());

        tvminimumplayers.setText(drill.getMinplayers());
        tvmaximumplayers.setText(drill.getMaxplayers());

        tvtools.setText(drill.getTrainingTools());
        tvage.setText(drill.getAge());
        tvplayerlevel.setText(drill.getPlayerLevel());
        tvballcolor.setText(drill.getBallColor());
        tvcourtsize.setText(drill.getCourtSize());

        // דוגמה לשוטים – תתאים לשדות שלך
        tvshots.setText(buildShotsText());

        // רמת קושי פיזית
        tvphysicallevel.setText(drill.getLevel());

        if (drill.getGif() == null || drill.getGif().isEmpty()) {
            imgGif.setVisibility(View.GONE);
        } else {
            imgGif.setVisibility(View.VISIBLE);

            Glide.with(ShowDrill.this)
                    .asGif()
                    .load(drill.getGif()) // ה-URL מ-Firebase Storage
                    .into(imgGif);
        }


    }

    private String buildShotsText() {
        StringBuilder sb = new StringBuilder();

        if (drill.getForehand()) sb.append("Forehand ");
        if (drill.getBackhand()) sb.append("Backhand ");
        if (drill.getVolleyforehand()) sb.append("Volley FH ");
        if (drill.getVolleybackhand()) sb.append("Volley BH ");
        if (drill.getForwardforehand()) sb.append("Forward FH ");
        if (drill.getForwardbackhand()) sb.append("Forward BH ");
        if (drill.getDriveforehand()) sb.append("Drive FH");
        if (drill.getDrivebackhand()) sb.append("Drive BH");
        if (drill.getServe()) sb.append("Serve ");
        if (drill.getSmash()) sb.append("Smash ");


        return sb.length() == 0 ? "—" : sb.toString();
    }

    @Override
    public void onClick(View v) {

        if (drill == null) return;

        if (v == btnvideocoachview) {
            Intent intent = new Intent(this, ShowDrillVideo.class);
            intent.putExtra("link", drill.getVideo1());
            startActivity(intent);
        }

        if (v == btnincourtview) {
            Intent intent = new Intent(this, ShowDrillVideo.class);
            intent.putExtra("link", drill.getVideo2());
            startActivity(intent);
        }
    }
}