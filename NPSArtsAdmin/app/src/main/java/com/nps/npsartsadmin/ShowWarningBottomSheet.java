package com.nps.npsartsadmin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowWarningBottomSheet extends BottomSheetDialogFragment {
    DatabaseReference userRef;
   public static String userKey;
    TextView warningHeading,warningMessage,cancleButton,grantButton;
    Boolean accountChecker=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.show_warning_bottomsheet,container,false);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        warningHeading=(TextView)view.findViewById(R.id.warningHeading);
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        Typeface aveny=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTRegular.otf");
          warningMessage=(TextView)view.findViewById(R.id.messagewarning);
          cancleButton=(TextView)view.findViewById(R.id.cancleButton);
          grantButton=(TextView)view.findViewById(R.id.grantButton);
          warningMessage.setTypeface(aveny);
          warningHeading.setTypeface(roboto);
          cancleButton.setTypeface(aveny);
          grantButton.setTypeface(aveny);
          cancleButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                dismiss();
              }
          });
          grantButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  accountChecker=true;
                  userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                          if (accountChecker){
                              String getAccountStatus=dataSnapshot.child(userKey).child("permission").getValue().toString();
                              if (getAccountStatus.equals("yes")){
                                  userRef.child(userKey).child("permission").setValue("no").addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {

                                          if (task.isSuccessful()){
                                              dismiss();
                                          }
                                          else
                                          {
                                              Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  });
                                  accountChecker=false;
                              }
                              else {
                                  userRef.child(userKey).child("permission").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if (task.isSuccessful()){
                                              dismiss();
                                          }
                                          else {
                                              Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                          dismiss();
                                          }
                                      }
                                  });
                                  accountChecker=false;
                              }
                          }
                      }
                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });
              }
          });




        return view;
    }
}
