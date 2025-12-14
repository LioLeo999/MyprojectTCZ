package com.example.myprojecttcz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myprojecttcz.R;

public class ShowDrillVideo extends AppCompatActivity {

    private Intent get;
    private WebView webView;
    private String link, embed;

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
    public void initViews(){
        get = getIntent();
        link = get.getStringExtra("link");
        webView = findViewById(R.id.youtubevideo);
        embed = convertToEmbedUrl(link);
        if (embed != null) {
            webView.loadUrl(embed);
        }


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

    }
    private String convertToEmbedUrl(String youtubeUrl) {
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

        return "https://www.youtube.com/embed/" + videoId;
    }
}