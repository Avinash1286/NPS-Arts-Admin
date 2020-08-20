package com.nps.npsartsadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ShowPostImage extends AppCompatActivity {

    Toolbar showFullImageTool;
    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post_image);
        Typeface aveny=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        String url=getIntent().getExtras().get("url").toString();
        String name=getIntent().getExtras().get("name").toString();
        showFullImageTool=(Toolbar)findViewById(R.id.showFullImageTool);
        setSupportActionBar(showFullImageTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setTypeface(aveny);
        headingPendingPost.setText(name);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        photoView=(PhotoView)findViewById(R.id.showFullImage);
        Picasso.with(this).load(url).placeholder(R.drawable.placeholder).into(photoView);
    }

}
