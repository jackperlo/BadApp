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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentBookedBinding;
import com.example.progettoium.ui.booked.bookedHistory.BookedHistoryCustomViewAdapter;
import com.example.progettoium.ui.home.bookedRepetitions.BookedRepetitionsCustomViewAdapter;
import com.example.progettoium.utils.NetworkViewModel;
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

        //getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
        //TODO: controllare se cade la connessione cosa fare, oppure essere sicuri che la  connessione ci sia
        networkViewModel.fetchBookedHistory();
        networkViewModel.testServerConnection("0", "check_connection_server");

        networkViewModel.getBookedRepetitions().observe(getViewLifecycleOwner(), courseObjects -> {
            if(courseObjects != null)
                adapter.setData(courseObjects);
        });

        TabLayout tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                networkViewModel.setSelectedHistoryTab(tab.getPosition());
                networkViewModel.fetchBookedHistory();
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

        return root;
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