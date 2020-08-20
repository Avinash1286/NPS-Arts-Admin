package com.nps.npsartsadmin;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class EssayVot extends Fragment {
    public EssayVot() {
        // Required empty public constructor
    }
    private    RecyclerView recyclePoem;
    private    DatabaseReference poemRef,checkRef,essayVotersOne,essayVotersAll,checkVoting;
    private RelativeLayout noInternetMessage,inActiveVoting;
    Boolean checkExistance=false;
    private FirebaseAuth mAuth;
    String currentUserss;
    LoadingView loadingView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_essay_vot, container, false);
        poemRef= FirebaseDatabase.getInstance().getReference().child("VotingSection").child("Essay");
        checkRef= FirebaseDatabase.getInstance().getReference().child("VotingSection");
        essayVotersAll= FirebaseDatabase.getInstance().getReference().child("EssayVotersForAll");
        essayVotersOne= FirebaseDatabase.getInstance().getReference().child("EssayVotersForOne");
        checkVoting= FirebaseDatabase.getInstance().getReference().child("VotingStatus");
        recyclePoem=(RecyclerView)view.findViewById(R.id.recyclePoem);
        noInternetMessage=(RelativeLayout) view.findViewById(R.id.noInternetHolder);
        inActiveVoting=(RelativeLayout)view.findViewById(R.id.showInActiveMessage);
        mAuth=FirebaseAuth.getInstance();
        currentUserss=mAuth.getCurrentUser().getUid();
        recyclePoem.setHasFixedSize(true);
        loadingView=(LoadingView)view.findViewById(R.id.loadingView);
        loadingView.start();
        recyclePoem.setLayoutManager(new LinearLayoutManager(getActivity()));
        checkVoting.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String con=dataSnapshot.child("EssayStatus").getValue().toString();
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
                    if (dataSnapshot.hasChild("Essay")){
                        inActiveVoting.setVisibility(View.GONE);
                        FirebaseRecyclerAdapter<EssayModel,PoemViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<EssayModel,PoemViewHolder>
                                (EssayModel.class,R.layout.essay_layout, PoemViewHolder.class,poemRef) {
                            @Override
                            protected void populateViewHolder(PoemViewHolder poemViewHolder, final EssayModel poemAndEssayModel, int i) {
                                final String poemKey=getRef(i).getKey();
                                poemViewHolder.setCandidateName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent =new Intent(getActivity(),ReadMoreArtsAndEssay.class);
                                        intent.putExtra("CandidateName",poemAndEssayModel.getTitles());
                                        intent.putExtra("Essay",poemAndEssayModel.getEssay());
                                        startActivity(intent);
                                    }
                                });
                                poemViewHolder.setEssay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent =new Intent(getActivity(),ReadMoreArtsAndEssay.class);
                                        intent.putExtra("CandidateName",poemAndEssayModel.getTitles());
                                        intent.putExtra("Essay",poemAndEssayModel.getEssay());
                                        startActivity(intent);
                                    }
                                });
                                poemViewHolder.setEssay(getActivity(),poemAndEssayModel.getEssay());
                                poemViewHolder.setTitles(getActivity(),poemAndEssayModel.getTitles());
                                poemViewHolder.setName(getActivity(),poemAndEssayModel.getAuthor());
                                poemViewHolder.setNumberOfVots(getActivity(),poemKey);
                                Animation animation= AnimationUtils.loadAnimation(getActivity(),R.anim.scale_animation);
                                poemViewHolder.voteButton.setAnimation(animation);
                                poemViewHolder.setTextVisibility(getActivity());
                                poemViewHolder.voteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkExistance=true;
                                        essayVotersAll.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (checkExistance){
                                                    if (!dataSnapshot.hasChild(currentUserss)){
                                                        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                                                        AboutToVote aboutToVote=new AboutToVote();
                                                        FragmentTransaction fragmentTransaction1=fragmentManager.beginTransaction();
                                                        fragmentTransaction1.add(R.id.essayFrame,aboutToVote);
                                                        AboutToVote.handKey=poemKey;
                                                        AboutToVote.currentUserss=currentUserss;
                                                        AboutToVote.votersForOne="EssayVotersForOne";
                                                        AboutToVote.votersForAll="EssayVotersForAll";
                                                        AboutToVote.name=poemAndEssayModel.getAuthor();
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
                        inActiveVoting.setVisibility(View.VISIBLE);
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
            poemRef=FirebaseDatabase.getInstance().getReference().child("EssayVotersForOne");
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

}
