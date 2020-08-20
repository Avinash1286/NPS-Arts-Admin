package com.nps.npsartsadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;

public class PostingCreativeHands extends AppCompatActivity {

    ImageView getCreativeHandsImage;
    TextInputEditText getArtistName;
    Button uploadAllCreativeHands;
    Bitmap compressedImage;
    Toolbar creativeTool;
    StorageReference storeCreativeHandsImage;
    byte[] finalImage;
    String imageLink;
    Uri imageUri;
    long creativeCounter;

    private final int Gallary_pick=1;
    DatabaseReference storeCreativeImageInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_creative_hands);
        getArtistName=(TextInputEditText)findViewById(R.id.getArtistName);
        creativeTool=(Toolbar)findViewById(R.id.postionCreativeTool);
        setSupportActionBar(creativeTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        headingPendingPost.setTypeface(roboto);
        headingPendingPost.setText("Upload Creative Hands");
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        storeCreativeHandsImage= FirebaseStorage.getInstance().getReference().child("CreativeHandsImage");
        storeCreativeImageInfo= FirebaseDatabase.getInstance().getReference().child("CreativeHandsInfo");
        getCreativeHandsImage=(ImageView)findViewById(R.id.uploadImageForCreativeHands);
        uploadAllCreativeHands=(Button)findViewById(R.id.uploadButtonForCreativeHands);
        storeCreativeImageInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    creativeCounter=dataSnapshot.getChildrenCount();
                }
                else {
                    creativeCounter=0;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        uploadAllCreativeHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String artistName=getArtistName.getText().toString();
                if (artistName.isEmpty()){
                    Toast.makeText(PostingCreativeHands.this, "Please enter a artist's name", Toast.LENGTH_SHORT).show();
                }
                if (finalImage==null){
                    Toast.makeText(PostingCreativeHands.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
                else {

                    new SpotsDialog.Builder().setContext(PostingCreativeHands.this).setMessage("Posting CreativeHands").setCancelable(false).build().show();
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
                    String date=dateFormat.format(calendar.getTime());
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
                    String time=simpleDateFormat.format(calendar.getTime());
                    final String random=date+time;
                    StorageReference storagePath=storeCreativeHandsImage.child(random+".jpg");
                    UploadTask uploadTask=storagePath.putBytes(finalImage);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> image_path_url=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            image_path_url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageLink=uri.toString();
                                 storeCreativeImageInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         HashMap putCreativeHandsInformation=new HashMap();
                                         putCreativeHandsInformation.put("name",artistName);
                                         putCreativeHandsInformation.put("image",imageLink);
                                         putCreativeHandsInformation.put("counter",creativeCounter);
                                         storeCreativeImageInfo.child(random).updateChildren(putCreativeHandsInformation).addOnCompleteListener(new OnCompleteListener() {
                                             @Override
                                             public void onComplete(@NonNull Task task) {
                                                 if (task.isSuccessful()){
                                                     Toast.makeText(PostingCreativeHands.this, "Uploading done!", Toast.LENGTH_SHORT).show();
                                                  onBackPressed();
                                                 }
                                                 else {
                                                     Toast.makeText(PostingCreativeHands.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                     onBackPressed();
                                                 }
                                             }
                                         });
                                     }
                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });
                                }
                            });

                        }

                    });
                }
            }
        });
        getCreativeHandsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallary_pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallary_pick && resultCode == RESULT_OK) {
            Uri imagePath = data.getData();
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON).start(PostingCreativeHands.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                imageUri = result.getUri();
                getCreativeHandsImage.setImageURI(imageUri);
                File filePath = new File(imageUri.getPath());
                try {
                    compressedImage = new Compressor(this)
                            .setMaxWidth(480)
                            .setMaxHeight(800)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToBitmap(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    finalImage = baos.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
