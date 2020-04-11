package com.jaysonm.meetm.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.textview.MaterialTextView;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.util.FontUtil;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        MaterialTextView toolbarTitle = findViewById(R.id.custom_tool_bar_title);
        toolbarTitle.setTypeface(FontUtil.getCustomTypeFace(AboutUsActivity.this, FontUtil.CUSTOM_FONTS.LEMON));

        AppCompatImageButton toolbarButton = findViewById(R.id.custom_tool_bar_button);
        toolbarButton.setImageResource(R.drawable.ic_close_custom_24dp);

        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });
    }
}
