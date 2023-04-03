package com.example.cse_5236_app.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.cse_5236_app.R;
import com.example.cse_5236_app.databinding.FragmentNotificationsBinding;
import com.example.cse_5236_app.model.User;
import com.example.cse_5236_app.ui.MainActivity;
import com.example.cse_5236_app.ui.dashboard.DashboardActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private String userid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView bottomNavigationView=getActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_notifications);

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.navigation_dashboard:
                        userid = getActivity().getIntent().getStringExtra("userid");
                        Intent intent = new Intent(getActivity(), DashboardActivity.class);
                        intent.putExtra("userid", userid);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications:
                        return true;
                    case R.id.navigation_home:
                        userid = getActivity().getIntent().getStringExtra("userid");
                        Intent intent2 = new Intent(getActivity(), MainActivity.class);
                        intent2.putExtra("userid", userid);
                        startActivity(intent2);
                        getActivity().overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        Log.v("Notification Fragment", "OnCreateView");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultUsername = "Error";
        String username = sharedPref.getString(getString(R.string.saved_username_key), defaultUsername);
        Log.v("Notification Fragment", username);
        ImageView profilePic = root.findViewById(R.id.imageView);

        //TODO make buttons work.
        Button deleteProf = root.findViewById(R.id.del_button);
        Button changeProf = root.findViewById(R.id.change_button);
        Button changePic = root.findViewById(R.id.change_pic_button);
        Button changeDesc = root.findViewById(R.id.change_desc_button);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference getImage = firebaseDatabase.getReference();
        Context context = getContext();
        getImage.child("users").child(username).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot dataSnapshot)
                    {
                        Log.v("Notification Fragment", dataSnapshot.toString());
                        try {
                            User user = dataSnapshot.getValue(User.class);
                            if (user.getImageUri() == null) {
                                Glide.with(context).load(getString(R.string.profile_uri_default)).into(profilePic);
                            }
                            else {
                                Glide.with(context).load(user.getImageUri()).into(profilePic);
                            }
                            Log.v("Notification Fragment", user.getUsername() + " " + user.getPassword() + " " + user.getImageUri());
                        }
                        catch (Exception e) {
                            Log.e("Notification Fragment", "Picture Database error");

                        }
                        Log.v("Notification Fragment", "Picture Loaded");

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Notification Fragment", "Picture Database error");
                    }
                    });

        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        textView.setText(username);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}