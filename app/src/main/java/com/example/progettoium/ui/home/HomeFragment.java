package com.example.progettoium.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettoium.ui.MainActivity;
import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.databinding.FragmentHomeBinding;
import com.example.progettoium.ui.home.bookedRepetitions.BookedRepetitionsCustomViewAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Observable;

public class HomeFragment extends Fragment {

    private NetworkViewModel networkViewModel;
    //CoursesTimeTableLiveData coursesViewModel;
    private FragmentHomeBinding binding;

    BookedRepetitionsCustomViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        networkViewModel = new ViewModelProvider(requireActivity()).get(NetworkViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.coursesList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookedRepetitionsCustomViewAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        /*TRYING TO GET DATA FROM LIVEDATA*/
        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), user -> {
            binding.lblWelcomeMainFragment.setText("Hi, " + user.getName() + " " + user.getSurname());
        });
        networkViewModel.getBookedRepetitions().observe(getViewLifecycleOwner(), courseObjects -> {
            adapter.setData(courseObjects);  // setta i dati nella recyclerView
        });

        networkViewModel.getIsConnected().observe(getViewLifecycleOwner(), connected -> {
            Log.d("NetworkWiewModel", "Observe " + connected);
            if(connected.equals("connected"))
                networkViewModel.fetchBookedRepetitions();
            else
                networkViewModel.pollingTestServerConnection();
        });

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String aus = ""+tab.getPosition();
                Toast.makeText(getContext(),aus, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}