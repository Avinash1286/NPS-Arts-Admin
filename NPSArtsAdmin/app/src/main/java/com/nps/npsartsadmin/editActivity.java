package com.nps.npsartsadmin;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class editActivity extends AppCompatActivity {
    private DatabaseReference userRef;
    private StorageReference storeProfileImage;
    private CircularImageView imageView;
    private   final int Galler_Pick=1;
    private TextInputEditText getFullname;
    private FirebaseAuth currentUserAuth;
    private   String currentUser;
    private Uri imageUri;
    private Bitmap compressedImage;
    private   String proLink="";
    private ProgressDialog dialog;
    private Button saveInfo;
    private String userName,userProLink;
    Toolbar editTool;
    CardView editCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        dialog=new ProgressDialog(this);
        getFullname=(TextInputEditText) findViewById(R.id.edituserFullName);
        currentUserAuth=FirebaseAuth.getInstance();
        currentUser=currentUserAuth.getCurrentUser().getUid();
        storeProfileImage= FirebaseStorage.getInstance().getReference().child("ProfileImages");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        imageView=(CircularImageView) findViewById(R.id.edituserProfileHolder);
        saveInfo=(Button)findViewById(R.id.editsaveInfo);
        editTool=(Toolbar)findViewById(R.id.editToolbar);
        editCard=(CardView)findViewById(R.id.editCard);
        setSupportActionBar(editTool);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.pending_post_tool,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setText("Edit Your profile");
        headingPendingPost.setTypeface(roboto);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForConnctoin()){
                    if (!proLink.isEmpty()){
                        String username=getFullname.getText().toString();
                        if (username.isEmpty()){
                            Toast.makeText(editActivity.this, "Username can't be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            HashMap putUserInfo=new HashMap();
                            putUserInfo.put("profileLink",proLink);
                            putUserInfo.put("fullname",username);
                            putUserInfo.put("tagname",username.toLowerCase());
                            userRef.child(currentUser).updateChildren(putUserInfo).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(editActivity.this, "Update Saved", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(editActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                }
                            });
                        }

                        return;
                    }

                    else{
                        String username=getFullname.getText().toString();
                        if (username.isEmpty()){
                            Toast.makeText(editActivity.this, "Username can't be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            HashMap putUserInfo=new HashMap();
                            putUserInfo.put("fullname",username);
                            putUserInfo.put("tagname",username.toLowerCase());
                            userRef.child(currentUser).updateChildren(putUserInfo).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(editActivity.this, "Update Saved", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(editActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                }
                            });
                        }
                    }
                }
                else {
                    onBackPressed();
                }

            }
        });
        showUserProfile();
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=getFullname.getText().toString();
                if (username.isEmpty()){
                    Snackbar snackbar=Snackbar.make(findViewById(R.id.setProRelative),"Username can't be empty",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                if (proLink.isEmpty()){
                    HashMap putUserInfo=new HashMap();
                    putUserInfo.put("fullname",username);
                    putUserInfo.put("tagname",username.toLowerCase());
                    userRef.child(currentUser).updateChildren(putUserInfo).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(editActivity.this, "Update Saved", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                finish();
                            }
                            else {
                                Toast.makeText(editActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            }
                        }
                    });
                   return;
                }
                else {
                    HashMap putUserInfo=new HashMap();
                    putUserInfo.put("profileLink",proLink);
                    putUserInfo.put("fullname",username);
                    putUserInfo.put("tagname",username.toLowerCase());
                    userRef.child(currentUser).updateChildren(putUserInfo).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(editActivity.this, "Update Saved", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                finish();
                            }
                            else {
                                Toast.makeText(editActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                             }
                        }
                    });
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Galler_Pick);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Galler_Pick && resultCode == RESULT_OK) {
            Uri imagePath = data.getData();
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON).start(editActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                dialog.setMessage("Saving your profile picture");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
                File filePath = new File(imageUri.getPath());
                try {
                    compressedImage = new Compressor(this)
                            .setMaxWidth(250)
                            .setMaxHeight(250)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToBitmap(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] finalImage = baos.toByteArray();
                    StorageReference storagePath = storeProfileImage.child(currentUser + ".jpg");
                    UploadTask uploadTask = storagePath.putBytes(finalImage);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> image_path_url = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            image_path_url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    proLink = uri.toString();
                                    dialog.dismiss();
                                }
                            });
                        }

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No New Image selected", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No New Image selected", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }
    private void showUserProfile() {
        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    userName = dataSnapshot.child("fullname").getValue().toString();
                    userProLink = dataSnapshot.child("profileLink").getValue().toString();
                    Picasso.with(getApplicationContext()).load(userProLink).placeholder(R.drawable.image_placeholder).into(imageView);
                    getFullname.setText(userName);
                    editCard.setVisibility(View.VISIBLE);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(editActivity.this,"Error "+e,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}
