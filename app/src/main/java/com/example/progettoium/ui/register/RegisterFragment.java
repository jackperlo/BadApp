package com.example.progettoium.ui.register;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.progettoium.NetworkViewModel;
import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentRegisterBinding;
import com.example.progettoium.model.Users;
import com.example.progettoium.ui.login.LoginFragment;

public class RegisterFragment extends Fragment {

    private NetworkViewModel networkViewModel;
    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        networkViewModel = new ViewModelProvider(requireActivity()).get(NetworkViewModel.class);

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateName(binding.name) && validateEmail(binding.email) && validatePassword(binding.password)) {
                    String password = binding.password.getText().toString();
                    if(valideteRetypePassword(binding.retypePassword, password)) {
                        binding.loading.setVisibility(View.VISIBLE);
                        if (networkViewModel.registerUser(binding.name.getText().toString(), binding.email.getText().toString(), password)[0]) {
                            NavHostFragment.findNavController(RegisterFragment.this)
                                    .navigate(R.id.action_nav_register_to_nav_home);
                        } else {
                            binding.loading.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Registration failed! Try Again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        return root;
    }

    private boolean validateName(EditText name) {
        String val = name.getText().toString();
        if(val.isEmpty()) {
            name.setError("Field cannot be empty");
            return false;
        } else {
            name.setError(null);
            return true;
        }
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

    private boolean valideteRetypePassword(EditText retypePassword, String password) {
        String val = retypePassword.getText().toString();

        if(!val.equals(password)){
            retypePassword.setError("Password must be equals");
            return false;
        } else {
            retypePassword.setError(null);
            return true;
        }
    }
}