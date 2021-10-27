package com.example.progettoium.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettoium.NetworkViewModel;
import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentHomeBinding;
import com.example.progettoium.ui.home.courses.CoursesCustomViewAdapter;
import com.example.progettoium.ui.home.courses.CoursesTimeTableViewModel;
import com.example.progettoium.ui.home.courses.CoursesTimeTableViewModelFactory;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private NetworkViewModel networkViewModel;
    CoursesTimeTableViewModel coursesViewModel;
    private FragmentHomeBinding binding;

    CoursesCustomViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        networkViewModel = new ViewModelProvider(requireActivity()).get(NetworkViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //setting up recycler view to show courses
        CoursesTimeTableViewModelFactory factory = new CoursesTimeTableViewModelFactory(getContext());
        coursesViewModel = new ViewModelProvider(this, factory).get(CoursesTimeTableViewModel.class);
        RecyclerView recyclerView = binding.coursesList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CoursesCustomViewAdapter(getContext(), new ArrayList());
        recyclerView.setAdapter(adapter);
        coursesViewModel.fetchCourses();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*TRYING TO GET DATA FROM LIVEDATA*/
        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), ite -> {
            binding.lblWelcomeMainFragment.setText("Hi, "+ite.getName());
        });
        coursesViewModel.getCourses().observe(getViewLifecycleOwner(), userObjects -> {
            Log.i("Home Fragment", "numero corsi: " + userObjects.size());
            if (userObjects.size() > 0) {
                Log.i("Home Fragment", "Corso: " + userObjects.get(0).getCodCourse() + " | Start: " + userObjects.get(0).getStartTime());
            }
            adapter.setData(userObjects);  // setta i dati nella recyclerView
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}