package com.example.cse_5236_app.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cse_5236_app.R;
import com.example.cse_5236_app.databinding.FragmentHomeBinding;
import com.example.cse_5236_app.model.Movie;
import com.example.cse_5236_app.model.User;
import com.example.cse_5236_app.ui.dashboard.MovieRecyclerView;
import com.example.cse_5236_app.ui.dashboard.OnClickMovieListener;
import com.example.cse_5236_app.ui.details.MovieDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnClickMovieListener {

    private FragmentHomeBinding binding;

    private MovieRecyclerView adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View v;
        Activity activity = requireActivity();
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            v = inflater.inflate(R.layout.fragment_home, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_home, container, false);
        }
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultUsername = "Error";
        String username = sharedPref.getString(getString(R.string.saved_username_key), defaultUsername);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        RecyclerView recyclerView = v.findViewById(R.id.postsRV);
        recyclerView.setHasFixedSize(true);
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        }
        adapter = new MovieRecyclerView(this);
        DatabaseReference movieList = mDatabase.child("users").child(username).child("myList");
        movieList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> userList = new ArrayList<>();
//                Log.d("Home Fragment", snapshot.toString());
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Movie movie = ds.getValue(Movie.class);
                    userList.add(movie);
                }
                adapter.setmMovies(userList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home Fragment", error.getMessage());
            }
        });

        recyclerView.setAdapter(adapter);

        return v;
    }



    @Override
    public void onDestroyView() {
        Log.v("Home Fragment", "On Destroy View");
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMovieClick(int pos) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movie", adapter.getSelectedMovie(pos));
        try {
            if (getActivity().getIntent().hasExtra("userid")) {
                String userid = getActivity().getIntent().getStringExtra("userid");
                intent.putExtra("userid", userid);

            }
            startActivity(intent);
        }
        catch (Exception e) {
            Log.e("Home Fragment", "Something bad happened");
        }

    }

    @Override
    public void onCategoryClick(String category) {

    }
}