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

        TabLayout tabLayout = binding.tabLayout;

        /*TRYING TO GET DATA FROM LIVEDATA*/
        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), user -> {
            if(user.first != null)
                binding.lblWelcomeMainFragment.setText("Hi, " + user.first.getName() + " " + user.first.getSurname());
        });

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getContext().getResources().getString(R.string.connection));
        progressDialog.show();
        networkViewModel.getFreeRepetitions().observe(getViewLifecycleOwner(), courseObjects -> {
            if(progressDialog != null)
                progressDialog.dismiss();

            if(courseObjects != null) {
                adapter.setData(courseObjects);  // setta i dati nella recyclerView
            } else {
                String out = getContext().getResources().getString(R.string.no_db_connection);
                Snackbar.make(getView(), out, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ArrayList<FreeRepetitions> frep = new ArrayList<>();
                adapter.setData(frep);
            }

            binding.swipeRefreshLayoutBooking.setRefreshing(false);
        });

        networkViewModel.getbookRepetitionData().observe(getViewLifecycleOwner(), status -> {
            if(status != null){
                if(status.equals("no_session")) {
                    if(progressDialog != null)
                        progressDialog.dismiss();
                } else {
                    ProgressDialog pdBookARepetition = adapter.getBookARepetitionDialog();
                    if(pdBookARepetition != null)
                        pdBookARepetition.dismiss();

                    Snackbar.make(getView(), status, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    networkViewModel.fetchFreeRepetitions(getWeekDay(binding.tabLayout.getSelectedTabPosition()));
                    networkViewModel.setOnDay(getWeekDay(binding.tabLayout.getSelectedTabPosition()));
                }
            }
        });
        networkViewModel.getbookRepetitionData().setValue(null);

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