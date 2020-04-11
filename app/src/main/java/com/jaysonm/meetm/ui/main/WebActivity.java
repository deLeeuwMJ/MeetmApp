package com.jaysonm.meetm.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.google.android.material.textview.MaterialTextView;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.ui.login.LoginActivity;
import com.jaysonm.meetm.util.FontUtil;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        MaterialTextView toolbarTitle = findViewById(R.id.custom_tool_bar_title);
        toolbarTitle.setTypeface(FontUtil.getCustomTypeFace(WebActivity.this, FontUtil.CUSTOM_FONTS.LEMON));

        AppCompatImageButton toolbarButton = findViewById(R.id.custom_tool_bar_button);
        toolbarButton.setImageResource(R.drawable.ic_close_custom_24dp);

        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });

        WebView webView = (WebView) findViewById(R.id.webview);
        String url = getIntent().getStringExtra("url");

        webView.loadUrl(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
