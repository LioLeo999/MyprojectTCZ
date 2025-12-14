package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;

public class ShowDrillVideo extends AppCompatActivity {

    private WebView webView;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_drill_video);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        link = intent.getStringExtra("link");

        webView = findViewById(R.id.youtubevideo);

        // הגדרות חובה ל-YouTube
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient());

        String videoId = extractVideoId(link);
        if (videoId == null) {
            finish();
            return;
        }

        String html =
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<body style='margin:0'>" +
                        "<iframe " +
                        "width='100%' " +
                        "height='100%' " +
                        "src='https://www.youtube.com/embed/" + videoId + "' " +
                        "frameborder='0' " +
                        "allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture' " +
                        "allowfullscreen>" +
                        "</iframe>" +
                        "</body>" +
                        "</html>".formatted(videoId);

        webView.loadDataWithBaseURL(
                "https://www.youtube.com",
                html,
                "text/html",
                "utf-8",
                null
        );
    }

    // חילוץ videoId מכל סוג קישור
    private String extractVideoId(String youtubeUrl) {
        if (youtubeUrl == null) return null;

        String videoId = null;

        if (youtubeUrl.contains("watch?v=")) {
            videoId = youtubeUrl.substring(youtubeUrl.indexOf("v=") + 2);
        } else if (youtubeUrl.contains("youtu.be/")) {
            videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
        } else if (youtubeUrl.contains("shorts/")) {
            videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
        }

        if (videoId == null) return null;

        // ניקוי פרמטרים מיותרים
        if (videoId.contains("&")) {
            videoId = videoId.substring(0, videoId.indexOf("&"));
        }

        return videoId;
    }
}
