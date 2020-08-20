package com.nps.npsartsadmin;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
public class Register extends AppCompatActivity {

  private   EditText getEmail,getPass;
  private   Button signUp;
  private   FirebaseAuth mAuth;
  private   ProgressDialog dialog;
  private Toolbar toolbar;
  private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
      toolbar=(Toolbar)findViewById(R.id.signuptoolbar);
      setActionBar(toolbar);
      getActionBar().setDisplayHomeAsUpEnabled(true);
      getActionBar().setDisplayShowHomeEnabled(true);
         dialog=new ProgressDialog(this);
         mAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
         signUp=(Button)findViewById(R.id.createAccount);
         getEmail=(EditText)findViewById(R.id.registeremail);
         getPass=(EditText)findViewById(R.id.registerpass);
         signUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 CreateAcc();
             }
         });
    }

    private void CreateAcc() {

        final String email=getEmail.getText().toString();
        final String password=getPass.getText().toString();
        if(email.isEmpty()){
            getEmail.setError("Please enter your email");
            getEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            getPass.setError("Please enter your password");
            getPass.requestFocus();
            return;
        }
        if(password.length()<6){
            getPass.setError("Password is less then 6 characters");
            getPass.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            getEmail.setError("Your email is not valid");
            getEmail.requestFocus();
            return;

        }

        else {

            dialog.setTitle("Creating your new account");
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Snackbar snackbar=Snackbar.make(findViewById(R.id.refRele),"User already exist by this email id",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        dialog.dismiss();
                    }
                    if (task.isSuccessful()){
                        final String currentUser=mAuth.getCurrentUser().getUid();
                        HashMap putUserInfo=new HashMap();
                        putUserInfo.put("Email",email);
                        putUserInfo.put("Pass",password);
                       userRef.child(currentUser).updateChildren(putUserInfo).addOnCompleteListener(new OnCompleteListener() {
                          @Override
                          public void onComplete(@NonNull Task task) {
                          if (task.isSuccessful()){
                              String currentuser=mAuth.getCurrentUser().getUid();
                              String deviceToken= FirebaseInstanceId.getInstance().getToken();
                              userRef.child(currentUser).child("deviceToken").setValue(deviceToken);
                               startActivity(new Intent(Register.this,GetUserInfo.class));
                              Snackbar snackbar=Snackbar.make(findViewById(R.id.refRele),"Registration successful",Snackbar.LENGTH_SHORT);
                              snackbar.show();
                               finish();
                               dialog.dismiss();
                          }
                          else {
                              String e=task.getException().toString();
                              Snackbar snackbar=Snackbar.make(findViewById(R.id.refRele),"Registration Failed",Snackbar.LENGTH_SHORT);
                              snackbar.show();
                          }
                          }
                      });
                    }







                }
            });


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }

}
