package com.nps.npsartsadmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentButtomSheet extends BottomSheetDialogFragment {

    private RecyclerView showComment ;
    private CircularImageView commentPro;
    private ImageView sendComment;
    private EditText getCommentText;
    private String currentUser;
    private DatabaseReference commentRef,userRef,noteRef;
    private FirebaseAuth commentAuth;
    private String userNameInComment,userProlinkInComment,getComment;
    private  long commentcounter;
    public static String uidKey,heading,postKey,postType;
    private TextView setTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.comment_bottom_sheet,container,false);
        Typeface aveny=Typeface.createFromAsset(getActivity().getAssets(),"font/AvenyTMedium.otf");
        showComment=(RecyclerView)view.findViewById(R.id.commentOnlineRecycle);
        showComment.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        showComment.setLayoutManager(layoutManager);
        commentPro=(CircularImageView)view.findViewById(R.id.profileInBottomComment);
        setTitle=(TextView)view.findViewById(R.id.commentTitle);
        setTitle.setTypeface(aveny);
        sendComment=(ImageView)view.findViewById(R.id.postCommentOnline);
        getCommentText=(EditText)view.findViewById(R.id.containOnlineCommen);
        commentRef= FirebaseDatabase.getInstance().getReference().child("OnlineComment").child(postKey);
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        noteRef=FirebaseDatabase.getInstance().getReference().child("Notification");
        commentAuth=FirebaseAuth.getInstance();
        currentUser=commentAuth.getCurrentUser().getUid();
        getUserInfo();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommentToDatabase();
                getCommentText.setText("");
            }
        });
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()){
                  int noOfComments=(int)dataSnapshot.getChildrenCount();
                  setTitle.setText(noOfComments+" Comments");
              }
              else {
                  setTitle.setText("0 Comment");
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void getUserInfo() {

        userRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild("fullname") && dataSnapshot.hasChild("profileLink")){
                    userNameInComment=String.valueOf(dataSnapshot.child("fullname").getValue());
                    userProlinkInComment=String.valueOf(dataSnapshot.child("profileLink").getValue());
                    Picasso.with(getActivity()).load(userProlinkInComment).placeholder(R.drawable.profile).into(commentPro);
                }
                else {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendCommentToDatabase() {
        getComment=getCommentText.getText().toString();
        if (getComment.isEmpty()){
            Toast.makeText(getActivity(), "Please write something first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userNameInComment.isEmpty()){
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userProlinkInComment.isEmpty()){
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        if (dataSnapshot.exists()){
                            commentcounter=dataSnapshot.getChildrenCount();
                        }
                        else {
                            commentcounter=0;
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
            String date=simpleDateFormat.format(calendar.getTime());
            SimpleDateFormat simpleTimeFormate=new SimpleDateFormat("HH:mm:ss");
            String time=simpleTimeFormate.format(calendar.getTime());


            HashMap putData=new HashMap();
            putData.put("Commentname",userNameInComment);
            putData.put("proLink",userProlinkInComment);
            putData.put("currentUser",currentUser);
            putData.put("comment",getComment);
            putData.put("CommentCounter",commentcounter);
            String randomValue=currentUser+date+time;
            String randomForNotification=postKey+currentUser;
            if (!uidKey.equals(currentUser)){
                if (postType.equals("normal")){
                    HashMap putNotData=new HashMap();
                    putNotData.put("postKey",postKey);
                    putNotData.put("username",userNameInComment);
                    putNotData.put("proLink",userProlinkInComment);
                    putNotData.put("notificationType","comment");
                    putNotData.put("heading",heading);
                    putNotData.put("currentUserss",currentUser);
                    noteRef.child(uidKey).child("AllNotification").child(randomForNotification).updateChildren(putNotData);

                }
                if (postType.equals("image")){
                    HashMap putNotData=new HashMap();
                    putNotData.put("postKey",postKey);
                    putNotData.put("username",userNameInComment);
                    putNotData.put("proLink",userProlinkInComment);
                    putNotData.put("notificationType","comment");
                    putNotData.put("heading","Photo");
                    putNotData.put("currentUserss",currentUser);
                    noteRef.child(uidKey).child("AllNotification").child(randomForNotification).updateChildren(putNotData);
                }
                if (postType.equals("message")){
                    HashMap putNotData=new HashMap();
                    putNotData.put("postKey",postKey);
                    putNotData.put("username",userNameInComment);
                    putNotData.put("proLink",userProlinkInComment);
                    putNotData.put("notificationType","comment");
                    putNotData.put("heading","Message");
                    putNotData.put("currentUserss",currentUser);
                    noteRef.child(uidKey).child("AllNotification").child(randomForNotification).updateChildren(putNotData);

                }

            }
            commentRef.child(randomValue).updateChildren(putData).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (!task.isSuccessful()){
                        String mess=task.getException().getMessage();
                        Toast.makeText(getActivity(), "Error:"+mess, Toast.LENGTH_SHORT).show();
                    }

                }
            });



        }

    }
    public static class CommentHolder extends RecyclerView.ViewHolder{
        View mview;
        String currentUserss;
        FirebaseAuth mAuth;
        ImageView deleteComment;
        DatabaseReference userReff;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            deleteComment=(ImageView)mview.findViewById(R.id.deleteComment);
            mAuth=FirebaseAuth.getInstance();
            currentUserss=mAuth.getCurrentUser().getUid();
            userReff=FirebaseDatabase.getInstance().getReference().child("Users");
        }
        public void setDeleteCommentVisibility(String userId) {
            if (userId.equals(currentUserss)) {
                deleteComment.setVisibility(View.VISIBLE);
            }
        }
        public void setCommentname(String userId) {
            userReff.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name=String.valueOf(dataSnapshot.child("fullname").getValue());
                    TextView userNameInMainPost=(TextView)mview.findViewById(R.id.whoCommented);
                    userNameInMainPost.setText(name);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void setProLink(final Context context,String userId) {

            userReff.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageLink=String.valueOf(dataSnapshot.child("profileLink").getValue());
                    CircularImageView setImage=(CircularImageView)mview.findViewById(R.id.onlineCommentProfile);
                    Picasso.with(context).load(imageLink).placeholder(R.drawable.profile).into(setImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        public void setComment(String comment) {
            TextView setComment=(TextView)mview.findViewById(R.id.commentTextonline);
            setComment.setText(comment);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Query commentInDecendingOrder=commentRef.orderByChild("CommentCounter");
        FirebaseRecyclerAdapter<OnLineCommentModel,CommentHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<OnLineCommentModel,CommentHolder>
                (OnLineCommentModel.class, R.layout.online_comment_layout,CommentHolder.class,commentInDecendingOrder) {
            @Override
            protected void populateViewHolder(final CommentHolder viewHolder, final OnLineCommentModel model, final int position) {
                 final String commentKey=getRef(position).getKey();
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentRef.child(commentKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String userId=String.valueOf(dataSnapshot.child("currentUser").getValue());
                                Intent intent=new Intent(getActivity(), ShowProOfLCandS.class);
                                intent.putExtra("userId",userId);
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
                viewHolder.setDeleteCommentVisibility(model.getCurrentUser());
                viewHolder.deleteComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu=new PopupMenu(getActivity(),viewHolder.deleteComment);
                        popupMenu.getMenuInflater().inflate(R.menu.deleteoption,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                commentRef.child(commentKey).removeValue();
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
                viewHolder.setCommentname(model.getCurrentUser());
                viewHolder.setComment(model.getComment());
                viewHolder.setProLink(getActivity(),model.getCurrentUser());
            }
        };
        showComment.setAdapter(firebaseRecyclerAdapter);


    }

}
