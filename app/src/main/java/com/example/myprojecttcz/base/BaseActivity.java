package com.example.myprojecttcz.base;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // הערה: אנחנו לא עושים כאן setContentView רגיל, זה יקרה בפונקציה למטה
    }

    /**
     * הפונקציה הזו נקראת ע"י כל האקטיביטיז שיורשות מ-BaseActivity.
     * במקום להחליף את המסך, היא לוקחת את העיצוב של הבן ומכניסה אותו לתוך ה-FrameLayout של האבא.
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // 1. טוענים את ה-Layout של הבסיס (עם ה-Toolbar)
        View fullView = getLayoutInflater().inflate(R.layout.activity_base, null);

        // 2. מוצאים את ה-FrameLayout
        FrameLayout activityContainer = fullView.findViewById(R.id.framelayout);

        // 3. מנפחים את ה-Layout של הבן (למשל activity_main) לתוך ה-Container
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        // 4. מגדירים את המסך המלא כ-Content View
        super.setContentView(fullView);

        // 5. הגדרות Toolbar וכפתור חזור
        initializeToolbar();

        // 6. הגדרות Insets (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.basetoolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar); // הופך את ה-Toolbar ל-ActionBar פעיל

            // בדיקה האם להציג כפתור חזור
            if (getSupportActionBar() != null) {
                boolean showBack = shouldShowBackButton();
                getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
                getSupportActionBar().setDisplayShowHomeEnabled(showBack);
            }
        }
    }

    // פונקציה שמאפשרת לאקטיביטיז הבנים להחליט אם להראות חץ חזור או לא
    public boolean shouldShowBackButton() {
        return true; // ברירת מחדל: כן
    }

    // טיפול בלחיצה על כפתור החזור ב-Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // סוגר את המסך וחוזר אחורה
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}