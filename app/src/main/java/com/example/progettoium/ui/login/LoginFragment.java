package com.example.progettoium.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private NetworkViewModel networkViewModel;
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        networkViewModel = new ViewModelProvider(requireActivity()).get(NetworkViewModel.class);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_nav_login_to_nav_register);
            }
        });

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateEmail(binding.email) && validatePassword(binding.password)) {
                    if(networkViewModel.loginUser(binding.email.getText().toString(), binding.password.getText().toString())){
                        NavHostFragment.findNavController(LoginFragment.this)
                                .navigate(R.id.action_nav_login_to_nav_home);
                    } else {
                        Toast.makeText(getContext(), "Login Failed! Try Again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return root;
    }

    private boolean validateEmail(EditText email) {
        String val = email.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            email.setError("Invalid email address");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePassword(EditText password) {
        String val = password.getText().toString();
        String passwordVal =
                "(.*[0-9])";  //at least 1 numeric

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password must contain at least 1 numeric character");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }
}