package com.nps.npsartsadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoveryAccount extends AppCompatActivity {

  private   EditText getEmail;
  private   Button sendLink;
  private   FirebaseAuth mAuth;
  private   ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_account);
        getEmail=(EditText) findViewById(R.id.getRecover);
        sendLink=(Button)findViewById(R.id.sendlink);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        sendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getE=getEmail.getText().toString();
                if (getE.isEmpty()){
                    Toast.makeText(RecoveryAccount.this, "Please enter your email first...", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.setTitle("Sending");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.sendPasswordResetEmail(getE).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(RecoveryAccount.this, "Please check your email account...", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RecoveryAccount.this,LogIn.class));
                                finish();
                                progressDialog.dismiss();
                            }
                            else {
                                String mess=task.getException().getMessage();
                                Toast.makeText(RecoveryAccount.this, "Could not Recover: "+mess, Toast.LENGTH_LONG).show();
                             progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}
