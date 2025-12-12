package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.services.DatabaseService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddDrill extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddDrill";
    private static final int PICK_GIF = 101;

    // INPUT FIELDS
    private EditText editName, editExplanation, editTime, editMinPlayers, editMaxPlayers;
    private EditText editTrainingTools, editAge;
    private EditText etcoachview, etincourtview;

    // SPINNERS
    private Spinner spinnerBallColor, spinnerCourtSize, spinnerPlayerLevel, spinnerPhisicalLevel;

    // CHECKBOXES
    private CheckBox checkForehand, checkBackhand, checkVolleyForehand, checkVolleyBackhand;
    private CheckBox checkDriveForehand, checkDriveBackhand, checkServe, checkSmash;
    private CheckBox checkForwardForehand, checkForwardBackhand;

    // BUTTONS
    private Button addsubmitbtn, btSelectGif;
    private ImageButton btntomain;

    // GIF FILE
    private Uri gifUri = null;
    private String gifUrl = null;

    // DATABASE SERVICE
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_drill);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        initViews();
        initSpinners();
    }

    // ---------------------------------------------------------
    // INIT VIEWS
    // ---------------------------------------------------------
    private void initViews() {

        // EditTexts
        editName = findViewById(R.id.editName);
        editExplanation = findViewById(R.id.editExplanation);
        editTime = findViewById(R.id.editTime);
        editMinPlayers = findViewById(R.id.editMinPlayers);
        editMaxPlayers = findViewById(R.id.editMaxPlayers);

        editTrainingTools = findViewById(R.id.editTrainingTools);
        editAge = findViewById(R.id.editAge);

        etcoachview = findViewById(R.id.etcoachv);
        etincourtview = findViewById(R.id.etincourtview);

        // Spinners
        spinnerBallColor = findViewById(R.id.spinnerBallColor);
        spinnerCourtSize = findViewById(R.id.spinnerCourtSize);
        spinnerPhisicalLevel = findViewById(R.id.spinnerPhysicalLevel);
        spinnerPlayerLevel = findViewById(R.id.spinnerPlayerLevel);
        // Checkboxes
        checkForehand = findViewById(R.id.checkForehand);
        checkBackhand = findViewById(R.id.checkBackhand);
        checkVolleyForehand = findViewById(R.id.checkVolleyForehand);
        checkVolleyBackhand = findViewById(R.id.checkVolleyBackhand);
        checkDriveForehand = findViewById(R.id.checkDriveForehand);
        checkDriveBackhand = findViewById(R.id.checkDriveBackhand);
        checkServe = findViewById(R.id.checkServe);
        checkSmash = findViewById(R.id.checkSmash);
        checkForwardForehand = findViewById(R.id.checkForwardForehand);
        checkForwardBackhand = findViewById(R.id.checkForwardBackhand);

        // Buttons
        addsubmitbtn = findViewById(R.id.addsubmitbtn);
        btSelectGif = findViewById(R.id.btSelectGif);
        btntomain = findViewById(R.id.tomainbtnfromadddrill);
        btntomain.setOnClickListener(this);

        // Listeners
        addsubmitbtn.setOnClickListener(this);
        btSelectGif.setOnClickListener(this);
    }

    // ---------------------------------------------------------
    // INIT SPINNERS
    // ---------------------------------------------------------
    private void initSpinners() {


        // Ball colors
        ArrayAdapter<CharSequence> adapterBall =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.ballColors,
                        android.R.layout.simple_spinner_item
                );

        adapterBall.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerBallColor.setAdapter(adapterBall);


        // Court sizes
        ArrayAdapter<CharSequence> adapterCourt =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.courtSizes,
                        android.R.layout.simple_spinner_item
                );

        adapterCourt.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCourtSize.setAdapter(adapterCourt);

        // Player level
        ArrayAdapter<CharSequence> adapterpllevel =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.playerlevel,
                        android.R.layout.simple_spinner_item
                );
        adapterCourt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlayerLevel.setAdapter(adapterpllevel);
        // Phisical level
        ArrayAdapter<CharSequence> adapterphlevel =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.level,
                        android.R.layout.simple_spinner_item
                );
        adapterCourt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhisicalLevel.setAdapter(adapterphlevel);

    }

    // ---------------------------------------------------------
    // BUTTON CLICK HANDLING
    // ---------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (v == btntomain){
            Intent go = new Intent(this, MainActivity.class);
            startActivity(go);
        }
        if (v == btSelectGif) {
            openGifPicker();
            return;
        }

        if (v == addsubmitbtn) {
            startDrillCreation();
        }
    }

    // ---------------------------------------------------------
    // GIF PICKER
    // ---------------------------------------------------------
    private void openGifPicker() {
        Intent intent = new Intent();
        intent.setType("image/gif");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select GIF"), PICK_GIF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_GIF && resultCode == RESULT_OK && data != null) {
            gifUri = data.getData();
            Toast.makeText(this, "GIF selected!", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------------------------------------------------
    // MAIN CREATION PROCESS
    // ---------------------------------------------------------
    private void startDrillCreation() {

        if (gifUri == null) {
            Toast.makeText(this, "חובה לבחור GIF!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = editName.getText().toString();
        String explanation = editExplanation.getText().toString();
        String time = editTime.getText().toString();
        String minP = editMinPlayers.getText().toString();
        String maxP = editMaxPlayers.getText().toString();

        String tools = editTrainingTools.getText().toString();
        String age = editAge.getText().toString();

        String ballColor = spinnerBallColor.getSelectedItem().toString();
        String courtSize = spinnerCourtSize.getSelectedItem().toString();
        String playerLevel = spinnerPlayerLevel.getSelectedItem().toString();
        String level = spinnerPhisicalLevel.getSelectedItem().toString();

        String yt1 = etcoachview.getText().toString().trim();
        String yt2 = etincourtview.getText().toString().trim();

        if (yt1.isEmpty()) {
            Toast.makeText(this, "You must add a coach view video", Toast.LENGTH_SHORT).show();
            return;
        }
        if (yt2.isEmpty()){
            Toast.makeText(this,"You must add in court view video", Toast.LENGTH_SHORT);
            return;
        }

        String id = databaseService.generateDrillId();

        uploadGif(id, () ->
                createDrillObject(id, name, explanation, time, level, minP, maxP,
                        tools, age, playerLevel, ballColor, courtSize, yt1, yt2)
        );
    }

    // ---------------------------------------------------------
    // UPLOAD GIF
    // ---------------------------------------------------------
    private void uploadGif(String drillId, Runnable onComplete) {

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("gifs/" + drillId + ".gif");

        ref.putFile(gifUri)
                .addOnSuccessListener(task ->
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            gifUrl = uri.toString();
                            Log.d(TAG, "GIF uploaded: " + gifUrl);
                            onComplete.run();
                        })
                )
                .addOnFailureListener(e -> {
                    Log.e(TAG, "GIF upload failed", e);
                    Toast.makeText(this, "העלאת GIF נכשלה", Toast.LENGTH_SHORT).show();
                });
    }

    // ---------------------------------------------------------
    // MAKE OBJECT + SEND TO DATABASE
    // ---------------------------------------------------------
    private void createDrillObject(
            String id, String name, String explanation, String time, String level,
            String minP, String maxP, String tools, String age, String playerLevel,
            String ballColor, String courtSize, String yt1, String yt2
    ) {





            Drill2v drill2v = new Drill2v(
                    id,
                    name,
                    explanation,
                    time,
                    level,
                    minP,
                    maxP,

                    checkForehand.isChecked(),
                    checkBackhand.isChecked(),
                    checkVolleyForehand.isChecked(),
                    checkVolleyBackhand.isChecked(),
                    checkDriveForehand.isChecked(),
                    checkDriveBackhand.isChecked(),
                    checkServe.isChecked(),
                    checkSmash.isChecked(),
                    checkForwardForehand.isChecked(),
                    checkForwardBackhand.isChecked(),

                    gifUrl,
                    yt1,
                    yt2,

                    tools,
                    age,
                    playerLevel,
                    ballColor,
                    courtSize
            );


            databaseService.createNewDrill2v(drill2v, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void object) {
                    Toast.makeText(AddDrill.this, "תרגיל 2V נוצר!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(AddDrill.this, "כשל ביצירת 2V", Toast.LENGTH_SHORT).show();
                }
            });

    }
}
