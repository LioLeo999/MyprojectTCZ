package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.myprojecttcz.R;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.model.User;
import com.example.myprojecttcz.services.DatabaseService;

public class EditDrill extends BaseActivity implements View.OnClickListener {

    Intent get;
    String changeDrillid;
    Drill2v drill;

    ImageView imgGif;

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
    private Button updatedrillbtn, btSelectGif;

    // GIF FILE
    private Uri gifUri = null;
    private String gifUrl = null;

    // DATABASE SERVICE
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_drill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initSpinners();
        initDrill();

    }

    private void initView(){
        get = getIntent();
        changeDrillid = get.getStringExtra("id");

        databaseService = DatabaseService.getInstance();

        imgGif = findViewById(R.id.imgGife);

        // INPUT FIELDS
        editName = findViewById(R.id.eeditName);
        editExplanation = findViewById(R.id.eeditExplanation);
        editTime = findViewById(R.id.eeditTime);
        editMinPlayers = findViewById(R.id.eeditMinPlayers);
        editMaxPlayers = findViewById(R.id.eeditMaxPlayers);
        editTrainingTools = findViewById(R.id.eeditTrainingTools);
        editAge = findViewById(R.id.eeditAge);
        etcoachview = findViewById(R.id.eetcoachv);
        etincourtview = findViewById(R.id.eetincourtview);

        // SPINNERS
        spinnerBallColor = findViewById(R.id.espinnerBallColor);
        spinnerCourtSize = findViewById(R.id.espinnerCourtSize);
        spinnerPlayerLevel = findViewById(R.id.espinnerPlayerLevel);
        spinnerPhisicalLevel = findViewById(R.id.espinnerPhysicalLevel);

        // CHECKBOXES
        checkForehand = findViewById(R.id.checkForehande);
        checkBackhand = findViewById(R.id.checkBackhande);
        checkVolleyForehand = findViewById(R.id.checkVolleyForehande);
        checkVolleyBackhand = findViewById(R.id.checkVolleyBackhande);
        checkDriveForehand = findViewById(R.id.checkDriveForehande);
        checkDriveBackhand = findViewById(R.id.checkDriveBackhande);
        checkServe = findViewById(R.id.checkServee);
        checkSmash = findViewById(R.id.checkSmashe);
        checkForwardForehand = findViewById(R.id.checkForwardForehande);
        checkForwardBackhand = findViewById(R.id.checkForwardBackhande);

        // BUTTONS
        btSelectGif = findViewById(R.id.ebtSelectGif);
        btSelectGif.setOnClickListener(this);
        updatedrillbtn = findViewById(R.id.updatedrillbtn);
        updatedrillbtn.setOnClickListener(this);


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
        adapterpllevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlayerLevel.setAdapter(adapterpllevel);
        // Phisical level
        ArrayAdapter<CharSequence> adapterphlevel =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.level,
                        android.R.layout.simple_spinner_item
                );
        adapterphlevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhisicalLevel.setAdapter(adapterphlevel);

    }
    private void initDrill(){
        databaseService.getDrillById(changeDrillid, new DatabaseService.DrillCallback() {
            @Override
            public void onSuccess(Drill2v drill2) {
                drill = drill2;
                fillUI();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EditDrill.this, "Coudn't load drill", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillUI() {
        if (drill == null) return;

        editName.setText(drill.getName());
        editExplanation.setText(drill.getExplanation());
        editTime.setText(drill.getTime());
        editMinPlayers.setText(drill.getMinplayers());
        editMaxPlayers.setText(drill.getMaxplayers());
        editTrainingTools.setText(drill.getTrainingTools());
        editAge.setText(drill.getAge());
        etcoachview.setText(drill.getVideo1());
        if (drill.getVideo2() != null){
            etincourtview.setText(drill.getVideo2());
        }

        // 1. צבע כדור (Ball Color)
        if (drill.getBallColor() != null) {
            // א. שולפים את האדפטר של הספינר
            ArrayAdapter adapter = (ArrayAdapter) spinnerBallColor.getAdapter();
            if (adapter != null) {
                // ב. מוצאים באיזה אינדקס נמצא הטקסט שלנו
                int position = adapter.getPosition(drill.getBallColor());
                // ג. אומרים לספינר לקפוץ לאינדקס הזה
                spinnerBallColor.setSelection(position);
            }
        }

        // 2. גודל מגרש (Court Size)
        if (drill.getCourtSize() != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerCourtSize.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(drill.getCourtSize());
                spinnerCourtSize.setSelection(position);
            }
        }

        // 3. רמת שחקן (Player Level)
        if (drill.getPlayerLevel() != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerPlayerLevel.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(drill.getPlayerLevel());
                spinnerPlayerLevel.setSelection(position);
            }
        }

        // 4. רמה פיזית (Physical Level)
        if (drill.getLevel() != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerPhisicalLevel.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(drill.getLevel());
                spinnerPhisicalLevel.setSelection(position);
            }
        }

        if (drill.getForehand()) {
            checkForehand.setChecked(true);
        }
        if (drill.getBackhand()) {
            checkBackhand.setChecked(true);
        }
        if (drill.getDriveforehand()){
            checkDriveForehand.setChecked(true);
        }
        if (drill.getDrivebackhand()){
            checkDriveBackhand.setChecked(true);
        }
        if (drill.getForwardforehand()){
            checkForwardForehand.setChecked(true);
        }
        if (drill.getForwardbackhand()){
            checkForwardBackhand.setChecked(true);
        }
        if (drill.getServe()){
            checkServe.setChecked(true);
        }
        if (drill.getSmash())
        {
            checkSmash.setChecked(true);
        }
        if (drill.getVolleyforehand()){
            checkVolleyForehand.setChecked(true);
        }
        if (drill.getVolleybackhand()){
            checkVolleyBackhand.setChecked(true);
        }



        if (drill.getGif() == null || drill.getGif().isEmpty()) {
            imgGif.setVisibility(View.GONE);
        } else {
            imgGif.setVisibility(View.VISIBLE);
            Glide.with(EditDrill.this).asGif().load(drill.getGif()).into(imgGif);
        }


    }
    private final ActivityResultLauncher<Intent> pickGifLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // מקבלים את הנתיב (Uri) של ה-GIF שנבחר
                    gifUri = result.getData().getData();

                    // מציגים אותו במסך (מחליף את הישן בתצוגה)
                    imgGif.setVisibility(View.VISIBLE);
                    Glide.with(EditDrill.this).asGif().load(gifUri).into(imgGif);
                }
            }
    );





    private void saveDrillChanges() {
        if (drill == null) return;

        // 1. אוספים את כל הטקסטים
        drill.setName(editName.getText().toString());
        drill.setExplanation(editExplanation.getText().toString());
        drill.setTrainingTools(editTrainingTools.getText().toString());
        drill.setTime(editTime.getText().toString());
        drill.setVideo1(etcoachview.getText().toString());
        drill.setVideo2(etincourtview.getText().toString());

        // ממירים מספרים בצורה בטוחה
        try {
            // שמירת שחקנים וגיל (שמוגדרים כ-String במודל)
            drill.setMinplayers(editMinPlayers.getText().toString());
            drill.setMaxplayers(editMaxPlayers.getText().toString());
            drill.setAge(editAge.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for Players/Age", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. אוספים מהספינרים
        if (spinnerBallColor.getSelectedItem() != null) drill.setBallColor(spinnerBallColor.getSelectedItem().toString());
        if (spinnerCourtSize.getSelectedItem() != null) drill.setCourtSize(spinnerCourtSize.getSelectedItem().toString());
        if (spinnerPlayerLevel.getSelectedItem() != null) drill.setPlayerLevel(spinnerPlayerLevel.getSelectedItem().toString());
        if (spinnerPhisicalLevel.getSelectedItem() != null) drill.setLevel(spinnerPhisicalLevel.getSelectedItem().toString());

        // 3. אוספים מהצ'קבוקסים
        drill.setForehand(checkForehand.isChecked());
        drill.setBackhand(checkBackhand.isChecked());
        drill.setDriveforehand(checkDriveForehand.isChecked());
        drill.setDrivebackhand(checkDriveBackhand.isChecked());
        drill.setForwardforehand(checkForwardForehand.isChecked());
        drill.setForwardbackhand(checkForwardBackhand.isChecked());
        drill.setServe(checkServe.isChecked());
        drill.setSmash(checkSmash.isChecked());
        drill.setVolleyforehand(checkVolleyForehand.isChecked());
        drill.setVolleybackhand(checkVolleyBackhand.isChecked());

        // 4. העלאה ושמירה
        // אם המשתמש בחר GIF חדש (gifUri לא ריק), צריך קודם להעלות אותו
        if (gifUri != null) {
            Toast.makeText(this, "Uploading new GIF...", Toast.LENGTH_SHORT).show();
            // הערה: שים כאן את הפעולה מה-DatabaseService שלך שמעלה תמונה
            databaseService.uploadImage(gifUri, new DatabaseService.ImageUploadCallback() {
                @Override
                public void onSuccess(String newImageUrl) {
                    drill.setGif(newImageUrl); // מעדכנים את ה-URL החדש בדריל
                    updateInFirebase(); // שומרים את שאר הפרטים
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EditDrill.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // אם לא נבחר GIF חדש, פשוט שומרים את הנתונים החדשים
            updateInFirebase();
        }
    }

    private void updateInFirebase() {
        // הערה: שים כאן את הפעולה מה-DatabaseService שלך שמעדכנת דריל קיים
        databaseService.updateDrill(drill, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public User onCompleted(Void object) {
                Toast.makeText(EditDrill.this, "Drill updated successfully", Toast.LENGTH_SHORT).show();
                finish();
                return null;
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(EditDrill.this, "Failed to update drill", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.updatedrillbtn) {
            saveDrillChanges();
        }
        if (view.getId() == R.id.ebtSelectGif){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/gif");
            pickGifLauncher.launch(intent);
        }
    }
}