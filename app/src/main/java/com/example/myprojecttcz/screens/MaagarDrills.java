package com.example.myprojecttcz.screens;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myprojecttcz.R;
import com.example.myprojecttcz.adapters.DrillAdapter;
import com.example.myprojecttcz.base.BaseActivity;
import com.example.myprojecttcz.model.Drill2v;
import com.example.myprojecttcz.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class MaagarDrills extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private DrillAdapter adapter;
    private List<Drill2v> drillList;
    private List<Drill2v> allDrillsList;
    private DatabaseService databaseService;

    // View Elements
    private EditText etSearchName, etMinPlayers, etMaxPlayers, etMinAge, etMaxAge, etMaxTime;
    private Spinner spinnerLevel, spinnerBallColor, spinnerCourtSize;

    // Checkboxes for all 12 strokes
    private CheckBox cbForehand, cbBackhand, cbServe, cbVolleyFh, cbVolleyBh, cbSmash;
    private CheckBox cbDriveFh, cbDriveBh, cbForwardFh, cbForwardBh, cbSliceFh, cbSliceBh;

    private Button btnToggleFilters, btnApplyFilters, btnClearFilters;
    private ScrollView filterLayout;

    // Spinner options in English
    private final String[] levelOptions = {"Level: All", "Beginner", "Intermediate", "Advanced", "Competitive"};
    private final String[] colorOptions = {"Ball: All", "Red", "Orange", "Green/Yellow"};
    private final String[] courtOptions = {"Court: All", "Mini", "3/4 court", "Full Court"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maagar_drills);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        setupSpinners();
        setupSearchListener();
        loaddrills();
    }

    public void initView() {
        recyclerView = findViewById(R.id.recyclerDrills);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        drillList = new ArrayList<>();
        allDrillsList = new ArrayList<>();

        adapter = new DrillAdapter(this, drillList);
        recyclerView.setAdapter(adapter);

        databaseService = DatabaseService.getInstance();

        filterLayout = findViewById(R.id.filterLayout);
        btnToggleFilters = findViewById(R.id.btnToggleFilters);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);

        etSearchName = findViewById(R.id.etSearchName);
        etMinPlayers = findViewById(R.id.etMinPlayers);
        etMaxPlayers = findViewById(R.id.etMaxPlayers);
        etMinAge = findViewById(R.id.etMinAge);
        etMaxAge = findViewById(R.id.etMaxAge);
        etMaxTime = findViewById(R.id.etMaxTime);

        spinnerLevel = findViewById(R.id.spinnerLevel);
        spinnerBallColor = findViewById(R.id.spinnerBallColor);
        spinnerCourtSize = findViewById(R.id.spinnerCourtSize);

        // Map all 12 checkboxes
        cbForehand = findViewById(R.id.cbForehand);
        cbBackhand = findViewById(R.id.cbBackhand);
        cbServe = findViewById(R.id.cbServe);
        cbVolleyFh = findViewById(R.id.cbVolleyFh);
        cbVolleyBh = findViewById(R.id.cbVolleyBh);
        cbSmash = findViewById(R.id.cbSmash);
        cbDriveFh = findViewById(R.id.cbDriveFh);
        cbDriveBh = findViewById(R.id.cbDriveBh);
        cbForwardFh = findViewById(R.id.cbForwardFh);
        cbForwardBh = findViewById(R.id.cbForwardBh);
        cbSliceFh = findViewById(R.id.cbSliceFh);
        cbSliceBh = findViewById(R.id.cbSliceBh);

        btnToggleFilters.setOnClickListener(this);
        btnApplyFilters.setOnClickListener(this);
        btnClearFilters.setOnClickListener(this);
    }

    private void setupSpinners() {
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelOptions);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(levelAdapter);

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colorOptions);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBallColor.setAdapter(colorAdapter);

        ArrayAdapter<String> courtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courtOptions);
        courtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourtSize.setAdapter(courtAdapter);
    }

    private void setupSearchListener() {
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void loaddrills() {
        databaseService.getAllDrills(new DatabaseService.DrillsCallback() {
            @Override
            public void onSuccess(List<Drill2v> drills) {
                allDrillsList.clear();
                allDrillsList.addAll(drills);

                drillList.clear();
                drillList.addAll(drills);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                // Log or Toast error here
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnToggleFilters) {
            if (filterLayout.getVisibility() == View.GONE) {
                filterLayout.setVisibility(View.VISIBLE);
            } else {
                filterLayout.setVisibility(View.GONE);
            }
        }
        else if (v.getId() == R.id.btnApplyFilters) {
            applyFilters();
            filterLayout.setVisibility(View.GONE);
        }
        else if (v.getId() == R.id.btnClearFilters) {
            clearAllFilters();
        }
    }

    private void clearAllFilters() {
        // איפוס שדות טקסט
        etSearchName.setText("");
        etMinPlayers.setText("");
        etMaxPlayers.setText("");
        etMinAge.setText("");
        etMaxAge.setText("");
        etMaxTime.setText("");

        // איפוס ספינרים (בחזרה לאינדקס 0 = "הכל")
        spinnerLevel.setSelection(0);
        spinnerBallColor.setSelection(0);
        spinnerCourtSize.setSelection(0);

        // איפוס צ'קבוקסים
        cbForehand.setChecked(false);
        cbBackhand.setChecked(false);
        cbServe.setChecked(false);
        cbVolleyFh.setChecked(false);
        cbVolleyBh.setChecked(false);
        cbSmash.setChecked(false);
        cbDriveFh.setChecked(false);
        cbDriveBh.setChecked(false);
        cbForwardFh.setChecked(false);
        cbForwardBh.setChecked(false);
        cbSliceFh.setChecked(false);
        cbSliceBh.setChecked(false);

        // הפעלה מחדש כדי להחזיר את כל התרגילים לרשימה
        applyFilters();
    }

    private void applyFilters() {
        String queryName = etSearchName.getText().toString().toLowerCase().trim();

        String queryLevel = spinnerLevel.getSelectedItemPosition() > 0 ? spinnerLevel.getSelectedItem().toString() : "";
        String queryColor = spinnerBallColor.getSelectedItemPosition() > 0 ? spinnerBallColor.getSelectedItem().toString() : "";
        String queryCourt = spinnerCourtSize.getSelectedItemPosition() > 0 ? spinnerCourtSize.getSelectedItem().toString() : "";

        int minPlayers = parseStringToInt(etMinPlayers.getText().toString(), 0);
        int maxPlayers = parseStringToInt(etMaxPlayers.getText().toString(), Integer.MAX_VALUE);
        int minAge = parseStringToInt(etMinAge.getText().toString(), 0);
        int maxAge = parseStringToInt(etMaxAge.getText().toString(), Integer.MAX_VALUE);
        int maxTime = parseStringToInt(etMaxTime.getText().toString(), Integer.MAX_VALUE);

        drillList.clear();

        for (Drill2v drill : allDrillsList) {
            boolean isMatch = true;

            if (!queryName.isEmpty() && (drill.getName() == null || !drill.getName().toLowerCase().contains(queryName))) {
                isMatch = false;
            }

            if (!queryLevel.isEmpty() && (drill.getLevel() == null || !drill.getLevel().contains(queryLevel))) isMatch = false;
            if (!queryColor.isEmpty() && (drill.getBallColor() == null || !drill.getBallColor().contains(queryColor))) isMatch = false;
            if (!queryCourt.isEmpty() && (drill.getCourtSize() == null || !drill.getCourtSize().contains(queryCourt))) isMatch = false;

            int drillMinPlayers = parseStringToInt(drill.getMinplayers(), 0);
            int drillMaxPlayers = parseStringToInt(drill.getMaxplayers(), Integer.MAX_VALUE);
            if (drillMinPlayers < minPlayers || drillMaxPlayers > maxPlayers) isMatch = false;

            int drillAge = parseStringToInt(drill.getAge(), -1);
            if (drillAge != -1 && (drillAge < minAge || drillAge > maxAge)) isMatch = false;

            int drillTime = parseStringToInt(drill.getTime(), -1);
            if (drillTime != -1 && drillTime > maxTime) isMatch = false;

            if (cbForehand.isChecked() && !Boolean.TRUE.equals(drill.getForehand())) isMatch = false;
            if (cbBackhand.isChecked() && !Boolean.TRUE.equals(drill.getBackhand())) isMatch = false;
            if (cbServe.isChecked() && !Boolean.TRUE.equals(drill.getServe())) isMatch = false;
            if (cbVolleyFh.isChecked() && !Boolean.TRUE.equals(drill.getVolleyforehand())) isMatch = false;
            if (cbVolleyBh.isChecked() && !Boolean.TRUE.equals(drill.getVolleybackhand())) isMatch = false;
            if (cbSmash.isChecked() && !Boolean.TRUE.equals(drill.getSmash())) isMatch = false;
            if (cbDriveFh.isChecked() && !Boolean.TRUE.equals(drill.getDriveforehand())) isMatch = false;
            if (cbDriveBh.isChecked() && !Boolean.TRUE.equals(drill.getDrivebackhand())) isMatch = false;
            if (cbForwardFh.isChecked() && !Boolean.TRUE.equals(drill.getForwardforehand())) isMatch = false;
            if (cbForwardBh.isChecked() && !Boolean.TRUE.equals(drill.getForwardbackhand())) isMatch = false;
            if (cbSliceFh.isChecked() && !Boolean.TRUE.equals(drill.getSliceforehand())) isMatch = false;
            if (cbSliceBh.isChecked() && !Boolean.TRUE.equals(drill.getSlicebackhand())) isMatch = false;

            if (isMatch) {
                drillList.add(drill);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private int parseStringToInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            String numericValue = value.replaceAll("[^0-9]", "");
            if (numericValue.isEmpty()) return defaultValue;
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}