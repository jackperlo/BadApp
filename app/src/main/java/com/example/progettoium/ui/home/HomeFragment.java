package com.example.progettoium.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettoium.NetworkViewModel;
import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentHomeBinding;
import com.example.progettoium.model.Courses;
import com.example.progettoium.model.coursestimetable.CoursesCustomViewAdapter;
import com.example.progettoium.model.coursestimetable.CoursesTimeTableViewModel;
import com.example.progettoium.model.coursestimetable.CoursesTimeTableViewModelFactory;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private NetworkViewModel networkViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        networkViewModel = new ViewModelProvider(requireActivity()).get(NetworkViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*TRYING TO GET DATA FROM LIVEDATA*/
        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), ite -> {
            binding.lblWelcomeMainFragment.setText("Hi, "+ite.getName());
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}