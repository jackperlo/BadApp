package com.example.progettoium.ui.booked;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentBookedBinding;
import com.example.progettoium.ui.booked.bookedHistory.BookedHistoryCustomViewAdapter;
import com.example.progettoium.ui.home.bookedRepetitions.BookedRepetitionsCustomViewAdapter;
import com.example.progettoium.utils.NetworkViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class BookedFragment extends Fragment {

    private FragmentBookedBinding binding;
    private NetworkViewModel networkViewModel;

    BookedHistoryCustomViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        networkViewModel = new ViewModelProvider(requireActivity()).get(NetworkViewModel.class);

        binding = FragmentBookedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.coursesListHistory;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookedHistoryCustomViewAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        networkViewModel.fetchBookedHistory(getTabState(binding.tabLayout.getSelectedTabPosition()));
        networkViewModel.testServerConnection("0", "check_connection_server");

        networkViewModel.getBookedRepetitions().observe(getViewLifecycleOwner(), courseObjects -> {
            if(courseObjects != null) {
                adapter.setData(courseObjects);
                binding.swipeRefreshLayoutBooked.setRefreshing(false);
            } else {
                Snackbar.make(getView(), "NO DATABASE CONNECTION", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                networkViewModel.fetchBookedHistory(getTabState(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(BookedFragment.this)
                        .navigate(R.id.action_nav_booked_to_nav_home);
            }
        });

        binding.swipeRefreshLayoutBooked.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkViewModel.fetchBookedHistory(getTabState(binding.tabLayout.getSelectedTabPosition()));
            }
        });

        return root;
    }

    private String getTabState(int tabValue) {
        String state = "Active";
        if (tabValue == 0)
            state = "Active";
        else if (tabValue == 1)
            state = "Cancelled";
        else
            state = "Done";

        return state;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}