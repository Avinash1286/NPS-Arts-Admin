package com.nps.npsartsadmin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.loadingview.LoadingView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
public class CreateQuizCompetation extends AppCompatActivity {
    CardView createQuizHolder,showFinalResult;
    ImageView rewardImage,quizImage,holdRunningQuizImage;
    Spinner getRightOption;
    RelativeLayout resultHolder,stopQuiz,startQuizAgain;
    TextView showQuestionInWhileRunningQuiz,showTotal,showLucky,showUnlucky,showWinnerName,showTarka;
    Button startQuiz;
    EditText option1,option2,option3,option4;
    DatabaseReference weeklyQuizDatabase,quizHistory,endMessageRef,sendNotification;
    TextInputEditText quizQuestion;
    String rightAnsInstilligation="",date,time;
    long totalLuckyList;
    Toolbar toolbar;
    Boolean checker=true;
    String winnerName="",winnerProLink="",historyQuestion,userType,historyOpt1,historyOpt2,historyOpt3,historyOpt4,historyCorrect,historyTime,historyQuizParent;
    String getQuest="",opt1="",opt2="",opt3="",opt4="",attachingValue="";
    LoadingView loadingView;
    RelativeLayout noInt;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz_competation);
         weeklyQuizDatabase= FirebaseDatabase.getInstance().getReference().child("WeeklyQuizRunning");
         quizHistory=FirebaseDatabase.getInstance().getReference().child("QuizHistory");
        sendNotification=FirebaseDatabase.getInstance().getReference().child("messagesquiz");
         toolbar=(Toolbar)findViewById(R.id.createQuizTool);
         setSupportActionBar(toolbar);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.quiztoolbar,null);
        actionBar.setCustomView(view);
        TextView headingPendingPost=(TextView)view.findViewById(R.id.pendingpostAdminHeading);
        headingPendingPost.setTypeface(roboto);
        headingPendingPost.setText("Weekly Quiz Management");
        ImageView openHistory=(ImageView)view.findViewById(R.id.showQuizManageHistorey);
        loadingView=(LoadingView)findViewById(R.id.loadingView);
        loadingView.start();
        openHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateQuizCompetation.this,QuizHistory.class));
            }
        });
        progressDialog=new ProgressDialog(this);
        ImageView setBack=(ImageView)view.findViewById(R.id.backPendingPostAdmin);
        setBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
         endMessageRef=FirebaseDatabase.getInstance().getReference().child("messagesQuizResult");
         createQuizHolder=(CardView)findViewById(R.id.createQuizHolder);
         showFinalResult=(CardView)findViewById(R.id.showFinalResult);
         rewardImage=(ImageView)findViewById(R.id.winnerImage);
         holdRunningQuizImage=(ImageView)findViewById(R.id.holdRunningStatusImage);
         quizImage=(ImageView)findViewById(R.id.quizImage);
         getRightOption=(Spinner)findViewById(R.id.getRightOption);
         resultHolder=(RelativeLayout)findViewById(R.id.resultHolder);
         stopQuiz=(RelativeLayout)findViewById(R.id.stopQuiz);
         startQuizAgain=(RelativeLayout)findViewById(R.id.startQuizAgain);
         quizQuestion=(TextInputEditText) findViewById(R.id.quizQuestion);
         showQuestionInWhileRunningQuiz=(TextView)findViewById(R.id.showQuestionInCreateAct);
         showTotal=(TextView)findViewById(R.id.showTotal);
         showLucky=(TextView)findViewById(R.id.showlucky);
         showUnlucky=(TextView)findViewById(R.id.showunlicky);
         showWinnerName=(TextView)findViewById(R.id.showWinnerName);
         showTarka=(TextView)findViewById(R.id.showTarka);
         startQuiz=(Button)findViewById(R.id.startQuiz);
         option1=(EditText)findViewById(R.id.opt1);
         option2=(EditText)findViewById(R.id.opt2);
         option3=(EditText)findViewById(R.id.opt3);
         option4=(EditText)findViewById(R.id.opt4);
         noInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
         startQuizAgain.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 createQuizHolder.setVisibility(View.VISIBLE);
                 holdRunningQuizImage.setVisibility(View.GONE);
                 showQuestionInWhileRunningQuiz.setVisibility(View.GONE);
                 resultHolder.setVisibility(View.GONE);
                 stopQuiz.setVisibility(View.GONE);
                 showFinalResult.setVisibility(View.GONE);
                 startQuizAgain.setVisibility(View.GONE);
             }
         });
         final String []rightAns={"Option 1","Option 2","Option 3","Option 4"};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,rightAns);
        getRightOption.setAdapter(arrayAdapter);
        getRightOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getQuest=String.valueOf(quizQuestion.getText());
                opt1=option1.getText().toString();
                opt2=option2.getText().toString();
                opt3=option3.getText().toString();
                opt4=option4.getText().toString();
                if (!getQuest.isEmpty() && !opt1.isEmpty() && !opt2.isEmpty() && !opt3.isEmpty() && !opt4.isEmpty()){
                    switch (position){
                        case 0:
                            rightAnsInstilligation=opt1;
                            break;
                        case 1:
                            rightAnsInstilligation=opt2;
                            break;
                        case 2:
                            rightAnsInstilligation=opt3;
                            break;
                        case 3:
                            rightAnsInstilligation=opt4;
                            break;

                    }
                    startQuiz.setEnabled(true);
                    return;
                }
                else {
                    Toast.makeText(CreateQuizCompetation.this, "Please fill up all the option", Toast.LENGTH_SHORT).show();
                    startQuiz.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                startQuiz.setEnabled(false);
            }
        });

        if (checkForConnctoin()){
            noInt.setVisibility(View.GONE);
            weeklyQuizDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.exists()) {
                            loadingView.stop();
                            loadingView.setVisibility(View.GONE);
                            if (checker.equals(true)) {
                                weeklyQuizDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        long total=dataSnapshot.child("participants").getChildrenCount();
                                        showTotal.setText("Total\n\n"+total);
                                        long lucky=dataSnapshot.child("WriteAnsGiver").getChildrenCount();
                                        showLucky.setText("Lucky\n\n"+lucky);
                                        long unlucky=dataSnapshot.child("WrongAnsGiver").getChildrenCount();
                                        showUnlucky.setText("Unlucky\n\n"+unlucky);
                                        String question=String.valueOf(dataSnapshot.child("QuestionAndOptions").child("question").getValue());
                                        showQuestionInWhileRunningQuiz.setText(question);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                showQuestionInWhileRunningQuiz.setVisibility(View.VISIBLE);
                                resultHolder.setVisibility(View.VISIBLE);
                                stopQuiz.setVisibility(View.VISIBLE);
                                createQuizHolder.setVisibility(View.GONE);
                                showFinalResult.setVisibility(View.GONE);
                                startQuizAgain.setVisibility(View.GONE);
                                holdRunningQuizImage.setVisibility(View.VISIBLE);
                            }
                        } else {
                            loadingView.setVisibility(View.GONE);
                            loadingView.stop();
                            if (checker.equals(true)) {
                                createQuizHolder.setVisibility(View.VISIBLE);
                                holdRunningQuizImage.setVisibility(View.GONE);
                                showQuestionInWhileRunningQuiz.setVisibility(View.GONE);
                                resultHolder.setVisibility(View.GONE);
                                stopQuiz.setVisibility(View.GONE);
                                showFinalResult.setVisibility(View.GONE);
                                startQuizAgain.setVisibility(View.GONE);
                                holdRunningQuizImage.setVisibility(View.GONE);
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        loadingView.setVisibility(View.GONE);
                        loadingView.stop();
                        Toast.makeText(CreateQuizCompetation.this, "Error "+e, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
        }
        else {
            loadingView.stop();
            loadingView.setVisibility(View.GONE);
            noInt.setVisibility(View.VISIBLE);
        }


         startQuiz.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 checkValidationAndStartQuiz();
             }
         });
         stopQuiz.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 checker=false;
                 weeklyQuizDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if (dataSnapshot.hasChild("WriteAnsGiver")){
                             stopQuizAndShowResult();
                         }
                         else {
                             final AlertDialog.Builder builder=new AlertDialog.Builder(CreateQuizCompetation.this);
                             builder.setTitle("No Participants");
                             builder.setMessage("Are you sure to end this Quiz Game?");
                             builder.setCancelable(false);
                             builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     weeklyQuizDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             Toast.makeText(CreateQuizCompetation.this, "Quiz Game Terminated", Toast.LENGTH_SHORT).show();
                                         }
                                     });
                                 }
                             });
                             builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     dialog.dismiss();
                                 }
                             });
                             builder.create().show();
                         }
                     }
                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                     }
                 });
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

    private void stopQuizAndShowResult() {
        progressDialog.setMessage("Getting Winner");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        weeklyQuizDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try{
                    date = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("date").getValue());
                    time = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("timeWithSecond").getValue());
                    historyQuestion = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("question").getValue());
                    historyOpt1 = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option1").getValue());
                    historyOpt2 =String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option2").getValue());
                    historyOpt3 = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option3").getValue());
                    historyOpt4 = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("option4").getValue());
                    historyCorrect = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("correct").getValue());
                    historyTime = String.valueOf(dataSnapshot.child("QuestionAndOptions").child("time").getValue());
                    historyQuizParent = date + time;
                    weeklyQuizDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("WriteAnsGiver")) {
                                totalLuckyList = dataSnapshot.child("WriteAnsGiver").getChildrenCount();
                                String convertInString = String.valueOf(totalLuckyList);
                                Integer convertInInteger = Integer.valueOf(convertInString);
                                if (convertInInteger == 1) {
                                    attachingValue = "1";
                                } else {
                                    Integer selectWinnerValue = ThreadLocalRandom.current().nextInt(1, convertInInteger);
                                    attachingValue = String.valueOf(selectWinnerValue);
                                }
                                if (!attachingValue.isEmpty()) {
                                    winnerName = String.valueOf(dataSnapshot.child("WriteAnsGiver").child(attachingValue).child("name").getValue());
                                    winnerProLink = String.valueOf(dataSnapshot.child("WriteAnsGiver").child(attachingValue).child("prolink").getValue());
                                    userType = String.valueOf(dataSnapshot.child("WriteAnsGiver").child(attachingValue).child("userType").getValue());
                                    Picasso.with(CreateQuizCompetation.this).load(winnerProLink).placeholder(R.drawable.image_placeholder).into(rewardImage);
                                    holdRunningQuizImage.setVisibility(View.GONE);
                                    showQuestionInWhileRunningQuiz.setVisibility(View.GONE);
                                    resultHolder.setVisibility(View.GONE);
                                    stopQuiz.setVisibility(View.GONE);
                                    showFinalResult.setVisibility(View.VISIBLE);
                                    rewardImage.setVisibility(View.VISIBLE);
                                    showWinnerName.setVisibility(View.VISIBLE);
                                    showTarka.setVisibility(View.VISIBLE);
                                    startQuizAgain.setVisibility(View.VISIBLE);
                                    showWinnerName.setText(winnerName+" ("+userType+")");
                                    showTarka.setText("Lucky among " + totalLuckyList + " participants");
                                    HashMap putQuizHistory = new HashMap();
                                    putQuizHistory.put("historyQuestion", historyQuestion);
                                    putQuizHistory.put("historyOpt1", historyOpt1);
                                    putQuizHistory.put("historyOpt2", historyOpt2);
                                    putQuizHistory.put("historyOpt3", historyOpt3);
                                    putQuizHistory.put("historyOpt4", historyOpt4);
                                    putQuizHistory.put("historyCorrect", historyCorrect);
                                    putQuizHistory.put("historyTime", historyTime);
                                    putQuizHistory.put("historyTimeWithSecond", time);
                                    putQuizHistory.put("historyDate", date);
                                    putQuizHistory.put("historyWinnerName", winnerName);
                                    putQuizHistory.put("historyWinnerProLink", winnerProLink);
                                    quizHistory.child(historyQuizParent).updateChildren(putQuizHistory).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                endMessageRef.push().setValue("sent");
                                                progressDialog.dismiss();
                                                weeklyQuizDatabase.removeValue();
                                            }
                                        }
                                    });
                                    return;
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(CreateQuizCompetation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(CreateQuizCompetation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(CreateQuizCompetation.this, "Error :"+e, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    return;
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(CreateQuizCompetation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkValidationAndStartQuiz() {


        if (getQuest.isEmpty()){
            quizQuestion.setError("Question can't be empty");
            quizQuestion.requestFocus();
            return;
        }
        if (opt1.isEmpty()){
            option1.setError("Option can't be empty");
            option1.requestFocus();
            return;
        }
        if (opt2.isEmpty()){
            option2.setError("Option can't be empty");
            option2.requestFocus();
            return;
        }
        if (opt3.isEmpty()){
            option3.setError("Option can't be empty");
            option3.requestFocus();
            return;
        }
        if (opt4.isEmpty()){
            option4.setError("Option can't be empty");
            option4.requestFocus();
             return;
         }
        if (rightAnsInstilligation.isEmpty()){
            Toast.makeText(this, "Please select right option from dropdown box", Toast.LENGTH_SHORT).show();
        return;
        }
        else {
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
            String date=dateFormat.format(calendar.getTime());
            SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm aa");
            String simpleTime=timeFormat.format(calendar.getTime());
            SimpleDateFormat timeWithSecond=new SimpleDateFormat("HH:mm:ss");
            String  timeandSecond=timeWithSecond.format(calendar.getTime());
            HashMap putQuizData=new HashMap();
            putQuizData.put("question",getQuest);
            putQuizData.put("option1",opt1);
            putQuizData.put("option2",opt2);
            putQuizData.put("option3",opt3);
            putQuizData.put("option4",opt4);
            putQuizData.put("correct",rightAnsInstilligation);
            putQuizData.put("date",date);
            putQuizData.put("timeWithSecond",timeandSecond);
            putQuizData.put("time",simpleTime);
            weeklyQuizDatabase.child("QuestionAndOptions").updateChildren(putQuizData).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                     sendNotification.push().setValue("received");
                        Toast.makeText(CreateQuizCompetation.this, "Weekly Quiz Competition Started", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(CreateQuizCompetation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
