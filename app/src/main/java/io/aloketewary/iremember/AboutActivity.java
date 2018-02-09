package io.aloketewary.iremember;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private Toolbar mAboutToolbar;
    private TextView mAbhiTwitter, mAlokeTwitter;

    private LinearLayout mAbhiLayout, mAlokeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAboutToolbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(mAboutToolbar);
        getSupportActionBar().setTitle("About iRemember");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAlokeTwitter = findViewById(R.id.aloke_twitter);
        mAbhiTwitter = findViewById(R.id.abhi_twitter);

        mAbhiLayout = findViewById(R.id.about_idea_by);
        mAlokeLayout = findViewById(R.id.about_dev_by);

        mAbhiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String twitterLoc_abhi = "https://twitter.com/abhishekaggrawa";
                openWebPage(twitterLoc_abhi);
            }
        });

        mAlokeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String twitterLoc_aloke = "https://twitter.com/aloketewary";
                openWebPage(twitterLoc_aloke);
            }
        });
    }

    /**
     * Open a web page of a specified URL
     *
     * @param url URL to open
     */
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
