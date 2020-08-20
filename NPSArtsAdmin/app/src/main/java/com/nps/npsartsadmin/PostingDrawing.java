
 package com.nps.npsartsadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;

 public class PostingDrawing extends AppCompatActivity {

     private   Toolbar  toolbar;
     private ImageView imageView,getImageUri;
     private  int getFontSize=18;
     private  String fontColor="";
     private  int backGroundColor=0;
     private EditText getHeading;
     private TextView userNameInPost;
     private  TextView buttonPostArt;
     private DatabaseReference userRef,pendingRef;
     private FirebaseAuth mAuth;
     private  String currentUser;
     private  String date,time,timeToShow;
     private  String randomValue;
     private  long postCounter;
     private   String articles="",headings="";
     private   String getUserN,getProLink;
     private CircularImageView userPofileInPost;
     private   String height,postType="image",message="none";
     private   TextView postTitle,textback;
     Bitmap compressedImage;
     StorageReference storePostImage;
     byte[] finalImage;
     String imageLink;
     Uri imageUri;
     private final int Gallary_pick=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_drawing);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        Typeface aveny=Typeface.createFromAsset(getAssets(),"font/AvenyTRegular.otf");
        getHeading=(EditText)findViewById(R.id.photocontainCaption);
        toolbar=(Toolbar)findViewById(R.id.photopostTool);
        setSupportActionBar(toolbar);

        userPofileInPost=(CircularImageView)findViewById(R.id.photouserPostProfile);
        userNameInPost=(TextView)findViewById(R.id.photopostUserName);
        getImageUri=(ImageView)findViewById(R.id.photouploadImage);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        pendingRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        storePostImage= FirebaseStorage.getInstance().getReference().child("PostImages");
        ActionBar actionBar=getSupportActionBar();
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.post_drawing_tool_layout,null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(view);
        imageView=(ImageView)findViewById(R.id.photobackPressedButton);
        postTitle=(TextView)findViewById(R.id.photouserPostTitle);
        buttonPostArt=(TextView)findViewById(R.id.photopostButton);
        postTitle.setTypeface(roboto);
        buttonPostArt.setTypeface(roboto);
        getCurrentUserInfo();
        buttonPostArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalImage==null){
                    Toast.makeText(PostingDrawing.this, "Please select an image", Toast.LENGTH_SHORT).show();

                }
                else {
                    new SpotsDialog.Builder()
                            .setMessage("Uploading")
                            .setCancelable(false)
                            .setContext(PostingDrawing.this)
                            .build()
                            .show();
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
                    String date=dateFormat.format(calendar.getTime());
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
                    String time=simpleDateFormat.format(calendar.getTime());
                    final String random=date+time;
                    StorageReference storagePath=storePostImage.child(random+".jpg");
                    UploadTask uploadTask=storagePath.putBytes(finalImage);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> image_path_url=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            image_path_url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageLink=uri.toString();
                                      if(imageLink.isEmpty()){
                                          Toast.makeText(PostingDrawing.this, "re-select your image", Toast.LENGTH_SHORT).show();

                                      return;
                                      }
                                      else {
                                          SavePostInFo();
                                      }
                                }
                            });

                        }

                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(PostingDrawing.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            int pro=progress.intValue();
                            Toast.makeText(PostingDrawing.this, ""+pro, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
        getHeading.setTypeface(aveny);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getImageUri.setOnClickListener(new View.OnClickListener() {
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

         if (requestCode==Gallary_pick && resultCode==RESULT_OK){
             Uri imagePath=data.getData();
             CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON).start(PostingDrawing.this);
         }
         if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
             CropImage.ActivityResult result=CropImage.getActivityResult(data);
             try {
                 imageUri=result.getUri();
                 getImageUri.setImageURI(imageUri);
                 File filePath=new File(imageUri.getPath());
                 try {
                     compressedImage = new Compressor(this)
                             .setMaxWidth(480)
                             .setMaxHeight(500)
                             .setQuality(50)
                             .setCompressFormat(Bitmap.CompressFormat.JPEG)
                             .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                     Environment.DIRECTORY_PICTURES).getAbsolutePath())
                             .compressToBitmap(filePath);
                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                     finalImage = baos.toByteArray();
                 }
                 catch (Exception e){
                     e.printStackTrace();
                     Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
                 }
             }
             catch (Exception e){
                 e.printStackTrace();
                 Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
             }
         }

     }
     private void getCurrentUserInfo() {
         userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 if (dataSnapshot.exists()){

                     if (dataSnapshot.hasChild("fullname")){
                         getUserN=dataSnapshot.child("fullname").getValue().toString();
                         userNameInPost.setText(getUserN);
                     }

                     if (dataSnapshot.hasChild("profileLink")){

                         getProLink=dataSnapshot.child("profileLink").getValue().toString();
                         Picasso.with(PostingDrawing.this).load(getProLink).placeholder(R.drawable.profile).into(userPofileInPost);

                     }
                     else {
                         Toast.makeText(PostingDrawing.this, "NO User Name and Profile Image", Toast.LENGTH_SHORT).show();
                     }
                 }


             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }

     private void SavePostInFo() {
         height="";
         headings=getHeading.getText().toString();
         pendingRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     postCounter=dataSnapshot.getChildrenCount();
                 }
                 else {
                     postCounter=0;
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
             }
         });
         userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 final String userFullName=dataSnapshot.child("fullname").getValue().toString();
                 String userProLink=dataSnapshot.child("profileLink").getValue().toString();
                 Calendar calendar=Calendar.getInstance();
                 SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
                 date=simpleDateFormat.format(calendar.getTime());
                 SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("HH:mm:ss");
                 time=simpleDateFormat1.format(calendar.getTime());
                 SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("HH:mm a");
                 timeToShow=simpleDateFormat2.format(calendar.getTime());
                 randomValue=currentUser+date+time;
                 String backInString=String.valueOf(backGroundColor);
                 String fontsizeInString=String.valueOf(getFontSize);
                 HashMap putPostInfo=new HashMap();
                 putPostInfo.put("uid",currentUser);
                 putPostInfo.put("userName",userFullName);
                 putPostInfo.put("userProLink",userProLink);
                 putPostInfo.put("date",date);
                 putPostInfo.put("time",timeToShow);
                 putPostInfo.put("articles",articles);
                 putPostInfo.put("backGround",backInString);
                 putPostInfo.put("fontSize",fontsizeInString);
                 putPostInfo.put("fontColor",fontColor);
                 putPostInfo.put("counter",postCounter);
                 putPostInfo.put("heading",headings);
                 putPostInfo.put("height",height);
                 putPostInfo.put("verifiedPost","no");
                 putPostInfo.put("postType","image");
                 putPostInfo.put("message",message);
                 putPostInfo.put("imageUrl",imageLink);
                 pendingRef.child(randomValue).updateChildren(putPostInfo).addOnCompleteListener(new OnCompleteListener() {
                     @Override
                     public void onComplete(@NonNull Task task) {
                         if (task.isSuccessful()){
                             Toast.makeText(PostingDrawing.this, "Uploading done", Toast.LENGTH_SHORT).show();
                             onBackPressed();
                         }
                         else {

                             String mess=task.getException().toString();
                             Toast.makeText(PostingDrawing.this, "Error occurred"+mess, Toast.LENGTH_SHORT).show();
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

 }
