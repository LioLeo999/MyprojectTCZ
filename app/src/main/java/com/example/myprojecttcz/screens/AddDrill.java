package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.model.Drill;
import com.example.myprojecttcz.model.Drill2v;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddDrill extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddDrill";
    private static final int PICK_GIF = 101;

    private EditText editName, editTime, editLevel, editMinPlayers, editMaxPlayers, edityt, edityt2;
    private CheckBox checkForehand, checkBackhand, checkVolleyForehand, checkVolleyBackhand,
            checkDriveForehand, checkDriveBackhand, checkServe, checkSmash,
            checkForwardForehand, checkForwardBackhand;
    private Button addsubmitbtn, btSelectGif;

    private Uri gifUri = null;
    private String gifUrl = null;

    private DatabaseReference drillsRef;

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

        drillsRef = FirebaseDatabase.getInstance().getReference("drills");
        initViews();
    }

    private void initViews() {

        // EditTexts
        editName = findViewById(R.id.editName);
        editTime = findViewById(R.id.editTime);
        editLevel = findViewById(R.id.editLevel);
        editMinPlayers = findViewById(R.id.editMinPlayers);
        editMaxPlayers = findViewById(R.id.editMaxPlayers);
        edityt = findViewById(R.id.etyoutubev);
        edityt2 = findViewById(R.id.etyoutubev2);

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

        addsubmitbtn.setOnClickListener(this);
        btSelectGif.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btSelectGif) {
            openGifPicker();
            return;
        }

        if (v == addsubmitbtn) {
            startDrillCreation();
        }
    }

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

    private void startDrillCreation() {

        String name = editName.getText().toString();
        String time = editTime.getText().toString();
        String level = editLevel.getText().toString();
        String minP = editMinPlayers.getText().toString();
        String maxP = editMaxPlayers.getText().toString();

        String yt1 = edityt.getText().toString().trim();
        String yt2 = edityt2.getText().toString().trim();

        String id = drillsRef.push().getKey();

        if (id == null) {
            Toast.makeText(this, "Failed to generate ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gifUri != null) {
            uploadGif(id, () -> createDrillObject(id, name, time, level, minP, maxP, yt1, yt2));
        } else {
            createDrillObject(id, name, time, level, minP, maxP, yt1, yt2);
        }
    }

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
                    Toast.makeText(this, "GIF upload failed", Toast.LENGTH_SHORT).show();
                    onComplete.run(); // create drill anyway without gif
                });
    }

    private void createDrillObject(String id, String name, String time, String level, String minP,
                                   String maxP, String yt1, String yt2) {

        if (!yt1.isEmpty() && yt2.isEmpty()) {

            Drill drill = new Drill(
                    id, name, time, level, minP, maxP,
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
                    yt1
            );

            drillsRef.child(id).setValue(drill);
            Toast.makeText(this, "Drill created!", Toast.LENGTH_SHORT).show();
        }

        else if (!yt1.isEmpty() && !yt2.isEmpty()) {

            Drill2v drill2v = new Drill2v(
                    id, name, time, level, minP, maxP,
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
                    yt2
            );

            drillsRef.child(id).setValue(drill2v);
            Toast.makeText(this, "2v Drill created!", Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(this, "Please provide at least 1 YouTube link", Toast.LENGTH_SHORT).show();
        }
    }
}
