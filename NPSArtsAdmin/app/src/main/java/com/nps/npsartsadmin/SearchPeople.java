package com.nps.npsartsadmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.StringTokenizer;

public class SearchPeople extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {
BottomNavigationViewEx bottomNavigationViewEx;
private Toolbar searchToolBar;
private String searchText="";
private SearchView searchView;
private RecyclerView recycleAllUsers;
private DatabaseReference userRef;
private Boolean searchButtonChecker=true;
private SwipeRefreshLayout refresh;
private RelativeLayout showNoInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);
        searchToolBar=(Toolbar)findViewById(R.id.searchpeople);
        refresh=(SwipeRefreshLayout)findViewById(R.id.refreshSearchPeople);
        refresh.setOnRefreshListener(this);
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.primaryStatus));
        }
        showNoInt=(RelativeLayout)findViewById(R.id.noInternetHolder);
        setSupportActionBar(searchToolBar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view=layoutInflater.inflate(R.layout.search_toolbar_layout,null);
        actionBar.setCustomView(view);

        recycleAllUsers=(RecyclerView)findViewById(R.id.recycleUsers);
        recycleAllUsers.setHasFixedSize(true);
        recycleAllUsers.setLayoutManager(new GridLayoutManager(this,3));
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomnav);
        searchView=(SearchView)findViewById(R.id.startSearching);
        if (checkForConnctoin()){
            startSearchingOnClick(searchText);
            showNoInt.setVisibility(View.GONE);
        }
        else {
            showNoInt.setVisibility(View.VISIBLE);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearchingOnClick(query.toLowerCase());

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                startSearchingOnClick(newText.toLowerCase());

                return true;
            }
        });
        setUpBottomNav();
    }
    private void startSearchingOnClick(String text){

        Query searchByTag=userRef.orderByChild("tagname").startAt(text).endAt(text+"\uf8ff");
        FirebaseRecyclerAdapter<SearchModel,SearchViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<SearchModel,SearchViewHolder>
                (SearchModel.class,R.layout.search_layout,SearchViewHolder.class,searchByTag) {
            @Override
            protected void populateViewHolder(SearchViewHolder viewHolder, SearchModel model,final int position) {
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId=getRef(position).getKey();
                        Intent intent=new Intent(SearchPeople.this,ShowProOfLCandS.class);
                        intent.putExtra("userId",userId);
                        startActivity(intent);
                    }
                });
  //              viewHolder.setFullname(model.getFullname());
                viewHolder.setProfileLink(getApplicationContext(),model.getProfileLink());
            }
        };
        recycleAllUsers.setAdapter(firebaseRecyclerAdapter);
        if (refresh.isRefreshing()){
            refresh.setRefreshing(false);
        }
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

    public void setUpBottomNav(){
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);
        Typeface roboto=Typeface.createFromAsset(getAssets(),"font/AvenyTRegular.otf");
        bottomNavigationViewEx.setTypeface(roboto);
        enableNavigation();
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setChecked(true);
        menuItem.setIcon(R.drawable.final_search_on);
    }
    public void enableNavigation(){
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(SearchPeople.this,ShowPost.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.postsearch:
                        break;
                    case R.id.offlineArticles:
                        startActivity(new Intent(SearchPeople.this,MainActivity.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                    case R.id.postlikes:
                        startActivity(new Intent(SearchPeople.this,HeartActivity.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;

                    case R.id.postprofile:
                        startActivity(new Intent(SearchPeople.this,AdminControll.class));
                        overridePendingTransition(R.anim.comment_in,R.anim.comment_in);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onRefresh() {
        startSearchingOnClick(searchText);
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
          mView=itemView;
        }
/*
        public void setFullname(String fullname) {

            TextView setNameInSearch=(TextView)mView.findViewById(R.id.showNameInSearch);
            setNameInSearch.setText(fullname);
                 }
*/
        public void setProfileLink(Context context,String profileLink) {
            ImageView setProInSearch=(ImageView) mView.findViewById(R.id.containProfileInSearch);
            Picasso.with(context).load(profileLink).placeholder(R.drawable.grayback).into(setProInSearch);
        }
    }
    public static class PopularSearch extends RecyclerView.ViewHolder{
        View mView;
        TextView viewProButton;
        public PopularSearch(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            viewProButton=(TextView)mView.findViewById(R.id.showProButtonInpop);
        }
        public void setProPic(Context context,String proLink){
            CircularImageView seImge=(CircularImageView)mView.findViewById(R.id.holdProfileInPop);
            Picasso.with(context).load(proLink).placeholder(R.drawable.profile).into(seImge);
        }
        public void setNamePop(String nameInPop){
            TextView setName=(TextView)mView.findViewById(R.id.holdeNameInpop);
            setName.setText(nameInPop);
        }
    }
}
