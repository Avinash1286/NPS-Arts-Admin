package com.nps.npsartsadmin;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class PostArticlesActivity extends AppCompatActivity {
  private   Toolbar toolbar;
  private  ImageView imageView;
   private  int getFontSize=18;
  private  String fontColor="";
  private  int backGroundColor=0;
  private  RecyclerView recyclerBackColor;
  private  EditText getArt,getHeading,getMessage;
  private  RelativeLayout backSetter,backMessage;
  private  TextView userNameInPost;
  private  TextView buttonPostArt;
  private  DatabaseReference userRef,postRef,postnotifyRef;
  private  FirebaseAuth mAuth;
  private  String currentUser;
  private  String date,time,timeToShow;
  private  String randomValue;
  private  long postCounter;
  private   String articles,headings;
  private   String getUserN,getProLink;
  private   CircularImageView userPofileInPost;
  private   String height,postType="normal",message="none";
  private   TextView postTitle,textback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_articles);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTMedium.otf");
        Typeface aveny=Typeface.createFromAsset(getAssets(),"font/AvenyTRegular.otf");
       toolbar=(Toolbar)findViewById(R.id.postArtTool);
       setActionBar(toolbar);
       getHeading=(EditText)findViewById(R.id.containHeading);
       mAuth=FirebaseAuth.getInstance();
       currentUser=mAuth.getCurrentUser().getUid();
       userRef=FirebaseDatabase.getInstance().getReference().child("Users");
       postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
       postnotifyRef=FirebaseDatabase.getInstance().getReference().child("PostNotification");
       userNameInPost=(TextView)findViewById(R.id.postUserName);
       userNameInPost.setTypeface(roboto);
       backMessage=(RelativeLayout)findViewById(R.id.backOfMessage);
       getMessage=(EditText)findViewById(R.id.containMessage);
       userPofileInPost=(CircularImageView)findViewById(R.id.userPostProfile);
       getArt=(EditText)findViewById(R.id.containContent);
       getArt.setTypeface(roboto);
       getHeading.setTypeface(aveny);
       getMessage.setTypeface(roboto);
       backSetter=(RelativeLayout)findViewById(R.id.backOfArt);
       recyclerBackColor=(RecyclerView)findViewById(R.id.recycleBackGround);
       BackGroundSetter backGroundSetter=new BackGroundSetter();
       recyclerBackColor.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
       recyclerBackColor.setAdapter(backGroundSetter);
       ActionBar actionBar=getActionBar();
       actionBar.setDisplayShowCustomEnabled(true);
       LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view=layoutInflater.inflate(R.layout.post_custom_bar,null);
       actionBar.setCustomView(view);
        postTitle=(TextView)findViewById(R.id.userPostTitle);
        postTitle.setTypeface(roboto);
       getCurrentUserInfo();
       buttonPostArt=(TextView)findViewById(R.id.postButton);
       buttonPostArt.setTypeface(roboto);
       buttonPostArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForConnctoin()){

                    if (postType.equals("normal")){
                        ValidationArts();
                    }
                    else {
                        ValidationMessage();
                    }
                }
                else {
                    Toast.makeText(PostArticlesActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

       imageView=(ImageView)findViewById(R.id.backPressedButton);
         imageView.setOnClickListener(
                 new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onBackPressed();
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
    private void ValidationMessage() {
        message=getMessage.getText().toString();
        headings="none";
        articles="none";
        if (message.isEmpty()){
            Toast.makeText(this, "Message box is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            SavePostInFo();
        }
    }
    private void ValidationArts() {
         articles=getArt.getText().toString();
         headings=getHeading.getText().toString();
         if (headings.isEmpty()){
             Toast.makeText(this, "Heading is empty", Toast.LENGTH_SHORT).show();
             return;
         }
        if (articles.isEmpty()){

            Toast.makeText(this, "Please write something to post", Toast.LENGTH_SHORT).show();

            return;
        }
        else {
            new SpotsDialog.Builder().setCancelable(false)
                    .setMessage("Posting").setContext(PostArticlesActivity.this).build().show();
            SavePostInFo();
        }
    }
    private void getCurrentUserInfo() {
        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("fullname")){
                        getUserN=dataSnapshot.child("fullname").getValue().toString();
                        userNameInPost.setText(getUserN);
                    }

                    if (dataSnapshot.hasChild("profileLink")){
                        getProLink=dataSnapshot.child("profileLink").getValue().toString();
                        Picasso.with(PostArticlesActivity.this).load(getProLink).placeholder(R.drawable.profile).into(userPofileInPost);
                    }
                    else {
                        Toast.makeText(PostArticlesActivity.this, "NO User Name and Profile Image", Toast.LENGTH_SHORT).show();
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void SavePostInFo() {
        height=String.valueOf(backSetter.getHeight());
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     postCounter=dataSnapshot.getChildrenCount();
                 }
                 else {
                     postCounter=0;
                 }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        userRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userFullName=dataSnapshot.child("fullname").getValue().toString();
                String userProLink=dataSnapshot.child("profileLink").getValue().toString();
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
                 date=simpleDateFormat.format(calendar.getTime());
                SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("HH:mm:ss");
                 time=simpleDateFormat1.format(calendar.getTime());
                SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("HH:mm a");
                timeToShow=simpleDateFormat2.format(calendar.getTime());
                randomValue=currentUser+date+time;
                String backInString=String.valueOf(backGroundColor);
                String fontsizeInString=String.valueOf(getFontSize);
                HashMap putPostInfo=new HashMap();
                putPostInfo.put("uid",currentUser);
                putPostInfo.put("userName",userFullName);
                putPostInfo.put("userProLink",userProLink);
                putPostInfo.put("date",date);
                putPostInfo.put("time",timeToShow);
                putPostInfo.put("articles",articles);
                putPostInfo.put("backGround",backInString);
                putPostInfo.put("fontSize",fontsizeInString);
                putPostInfo.put("fontColor",fontColor);
                putPostInfo.put("counter",postCounter);
                putPostInfo.put("heading",headings);
                putPostInfo.put("height",height);
                putPostInfo.put("verifiedPost","no");
                putPostInfo.put("postType",postType);
                putPostInfo.put("message",message);
                putPostInfo.put("imageUrl","");
                postRef.child(randomValue).updateChildren(putPostInfo).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Toast.makeText(PostArticlesActivity.this, "Uploading done", Toast.LENGTH_SHORT).show();
                              onBackPressed();
                        }
                        else {
                            String mess=task.getException().toString();
                            Toast.makeText(PostArticlesActivity.this, "Error occurred"+mess, Toast.LENGTH_SHORT).show();
                               onBackPressed();
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public class BackGroundSetter extends RecyclerView.Adapter<BackGroundSetter.ViewHoler> {
        @NonNull
        @Override
        public ViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater=LayoutInflater.from(viewGroup.getContext());
            View view=layoutInflater.inflate(R.layout.set_back_of_art,null);
            return new ViewHoler(view);
        }
        @Override
        public void onBindViewHolder(@NonNull final ViewHoler viewHoler, final int i) {

            viewHoler.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (i){
                        case 0:
                             backSetter.setBackgroundResource(R.drawable.white);
                            getArt.setTextColor(Color.BLACK);
                            getArt.setHintTextColor(Color.BLACK);
                            fontColor="black";
                            postType="normal";
                            backGroundColor=i;
                            getHeading.setVisibility(View.VISIBLE);
                            getArt.setVisibility(View.VISIBLE);
                            backMessage.setVisibility(View.GONE);
                            break;
                        case 1:
                            backMessage.setBackgroundResource(R.drawable.gradient);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;
                        case 2:
                            backMessage.setBackgroundResource(R.drawable.gradient2);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;
                        case 3:
                            backMessage.setBackgroundResource(R.drawable.gradient3);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;

                        case 4:
                            backMessage.setBackgroundResource(R.drawable.gradient4);
                            getMessage.setTextColor(Color.BLACK);
                            getMessage.setHintTextColor(Color.BLACK);
                            backGroundColor=i;
                            fontColor="black";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;

                        case 5:
                            backMessage.setBackgroundResource(R.drawable.gradient5);
                            getMessage.setTextColor(Color.BLACK);
                            getMessage.setHintTextColor(Color.BLACK);
                            backGroundColor=i;
                            fontColor="black";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;

                        case 6:
                            backMessage.setBackgroundResource(R.drawable.gradient6);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;
                        case 7:
                            backMessage.setBackgroundResource(R.drawable.gradient7);
                            getMessage.setHintTextColor(Color.BLACK);
                            getMessage.setTextColor(Color.BLACK);
                            backGroundColor=i;
                            fontColor="black";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;
                        case 8:
                            backMessage.setBackgroundResource(R.drawable.gradient8);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;
                        case 9:
                            backMessage.setBackgroundResource(R.drawable.gradient9);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;

                        case 10:
                            backMessage.setBackgroundResource(R.drawable.gradient10);
                            getMessage.setTextColor(Color.BLACK);
                            getMessage.setHintTextColor(Color.BLACK);
                            backGroundColor=i;
                            fontColor="black";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;

                        case 11:
                            backMessage.setBackgroundResource(R.drawable.gradient11);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;

                        case 12:
                            backMessage.setBackgroundResource(R.drawable.gradient12);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;


                        case 13:
                            backMessage.setBackgroundResource(R.drawable.gradient13);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;

                         case 14:
                            backMessage.setBackgroundResource(R.drawable.gradient14);
                             getMessage.setTextColor(Color.WHITE);
                             getMessage.setHintTextColor(Color.WHITE);
                             backGroundColor=i;
                             fontColor="white";
                             postType="message";
                             getArt.setVisibility(View.GONE);
                             getHeading.setVisibility(View.GONE);
                             backMessage.setVisibility(View.VISIBLE);
                             getMessage.setHeight(300);
                             getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                             break;
                        case 15:
                            backMessage.setBackgroundResource(R.drawable.gradient15);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;
                        case 16:
                            backMessage.setBackgroundResource(R.drawable.gradient1);
                            getMessage.setTextColor(Color.WHITE);
                            getMessage.setHintTextColor(Color.WHITE);
                            backGroundColor=i;
                            fontColor="white";
                            postType="message";
                            getArt.setVisibility(View.GONE);
                            getHeading.setVisibility(View.GONE);
                            backMessage.setVisibility(View.VISIBLE);
                            getMessage.setHeight(300);
                            getMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            break;
                    }
                }
            });
            switch (i){
                case 0:
                    viewHoler.cardView.setBackgroundResource(R.drawable.ic_do_not_disturb_black_24dp);
                    break;
                case 1:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient);

                    break;
                case 2:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient2);

                    break;
                case 3:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient3);

                    break;
                case 4:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient4);

                    break;
                case 5:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient5);

                    break;
                case 6:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient6);

                    break;
                case 7:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient7);

                    break;
                case 8:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient8);

                    break;
                case 9:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient9);

                    break;
                case 10:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient10);

                    break;
                case 11:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient11);

                    break;
                case 12:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient12);

                    break;
                case 13:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient13);

                    break;
                case 14:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient14);

                    break;
                case 15:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient15);
                    break;

                case 16:
                    viewHoler.cardView.setBackgroundResource(R.drawable.gradient1);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return 16;
        }
        public class ViewHoler extends RecyclerView.ViewHolder{

            RelativeLayout cardView;

            public ViewHoler(@NonNull View itemView) {
                super(itemView);
                cardView=(RelativeLayout) itemView.findViewById(R.id.backgroundColor);
            }
        }
    }
}
