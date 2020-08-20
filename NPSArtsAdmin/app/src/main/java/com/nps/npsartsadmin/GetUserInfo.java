package com.nps.npsartsadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class GetUserInfo extends AppCompatActivity {
  private   DatabaseReference userRef;
  private   StorageReference storeProfileImage;
  private   ImageView imageView;
  private   final int Galler_Pick=1;
  private   EditText getFullname;
  private   FirebaseAuth currentUserAuth;
  private   String currentUser;
  private   Uri imageUri;
  private   Bitmap compressedImage;
  private   String proLink="";
  private   ProgressDialog dialog;
  private   Button saveInfo;
  private Spinner getOptions;
  ProgressDialog progressDialog;
  Toolbar configTool;
  private String getUserType="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);
        dialog=new ProgressDialog(this);
        getFullname=(EditText)findViewById(R.id.userFullName);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        getOptions=(Spinner)findViewById(R.id.getUserType);
        currentUserAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        try {
            currentUser=currentUserAuth.getCurrentUser().getUid();
        }
        catch (Exception e){
            startActivity(new Intent(GetUserInfo.this,LogInOptions.class));
            finish();
        }
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        configTool=(Toolbar)findViewById(R.id.configureTool);
        setSupportActionBar(configTool);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.configurepro_tool,null);
        actionBar.setCustomView(view);
        TextView configText=(TextView)findViewById(R.id.configureProHeading);
        configText.setTypeface(roboto);
        final String []userType={"Guest","Teacher","Class One","Class Two","Class Three","Class Four","Class Five","Class Six","Class Seven","Class Eight","Class Nine","Class Ten"};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,userType);
        getOptions.setAdapter(arrayAdapter);
        getOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        getUserType="Guest";
                        break;
                    case 1:
                        getUserType="Teacher";
                        break;
                    case 2:
                        getUserType="Class One";
                        break;
                    case 3:
                        getUserType="Class Two";
                        break;
                    case 4:
                        getUserType="Class Three";
                        break;
                    case 5:
                        getUserType="Class Four";
                        break;
                    case 6:
                        getUserType="Class Five";
                        break;
                    case 7:
                        getUserType="Class Six";
                        break;
                    case 8:
                        getUserType="Class Seven";
                        break;
                    case 9:
                        getUserType="Class Eight";
                        break;
                    case 10:
                        getUserType="Class Nine";
                        break;
                    case 11:
                        getUserType="Class Ten";
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        storeProfileImage= FirebaseStorage.getInstance().getReference().child("ProfileImages");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        imageView=(ImageView)findViewById(R.id.userProfileHolder);
        saveInfo=(Button)findViewById(R.id.saveInfo);
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=getFullname.getText().toString();
                if (username.isEmpty()){
                    Snackbar snackbar=Snackbar.make(findViewById(R.id.setProRelative),"Enter your name first",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                if (proLink.isEmpty()){
                    Snackbar snackbar=Snackbar.make(findViewById(R.id.setProRelative),"Re-select your profile picture something went wrong",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                if (getUserType.isEmpty()){
                    Toast.makeText(GetUserInfo.this, "Select Option first", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    progressDialog.setMessage("Saving your information");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                     String tagname=username.toLowerCase();
                    HashMap putUserInfo=new HashMap();
                    putUserInfo.put("profileLink",proLink);
                    putUserInfo.put("fullname",username);
                    putUserInfo.put("verified","no");
                    putUserInfo.put("tagname",tagname);
                    putUserInfo.put("AccountStatus","Enabled");
                    putUserInfo.put("permission","no");
                    putUserInfo.put("userType",getUserType);
                    userRef.child(currentUser).updateChildren(putUserInfo).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                             if (task.isSuccessful()){
                                 Snackbar snackbar=Snackbar.make(findViewById(R.id.setProRelative),"Your information is saved",Snackbar.LENGTH_SHORT);
                                 snackbar.show();
                                 progressDialog.dismiss();
                                 startActivity(new Intent(GetUserInfo.this,LogIn.class));
                                 finish();
                             }
                             else {
                                 progressDialog.dismiss();
                                 Snackbar snackbar=Snackbar.make(findViewById(R.id.setProRelative),"Something went wrong",Snackbar.LENGTH_SHORT);
                                   snackbar.show();
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
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON).start(GetUserInfo.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                dialog.setMessage("saving your selfi please wait...");
                dialog.setCanceledOnTouchOutside(false);
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
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.setProRelative), "Image is not Selected Yet", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    dialog.dismiss();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.setProRelative), "No Image selected", Snackbar.LENGTH_SHORT);
                snackbar.show();
                dialog.dismiss();
            }
        }


    }

    @Override
    public void onBackPressed() {

    }
}
