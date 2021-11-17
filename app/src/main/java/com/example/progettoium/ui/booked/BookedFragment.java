package com.example.progettoium.ui.booked;

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
import com.example.progettoium.ui.login.LoginFragment;
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
        adapter = new BookedHistoryCustomViewAdapter(getContext(), new ArrayList<>(), networkViewModel);
        recyclerView.setAdapter(adapter);

        networkViewModel.setOnState(getTabState(0));

        networkViewModel.fetchBookedHistory();
        networkViewModel.testServerConnection("0", "check_connection_server");

        networkViewModel.getBookedRepetitions().observe(getViewLifecycleOwner(), courseObjects -> {
            if(courseObjects != null) {
                adapter.setData(courseObjects);
                binding.swipeRefreshLayoutBooked.setRefreshing(false);
            } else {
                String out = getContext().getResources().getString(R.string.no_db_connection);
                Snackbar.make(getView(), out, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), user -> {
            if(user.second.equals("session expired")) {
                NavHostFragment.findNavController(BookedFragment.this)
                        .navigate(R.id.action_nav_booked_to_nav_home);
            }
        });

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                networkViewModel.setOnState(getTabState(tab.getPosition()));
                networkViewModel.fetchBookedHistory();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.swipeRefreshLayoutBooked.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkViewModel.fetchBookedHistory();
            }
        });

        networkViewModel.getManaged().observe(getViewLifecycleOwner(), manged -> {
            adapter.progressDialog.dismiss();
            if(manged == null) {
                String out = getContext().getResources().getString(R.string.no_db_connection);
                Snackbar.make(getView(), out, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else if (manged){
                networkViewModel.fetchBookedHistory();
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