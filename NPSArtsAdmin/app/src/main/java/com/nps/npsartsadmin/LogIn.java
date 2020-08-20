package com.nps.npsartsadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import dmax.dialog.SpotsDialog;

public class LogIn extends AppCompatActivity {

 private Button signup,login,logInWithGoogle;
 private EditText email,password;
 private     ProgressDialog progressDialog;
 private FirebaseAuth mAuth;
 private TextView forget;
 GoogleApiClient mGoogleSignInClient;
 DatabaseReference userRef;
 public static final int RC_SIGN_IN=1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_log_in);
      Typeface avenyInsta=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
      Typeface avenyInstaRegular=Typeface.createFromAsset(getAssets(),"font/AvenyTRegular.otf");
      userRef= FirebaseDatabase.getInstance().getReference().child("Users");
    email=(EditText)findViewById(R.id.email);
    mAuth=FirebaseAuth.getInstance();
    forget=(TextView)findViewById(R.id.forget);
    forget.setTypeface(avenyInsta);
    email.setTypeface(avenyInstaRegular);
    forget.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LogIn.this,RecoveryAccount.class));
        }
    });
 progressDialog=new ProgressDialog(this);
      Window window=getWindow();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
      }
    password=(EditText)findViewById(R.id.pass);
    signup=(Button)findViewById(R.id.signup);
    login=(Button)findViewById(R.id.logBut);
    password.setTypeface(avenyInstaRegular);
    signup.setTypeface(avenyInsta);
    login.setTypeface(avenyInsta);
    logInWithGoogle=(Button)findViewById(R.id.logInWithGoogle);
    logInWithGoogle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            signIn();
        }
    });
    login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        StartLogIn();
      }
    });

    signup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(LogIn.this, Register.class));
      }
    });

      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestIdToken(getString(R.string.default_web_client_id))
              .requestEmail()
              .build();

      mGoogleSignInClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
          @Override
          public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

          }
      }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
  }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
               new SpotsDialog.Builder().setContext(getApplicationContext()).setMessage("Logging with you google account").build().show();
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogIn.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            String currentUser=mAuth.getCurrentUser().getUid();
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            userRef.child(currentUser).child("deviceToken").setValue(deviceToken);
                            startActivity(new Intent(LogIn.this,ShowPost.class));
                            finish();
                            progressDialog.dismiss();
                        } else {

                            String mess=task.getException().getMessage();
                            Toast.makeText(LogIn.this, "LogIn failed : "+mess, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }


                    }
                });
    }



    private void StartLogIn() {

    String em=email.getText().toString();
    String ps=password.getText().toString();
    if(em.isEmpty()){
      email.setError("Please Enter Your Email");
      email.requestFocus();
      return;
    }
    if(ps.isEmpty()){
      password.setError("Please Enter Your Password");
      password.requestFocus();
      return;
    }
    else {
         progressDialog.setTitle("Logging in...");
         progressDialog.setCanceledOnTouchOutside(false);
         progressDialog.show();
      mAuth.signInWithEmailAndPassword(em,ps).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

          if (task.isSuccessful()){
              String currentUser=mAuth.getCurrentUser().getUid();
              String deviceToken= FirebaseInstanceId.getInstance().getToken();
              userRef.child(currentUser).child("deviceToken").setValue(deviceToken);
            Toast.makeText(LogIn.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LogIn.this,ShowPost.class));
            finish();
            progressDialog.dismiss();
          }
          else {
            String mess=task.getException().getMessage();
            Toast.makeText(LogIn.this, "LogIn failed : "+mess, Toast.LENGTH_SHORT).show();
          progressDialog.dismiss();
          }

        }
      });

    }


  }

  @Override
  protected void onStart() {
    super.onStart();

    FirebaseUser firebaseUser=mAuth.getCurrentUser();
    if (firebaseUser!=null){
      startActivity(new Intent(LogIn.this,ShowPost.class));
      finish();
    }

  }
}
