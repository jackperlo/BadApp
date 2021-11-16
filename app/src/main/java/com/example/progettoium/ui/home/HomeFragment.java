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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.progettoium.R;
import com.example.progettoium.data.FreeRepetitions;
import com.example.progettoium.ui.booked.BookedFragment;
import com.example.progettoium.ui.login.LoginFragment;
import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.databinding.FragmentHomeBinding;
import com.example.progettoium.ui.home.freeRepetitions.FreeRepetitionsCustomViewAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private NetworkViewModel networkViewModel;
    private FragmentHomeBinding binding;

    FreeRepetitionsCustomViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        networkViewModel = new ViewModelProvider(requireActivity()).get(NetworkViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        RecyclerView recyclerView = binding.coursesList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FreeRepetitionsCustomViewAdapter(getContext(), new ArrayList<>(), networkViewModel);
        recyclerView.setAdapter(adapter);

        /*TRYING TO GET DATA FROM LIVEDATA*/
        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), user -> {
            if(user != null)
                binding.lblWelcomeMainFragment.setText("Hi, " + user.getName() + " " + user.getSurname());
        });

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Connection...");
        progressDialog.show();
        networkViewModel.getFreeRepetitions().observe(getViewLifecycleOwner(), courseObjects -> {
            progressDialog.dismiss();
            if(courseObjects != null) {
                adapter.setData(courseObjects);  // setta i dati nella recyclerView
                binding.swipeRefreshLayoutBooking.setRefreshing(false);
            } else {
                Snackbar.make(getView(), "NO DATABASE CONNECTION", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ArrayList<FreeRepetitions> frep = new ArrayList<>();
                adapter.setData(frep);
            }
        });

        networkViewModel.getbookRepetitionData().observe(getViewLifecycleOwner(), status -> {
            ProgressDialog pdBookARepetition = adapter.getBookARepetitionDialog();
            pdBookARepetition.dismiss();
            Snackbar.make(getView(), status, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            networkViewModel.fetchFreeRepetitions(getWeekDay(binding.tabLayout.getSelectedTabPosition()));
            networkViewModel.setOnDay(getWeekDay(binding.tabLayout.getSelectedTabPosition()));
        });

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                networkViewModel.fetchFreeRepetitions(getWeekDay(tab.getPosition()));
                networkViewModel.setOnDay(getWeekDay(tab.getPosition()));
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
                networkViewModel.setOnDay(getWeekDay(binding.tabLayout.getSelectedTabPosition()));
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
                ret = "Thursday";
                break;
            case 4:
                ret = "Friday";
                break;
            default:
                break;
        }
        return ret;
    }

}