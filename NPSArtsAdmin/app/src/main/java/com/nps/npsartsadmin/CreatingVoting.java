package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
public class CreatingVoting extends AppCompatActivity {
    String getChild,imageLink;
    Toolbar toolbar;
    ImageView setOnBack,getPic;
    EditText getEssay;
    TextView upladVotingArts;
    TextInputEditText getCandidateNamess,getHeadingForVoting;
    DatabaseReference votingRef;
    final int Gallary_pick=1;
    Uri imageUri;
    Bitmap compressedImage;
    byte[] finalImage;
    StorageReference storeHandWritingAndDrawing;
    TextView setHeading;
    TextInputLayout textInputLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_voting);
         getChild=getIntent().getExtras().get("type").toString();
         toolbar=(Toolbar)findViewById(R.id.votingTool);
            setActionBar(toolbar);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
         ActionBar actionBar=getActionBar();
         actionBar.setDisplayShowCustomEnabled(true);
         LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View view=layoutInflater.inflate(R.layout.voting_toollayout,null);
         actionBar.setCustomView(view);
         actionBar.setDisplayHomeAsUpEnabled(false);
         actionBar.setDisplayShowHomeEnabled(false);
         votingRef= FirebaseDatabase.getInstance().getReference().child("VotingSection").child(getChild);
         storeHandWritingAndDrawing= FirebaseStorage.getInstance().getReference().child("HandWritingAndDrawing");
         setHeading=(TextView)findViewById(R.id.setHeading);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        setHeading.setTypeface(roboto);
         setHeading.setText("Upload "+getChild);
         setOnBack=(ImageView)findViewById(R.id.onBackVoting);
         getPic=(ImageView)findViewById(R.id.containHandWriting);
         getEssay=(EditText)findViewById(R.id.containEssay);
         upladVotingArts=(TextView)findViewById(R.id.uploadHandWriting);
         upladVotingArts.setTypeface(roboto);
         getCandidateNamess=(TextInputEditText)findViewById(R.id.containCandidateName);
         getHeadingForVoting=(TextInputEditText)findViewById(R.id.containHeadingForVoting);
         textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayout1);
         if (getChild.equals("Poem")){
             getEssay.setHint("Write a poem here...");
             getHeadingForVoting.setHint("Poem's Title");
         }
         if (getChild.equals("Essay")){
             getEssay.setHint("Write an essay here...");
             getHeadingForVoting.setHint("Essay's Title");
         }
         setOnBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onBackPressed();
             }
         });
         if (getChild.equals("Handwriting") || getChild.equals("Drawing")){
             getPic.setVisibility(View.VISIBLE);
             getEssay.setVisibility(View.GONE);
             textInputLayout.setVisibility(View.GONE);

         }
         else {
             getPic.setVisibility(View.GONE);
             getEssay.setVisibility(View.VISIBLE);
             textInputLayout.setVisibility(View.VISIBLE);
         }
         getPic.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                 intent.setType("image/*");
                 startActivityForResult(intent,Gallary_pick);
             }
         });
         upladVotingArts.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (checkForConnctoin()){
                     if (getChild.equals("Handwriting")){
                         UploadHandwriting();
                         return;
                     }
                     if (getChild.equals("Drawing")){
                         UploadDrawing();
                         return;
                     }
                     if (getChild.equals("Poem")){
                         UploadPoems();
                         return;
                     }
                     if (getChild.equals("Essay")){
                         UploadEssay();
                     }
                 }
                 else {
                     Toast.makeText(CreatingVoting.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                 }
             }
         });
    }
    private boolean checkForConnctoin() {
        ConnectivityManager cm=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){

            return true;

        }
        else {
            return false;
        }

    }

    private void UploadPoems() {
        final String candidateNamess=getCandidateNamess.getText().toString();
        final String essay=getEssay.getText().toString();
        final String title=getHeadingForVoting.getText().toString();
        if (candidateNamess.isEmpty()){
            Toast.makeText(CreatingVoting.this, "Candidate's name is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (essay.isEmpty()){
            Toast.makeText(CreatingVoting.this, "Select an Image!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.isEmpty()){
            Toast.makeText(this, "Title is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            new SpotsDialog.Builder().setContext(CreatingVoting.this).setMessage("Uploading Poem").setCancelable(false).build().show();
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
            String date=dateFormat.format(calendar.getTime());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
            String time=simpleDateFormat.format(calendar.getTime());
            final String random=date+time;
            HashMap putVotingInfo=new HashMap();
            putVotingInfo.put("Composer",candidateNamess);
            putVotingInfo.put("Poem",essay);
            putVotingInfo.put("Heading",title);
            votingRef.child(random).updateChildren(putVotingInfo).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                         Toast.makeText(CreatingVoting.this, "Uploading done!", Toast.LENGTH_SHORT).show();
                          onBackPressed();
                    }
                    else {
                        Toast.makeText(CreatingVoting.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            });
        }
    }
    private void UploadDrawing() {
        final String artistName=getCandidateNamess.getText().toString();
        if (artistName.isEmpty()){
            Toast.makeText(CreatingVoting.this, "Please enter a candidate's name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finalImage==null){
            Toast.makeText(CreatingVoting.this, "Please select a Handwriting or Drawing", Toast.LENGTH_SHORT).show();
        }
        else {
            new SpotsDialog.Builder().setContext(CreatingVoting.this).setMessage("Uploading Drawing").setCancelable(false).build().show();
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
            String date=dateFormat.format(calendar.getTime());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
            String time=simpleDateFormat.format(calendar.getTime());
            final String random=date+time;
            StorageReference storagePath=storeHandWritingAndDrawing.child(random+".jpg");
            UploadTask uploadTask=storagePath.putBytes(finalImage);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> image_path_url=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    image_path_url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageLink=uri.toString();
                            HashMap putHandwritingAndDrawing=new HashMap();
                            putHandwritingAndDrawing.put("ArtistName",artistName);
                            putHandwritingAndDrawing.put("ImageOFDrawing",imageLink);
                            votingRef.child(random).updateChildren(putHandwritingAndDrawing).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(CreatingVoting.this, "Uploading done!", Toast.LENGTH_SHORT).show();
                                      onBackPressed();
                                    }
                                    else {
                                        Toast.makeText(CreatingVoting.this, "Uploading failed!", Toast.LENGTH_SHORT).show();
                                     onBackPressed();
                                    }

                                }
                            });

                        }
                    });
                }

            });
        }

    }

    private void UploadEssay() {
        final String candidateNamess=getCandidateNamess.getText().toString();
        final String essay=getEssay.getText().toString();
        final String title=getHeadingForVoting.getText().toString();
        if (candidateNamess.isEmpty()){
            Toast.makeText(CreatingVoting.this, "Candidate's name is empty!", Toast.LENGTH_SHORT).show();
         return;
        }
        if (essay.isEmpty()){
            Toast.makeText(CreatingVoting.this, "Select an Image!", Toast.LENGTH_SHORT).show();
          return;
        }
        if (title.isEmpty()){
            Toast.makeText(this, "Title is empty!", Toast.LENGTH_SHORT).show();
        }

        else {
            new SpotsDialog.Builder().setContext(CreatingVoting.this).setMessage("Uploading Essay").setCancelable(false).build().show();
            Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
                    String date=dateFormat.format(calendar.getTime());
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
                    String time=simpleDateFormat.format(calendar.getTime());
                    final String random=date+time;
                    HashMap putVotingInfo=new HashMap();
                    putVotingInfo.put("Author",candidateNamess);
                    putVotingInfo.put("Essay",essay);
                    putVotingInfo.put("Titles",title);
                    votingRef.child(random).updateChildren(putVotingInfo).addOnCompleteListener(new OnCompleteListener() {
                      @Override
                      public void onComplete(@NonNull Task task) {
                       if (task.isSuccessful()){
                             onBackPressed();
                           Toast.makeText(CreatingVoting.this, "Uploading done!", Toast.LENGTH_SHORT).show();

                       }
                       else {
                           onBackPressed();
                           Toast.makeText(CreatingVoting.this, "Uploading Failed", Toast.LENGTH_SHORT).show();

                       }
                      }
                  });
        }
    }
    private void UploadHandwriting() {
        final String artistName=getCandidateNamess.getText().toString();
        if (artistName.isEmpty()){
            Toast.makeText(CreatingVoting.this, "Please enter a candidate's name", Toast.LENGTH_SHORT).show();
        return;
        }
        if (finalImage==null){
            Toast.makeText(CreatingVoting.this, "Please select a Handwriting or Drawing", Toast.LENGTH_SHORT).show();
        }
        else {
            new SpotsDialog.Builder().setContext(CreatingVoting.this).setMessage("Uploading Handwriting").setCancelable(false).build().show();
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
            String date=dateFormat.format(calendar.getTime());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
            String time=simpleDateFormat.format(calendar.getTime());
            final String random=date+time;
            StorageReference storagePath=storeHandWritingAndDrawing.child(random+".jpg");
            UploadTask uploadTask=storagePath.putBytes(finalImage);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> image_path_url=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    image_path_url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageLink=uri.toString();
                                    HashMap putHandwritingAndDrawing=new HashMap();
                                    putHandwritingAndDrawing.put("HandwritingWriterName",artistName);
                                    putHandwritingAndDrawing.put("ImageOFHandwriting",imageLink);
                                   votingRef.child(random).updateChildren(putHandwritingAndDrawing).addOnCompleteListener(new OnCompleteListener() {
                                       @Override
                                       public void onComplete(@NonNull Task task) {
                                           if (task.isSuccessful()){
                                               Toast.makeText(CreatingVoting.this, "Uploading done!", Toast.LENGTH_SHORT).show();
                                                      onBackPressed();
                                           }
                                           else {
                                               Toast.makeText(CreatingVoting.this, "Uploading failed!", Toast.LENGTH_SHORT).show();
                                                 onBackPressed();
                                           }

                                       }
                                   });

                        }
                    });
                }

            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==Gallary_pick && resultCode==RESULT_OK){
            Uri imagePath=data.getData();
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON).start(CreatingVoting.this);
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            try {
                imageUri=result.getUri();
                getPic.setImageURI(imageUri);
                File filePath=new File(imageUri.getPath());
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
}
