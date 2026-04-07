package com.example.myprojecttcz.screens;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.model.MaarachImun;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowDrill extends BaseActivity implements View.OnClickListener {
    private Intent get;
    private String id;
    private Drill2v drill;
    private ImageView imgGif;
    private Spinner SpinnerAddtoImun;
    private String currentUserUid;
    private ArrayList<String> trainingSets;
    private User currentUser;
    private ArrayAdapter<String> adapter;

    private TextView tvname, tvexplanation, tvminimumplayers, tvmaximumplayers, tvshots, tvtools, tvage, tvplayerlevel, tvphysicallevel, tvballcolor, tvcourtsize;
    private Button btnvideocoachview, btnincourtview;
    private DatabaseService databaseService;
    private FirebaseAuth mauth;
    private CardView cardViewAddtotrainingset;


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

        // בדיקת Null בטוחה לפני קריאה ל-getUid
        if (mauth.getCurrentUser() != null) {
            currentUserUid = mauth.getUid();
        }
        showaddtotrainingset();
        loadDrill();

        if (mauth.getCurrentUser() != null){
            startLoadingData();
        }
    }

    public void initView(){
        databaseService = DatabaseService.getInstance();
        mauth = FirebaseAuth.getInstance();
        trainingSets = new ArrayList<>();
        get = getIntent();

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

        btnvideocoachview = findViewById(R.id.btncoachview);
        btnvideocoachview.setOnClickListener(this);
        btnincourtview = findViewById(R.id.btnincourtview);
        btnincourtview.setOnClickListener(this);

        cardViewAddtotrainingset = findViewById(R.id.cardViewAddtotrainingset);

        SpinnerAddtoImun = findViewById(R.id.spinnerAddtoImun);
        imgGif = findViewById(R.id.imgGif);

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
                Toast.makeText(ShowDrill.this, "Failed to load drill", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fillUI() {
        if (drill == null) return;

        tvname.setText(drill.getName());
        tvexplanation.setText("Explanation: " + drill.getExplanation());
        tvminimumplayers.setText("Mininum players: " + drill.getMinplayers());
        tvmaximumplayers.setText("Maximum players:" + drill.getMaxplayers());
        tvtools.setText("Training tools: " + drill.getTrainingTools());
        tvage.setText("Ages: " + drill.getAge());
        tvplayerlevel.setText("Player level: " + drill.getPlayerLevel());
        tvballcolor.setText("Ball color: " + drill.getBallColor());
        tvcourtsize.setText("Court size: " + drill.getCourtSize());
        tvshots.setText(buildShotsText());
        tvphysicallevel.setText("Physical level: " + drill.getLevel());

        if (drill.getGif() == null || drill.getGif().isEmpty()) {
            imgGif.setVisibility(View.GONE);
        } else {
            imgGif.setVisibility(View.VISIBLE);
            Glide.with(ShowDrill.this).asGif().load(drill.getGif()).into(imgGif);
        }

        if (drill.getVideo2() == null)
            btnincourtview.setVisibility(View.GONE);
    }

    private String buildShotsText() {
        StringBuilder sb = new StringBuilder();
        if (drill.getForehand()) sb.append("Forehand ");
        if (drill.getBackhand()) sb.append("Backhand ");
        if (drill.getVolleyforehand()) sb.append("Volley FH ");
        if (drill.getVolleybackhand()) sb.append("Volley BH ");
        if (drill.getForwardforehand()) sb.append("Forward FH ");
        if (drill.getForwardbackhand()) sb.append("Forward BH ");
        if (drill.getDriveforehand()) sb.append("Drive FH ");
        if (drill.getDrivebackhand()) sb.append("Drive BH ");
        if (drill.getServe()) sb.append("Serve ");
        if (drill.getSmash()) sb.append("Smash ");
        return sb.length() == 0 ? "—" : sb.toString();
    }

    public void startLoadingData() {
        createArray();
    }

    public void createArray() {
        if (currentUserUid == null) return;

        databaseService.getUser(currentUserUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public User onCompleted(User object) {
                currentUser = object;

                trainingSets.clear();
                trainingSets.add("Add to a training set");
                trainingSets.add("Create new training set");

                // תיקון: איטרציה על Values של ה-HashMap
                if (currentUser != null && currentUser.getMaarachim() != null) {
                    for (MaarachImun set : currentUser.getMaarachim().values()) {
                        trainingSets.add(set.getName());
                    }
                }

                setUpSpinner();
                return null;
            }

            @Override
            public void onFailed(Exception e) { }
        });
    }

    public void setUpSpinner() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainingSets);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerAddtoImun.setAdapter(adapter);

        SpinnerAddtoImun.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) { // "Create new training set"
                    showCreateMaarachDialog();
                } else if (position > 1) {
                    String selectedMaarachName = trainingSets.get(position);
                    addDrillToSelectedMaarach(selectedMaarachName);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void addDrillToSelectedMaarach(String maarachName) {
        if (currentUser == null || drill == null) return;

        MaarachImun selectedMaarach = null;

        // תיקון: חיפוש ב-HashMap
        if (currentUser.getMaarachim() != null) {
            for (MaarachImun m : currentUser.getMaarachim().values()) {
                if (m.getName().equals(maarachName)) {
                    selectedMaarach = m;
                    break;
                }
            }
        }

        if (selectedMaarach != null) {
            if (selectedMaarach.getDrillsid() == null) {
                selectedMaarach.setDrillsid(new ArrayList<>());
            }

            if (selectedMaarach.getDrillsid().contains(drill.getId())) {
                Toast.makeText(this, "Drill already in this set", Toast.LENGTH_SHORT).show();
                SpinnerAddtoImun.setSelection(0);
                return;
            }

            selectedMaarach.getDrillsid().add(drill.getId());

            databaseService.updateUser(currentUser, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public User onCompleted(Void object) {
                    Toast.makeText(ShowDrill.this, "Added to " + maarachName, Toast.LENGTH_SHORT).show();
                    SpinnerAddtoImun.setSelection(0);
                    return null;
                }
                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(ShowDrill.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showCreateMaarachDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Create New Training Set");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter set name (e.g., Monday Practice)");
        builder.setView(input);

        builder.setPositiveButton("Create & Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                createNewMaarachWithDrill(name);
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                SpinnerAddtoImun.setSelection(0);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            SpinnerAddtoImun.setSelection(0);
        });

        builder.show();
    }

    private void createNewMaarachWithDrill(String name) {
        if (currentUser == null || drill == null) return;

        String newMaarachId = databaseService.generateMaarachId(currentUserUid);

        ArrayList<String> drillIds = new ArrayList<>();
        drillIds.add(drill.getId());

        MaarachImun newMaarach = new MaarachImun(newMaarachId, name, "", drillIds);

        if (currentUser.getMaarachim() == null) {
            currentUser.setMaarachim(new HashMap<>());
        }

        // תיקון: שימוש ב-put במקום add
        currentUser.getMaarachim().put(newMaarach.getId(), newMaarach);

        databaseService.updateUser(currentUser, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                Toast.makeText(ShowDrill.this, "New set '" + name + "' created!", Toast.LENGTH_SHORT).show();
                trainingSets.add(name);
                adapter.notifyDataSetChanged();
                SpinnerAddtoImun.setSelection(0);
                return null;
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ShowDrill.this, "Failed to create set", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (drill == null) return;

        if (v == btnvideocoachview) {
            String videoUrl = drill.getVideo1(); // קבל את הלינק מהאובייקט שלך

            if (videoUrl != null && !videoUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // במקרה שאין אפליקציה שיכולה לפתוח את הוידאו, נפתח בדפדפן כשגיבוי
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl));
                    startActivity(webIntent);
                }
            }
        }
        if (v == btnincourtview) {
            String videoUrl = drill.getVideo2(); // קבל את הלינק מהאובייקט שלך

            if (videoUrl != null && !videoUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // במקרה שאין אפליקציה שיכולה לפתוח את הוידאו, נפתח בדפדפן כשגיבוי
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl));
                    startActivity(webIntent);
                }
            }
        }
    }

    public void showaddtotrainingset(){
        String uid = mauth.getUid();
        if (uid != null)
            cardViewAddtotrainingset.setVisibility(View.VISIBLE);
    }
}