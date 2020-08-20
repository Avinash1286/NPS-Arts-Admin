package com.nps.npsartsadmin;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import dmax.dialog.SpotsDialog;
public class LogInOptions extends AppCompatActivity {
    Button signInWithPhone;
    TextView logInHeading;
    DatabaseReference userRef;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_options);
        Typeface avenyInstaRegular=Typeface.createFromAsset(getAssets(),"font/FacebookNarrow_A_Rg.ttf");
          logInHeading=(TextView)findViewById(R.id.welcomeHeading);
          logInHeading.setTypeface(avenyInstaRegular);
          mAuth=FirebaseAuth.getInstance();
          progressDialog=new ProgressDialog(this);
          signInWithPhone=(Button)findViewById(R.id.logInPhoneNumber);
          userRef= FirebaseDatabase.getInstance().getReference().child("Users");
          signInWithPhone.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(LogInOptions.this,PhoneNumberAuth.class));
              }
          });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser!=null){
            startActivity(new Intent(LogInOptions.this,ShowPost.class));
            finish();
        }

    }

}
