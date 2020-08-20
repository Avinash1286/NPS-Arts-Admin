package com.nps.npsartsadmin;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.loadingview.LoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;
public class PoemVoting extends Fragment {
 private     RecyclerView recyclePoem;
 private     DatabaseReference poemRef,checkRef,poemVotersOne,poemVotersAll,checkVoting;
 private     RelativeLayout noInternetMessage,inActiveVoting;
 private     FirebaseAuth mAuth;
             String currentUserss;
             Boolean checkExistance=false;
             LoadingView loadingView;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_poem_voting, container, false);
        poemRef= FirebaseDatabase.getInstance().getReference().child("VotingSection").child("Poem");
        checkRef= FirebaseDatabase.getInstance().getReference().child("VotingSection");
        checkVoting= FirebaseDatabase.getInstance().getReference().child("VotingStatus");
        poemVotersAll= FirebaseDatabase.getInstance().getReference().child("PoemVotersForAll");
        poemVotersOne= FirebaseDatabase.getInstance().getReference().child("PoemVotersForOne");
        recyclePoem=(RecyclerView)view.findViewById(R.id.recyclePoem);
        mAuth=FirebaseAuth.getInstance();
        loadingView=(LoadingView)view.findViewById(R.id.loadingView);
        loadingView.start();
        currentUserss=mAuth.getCurrentUser().getUid();
        noInternetMessage=(RelativeLayout) view.findViewById(R.id.noInternetHolder);
        inActiveVoting=(RelativeLayout)view.findViewById(R.id.showInActiveMessage);
        recyclePoem.setHasFixedSize(true);
        recyclePoem.setLayoutManager(new LinearLayoutManager(getActivity()));
        checkVoting.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           String con=dataSnapshot.child("PoemStatus").getValue().toString();
           if (con.equals("yes")){
               getData();
           }
           else {
               loadingView.stop();
               loadingView.setVisibility(View.GONE);
               inActiveVoting.setVisibility(View.VISIBLE);
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    private void getData() {
        if (checkForConnctoin()){
            checkRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.hasChild("Poem")){
                   inActiveVoting.setVisibility(View.GONE);
                   FirebaseRecyclerAdapter<PoemModel,PoemViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PoemModel, PoemViewHolder>
                           (PoemModel.class,R.layout.essay_layout,PoemViewHolder.class,poemRef) {
                       @Override
                       protected void populateViewHolder(PoemViewHolder poemViewHolder, final PoemModel poemAndEssayModel, int i) {
                          final String poemKey=getRef(i).getKey();
                           poemViewHolder.setCandidateName.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent =new Intent(getActivity(),ReadMoreArtsAndEssay.class);
                                   intent.putExtra("CandidateName",poemAndEssayModel.getComposer());
                                   intent.putExtra("Essay",poemAndEssayModel.getPoem());
                                   startActivity(intent);
                               }
                           });
                           poemViewHolder.setEssay.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent =new Intent(getActivity(),ReadMoreArtsAndEssay.class);
                                   intent.putExtra("CandidateName",poemAndEssayModel.getComposer());
                                   intent.putExtra("Essay",poemAndEssayModel.getPoem());
                                   startActivity(intent);
                               }
                           });
                           poemViewHolder.setEssay(getActivity(),poemAndEssayModel.getPoem());
                           poemViewHolder.setTitles(getActivity(),poemAndEssayModel.getHeading());
                           poemViewHolder.setName(getActivity(),poemAndEssayModel.getComposer());
                           poemViewHolder.setNumberOfVots(getActivity(),poemKey);
                           Animation animation= AnimationUtils.loadAnimation(getActivity(),R.anim.scale_animation);
                           poemViewHolder.voteButton.setAnimation(animation);
                           poemViewHolder.setTextVisibility(getActivity());
                           poemViewHolder.voteButton.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   checkExistance=true;
                                   poemVotersAll.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if (checkExistance){
                                               if (!dataSnapshot.hasChild(currentUserss)){
                                                   FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                                                   AboutToVote aboutToVote=new AboutToVote();
                                                   FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                                                   fragmentTransaction1.add(R.id.holdAboutToVote,aboutToVote);
                                                   AboutToVote.handKey=poemKey;
                                                   AboutToVote.currentUserss=currentUserss;
                                                   AboutToVote.votersForOne="PoemVotersForOne";
                                                   AboutToVote.votersForAll="PoemVotersForAll";
                                                   AboutToVote.name=poemAndEssayModel.getComposer();
                                                   fragmentTransaction1.addToBackStack(null);
                                                   fragmentTransaction1.commit();
                                                   checkExistance=false;
                                               }
                                               else {
                                                   Toast.makeText(getActivity(), "You have already taken part", Toast.LENGTH_SHORT).show();
                                                   checkExistance=false;
                                               }

                                           }

                                       }
                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });
                               }
                           });
                       }
                   };
                   recyclePoem.setAdapter(firebaseRecyclerAdapter);
                   loadingView.stop();
                   loadingView.setVisibility(View.GONE);
               }
               else {

                   loadingView.stop();
                   loadingView.setVisibility(View.GONE);
                   inActiveVoting.setVisibility(View.GONE);
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
            noInternetMessage.setVisibility(View.GONE);
        }
    }
    public static class PoemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView setCandidateName;
        DatabaseReference poemRef,votingTextVisibilityRef;
        TextView setEssay,voteButton,showNumberOfVots;
        public PoemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
             setCandidateName=(TextView)mView.findViewById(R.id.showCandidateName);
             setEssay=(TextView)mView.findViewById(R.id.holdCandidateContentInPoemAndEssay);
             showNumberOfVots=(TextView)mView.findViewById(R.id.containNumberofVots);
             voteButton=(TextView)mView.findViewById(R.id.voteButton);
             poemRef=FirebaseDatabase.getInstance().getReference().child("PoemVotersForOne");
            votingTextVisibilityRef= FirebaseDatabase.getInstance().getReference().child("VotingStatus");
        }
        public void setEssay(Context context,String essay) {
            setEssay.setText(essay);
        }
        public void setTitles(Context context,String titles) {
            TextView setTitles=(TextView)mView.findViewById(R.id.containContentHeading);
            setTitles.setText(titles);
        }
        public void setTextVisibility(Context context){
            votingTextVisibilityRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("NoOfVotsCon").getValue().toString().equals("yes")){
                        showNumberOfVots.setVisibility(View.VISIBLE);
                    }
                    else {
                        showNumberOfVots.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setName(Context context,String name) {
           setCandidateName.setText(name);
        }
        public void setNumberOfVots(final Context context, final String voteKey){
            poemRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long counter=dataSnapshot.child(voteKey).getChildrenCount();
                    Typeface roboto=Typeface.createFromAsset(context.getAssets(),"font/AvenyTMedium.otf");
                    showNumberOfVots.setTypeface(roboto);
                    showNumberOfVots.setText(String.valueOf(counter));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private boolean checkForConnctoin() {
        ConnectivityManager cm=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){

            return true;

        }
        else {
            return false;
        }

    }
}
