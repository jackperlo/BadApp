package com.example.progettoium.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentLoginBinding;
import com.example.progettoium.databinding.FragmentRegisterBinding;
import com.example.progettoium.ui.register.RegisterFragment;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText txtUsername = binding.username;
        final EditText txtPassword = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar progressBar = binding.loading;

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = new RegisterFragment();
                FragmentManager fm = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.nav_login, fr);
                fragmentTransaction.commit();
            }
        });

        return root;
    }
}