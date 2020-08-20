package com.nps.npsartsadmin;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
public class PhoneNumberAuth extends AppCompatActivity {
    EditText phoneNumber;
    TextInputEditText verificationCode;
    TextInputLayout verificationparent;
    TextView getCode,verifyCode;
    RelativeLayout hindeNumberInput;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    TextView signHeading,userinPhone,reSend,showSec;
    ProgressDialog progressDialog;
    CountDownTimer countDownTimer;
    String getPhoneNumber="";
    DatabaseReference adminUserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_auth);
        mAuth=FirebaseAuth.getInstance();
        phoneNumber=(EditText)findViewById(R.id.phoneNumber);
        Typeface avenyInstaRegular=Typeface.createFromAsset(getAssets(),"font/FacebookNarrow_A_Rg.ttf");
        verificationparent=(TextInputLayout) findViewById(R.id.enterVerificationCode);
        adminUserRef= FirebaseDatabase.getInstance().getReference().child("AdminNumber");
        verificationCode=(TextInputEditText)findViewById(R.id.verificationCode);
        hindeNumberInput=(RelativeLayout)findViewById(R.id.phoneNumberHolder);
        getCode=(TextView)findViewById(R.id.getVerificationCodeButton);
        signHeading=(TextView)findViewById(R.id.siginHeading);
        userinPhone=(TextView)findViewById(R.id.usingHeading);
        reSend=(TextView)findViewById(R.id.reSendCode);
        showSec=(TextView)findViewById(R.id.showSeconds);
        signHeading.setTypeface(avenyInstaRegular);
        userinPhone.setTypeface(avenyInstaRegular);
        verifyCode=(TextView)findViewById(R.id.verifyCodebutton);
        progressDialog=new ProgressDialog(this);
        countDownTimer=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                showSec.setVisibility(View.VISIBLE);
               showSec.setText(millisUntilFinished/1000+"");
               reSend.setVisibility(View.INVISIBLE);
               reSend.setEnabled(false);
            }
            @Override
            public void onFinish() {
              reSend.setVisibility(View.VISIBLE);
              showSec.setText("0:00");
              reSend.setEnabled(true);
              showSec.setVisibility(View.INVISIBLE);
            }
        };
        reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getPhoneNumber.isEmpty()){
                    countDownTimer.start();
                    String phoneNumberWithCountryCode="+977"+getPhoneNumber;
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumberWithCountryCode,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneNumberAuth.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
                else {
                    Toast.makeText(PhoneNumberAuth.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhoneNumber=phoneNumber.getText().toString();
                 if (getPhoneNumber.isEmpty()){
                     phoneNumber.setError("phone number can't be empty");
                     phoneNumber.setFocusable(true);
                 return;
                 }
                 if (getPhoneNumber.length()<10 || getPhoneNumber.length()>10){
                     phoneNumber.setError("phone number should be of ten digits");
                     phoneNumber.setFocusable(true);
                 }
                 else {

                     adminUserRef.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(getPhoneNumber)){
                            progressDialog.setMessage("Authenticating your phone number");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            String phoneNumberWithCountryCode="+977"+getPhoneNumber;
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumberWithCountryCode,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    PhoneNumberAuth.this,               // Activity (for callback binding)
                                    callbacks);        // OnVerificationStateChangedCallbacks

                        }
                        else {
                            Toast.makeText(PhoneNumberAuth.this, getPhoneNumber+" is not verified number", Toast.LENGTH_LONG).show();
                        }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });
                 }
            }
        });
        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode.setVisibility(View.GONE);
                hindeNumberInput.setVisibility(View.GONE);
                String getVerificationCode=verificationCode.getText().toString();
                if (getVerificationCode.isEmpty()){
                    Toast.makeText(PhoneNumberAuth.this, "please enter verification code first", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    progressDialog.setMessage("Verifying Verification Code");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,getVerificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                     signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneNumberAuth.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                verificationparent.setVisibility(View.GONE);
                verifyCode.setVisibility(View.GONE);
                reSend.setVisibility(View.GONE);
                showSec.setVisibility(View.GONE);
                getCode.setVisibility(View.VISIBLE);
                hindeNumberInput.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                Toast.makeText(PhoneNumberAuth.this, "Try again letter or use another phone number", Toast.LENGTH_SHORT).show();
            }
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                if (getCode.getVisibility()==View.VISIBLE){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reSend.setVisibility(View.VISIBLE);
                        }
                    },60000);
                }
                mVerificationId = verificationId;
                mResendToken = token;
                progressDialog.dismiss();
                verificationparent.setVisibility(View.VISIBLE);
                verifyCode.setVisibility(View.VISIBLE);
                getCode.setVisibility(View.GONE);
                hindeNumberInput.setVisibility(View.GONE);

            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                       startActivity(new Intent(PhoneNumberAuth.this,ShowPost.class));
                       finish();
                            
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser!=null){
            startActivity(new Intent(PhoneNumberAuth.this,ShowPost.class));
            finish();
        }

    }


}
