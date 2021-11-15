package com.example.progettoium.ui.home;

import android.app.ProgressDialog;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.progettoium.ui.MainActivity;
import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.databinding.FragmentHomeBinding;
import com.example.progettoium.ui.home.bookedRepetitions.BookedRepetitionsCustomViewAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Observable;

public class HomeFragment extends Fragment {

    private NetworkViewModel networkViewModel;
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
            if(user != null)
                binding.lblWelcomeMainFragment.setText("Hi, " + user.getName() + " " + user.getSurname());
        });

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Connessione...");
        progressDialog.show();
        networkViewModel.getFreeRepetitions().observe(getViewLifecycleOwner(), courseObjects -> {
            progressDialog.dismiss();
            if(courseObjects != null) {
                adapter.setData(courseObjects);  // setta i dati nella recyclerView
                binding.swipeRefreshLayoutBooking.setRefreshing(false);
            } else {
                Snackbar.make(getView(), "NO DATABASE CONNECTION", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                networkViewModel.fetchFreeRepetitions(getWeekDay(tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.swipeRefreshLayoutBooking.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkViewModel.fetchFreeRepetitions(getWeekDay(binding.tabLayout.getSelectedTabPosition()));
            }
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

    private String getWeekDay(int tabValue){
        String ret = "";
        switch (tabValue){
            case 0:
                ret = "Monday";
                break;
            case 1:
                ret = "Tuesday";
                break;
            case 2:
                ret = "Wednesday";
                break;
            case 3:
                ret = "Thurday";
                break;
            case 4:
                ret = "Friday";
                break;
            default:
                break;
        }
        return ret;
    }

    public void fetchFreeRepetitions(){
        TabLayout tabLayout = binding.tabLayout;
        networkViewModel.fetchFreeRepetitions(getWeekDay(tabLayout.getSelectedTabPosition()));
    }
}