package com.example.progettoium.ui.register;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.progettoium.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateName(binding.name) && validateEmail(binding.email) && validatePassword(binding.password)) {
                    String password = binding.password.getText().toString();
                    if(valideteRetypePassword(binding.retypePassword, password)) {
                        binding.loading.setVisibility(View.VISIBLE);
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
                "(?=.*[@#$%^&+=])";  //at least 1 special character//no white spaces

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } /*else if (!val.matches(passwordVal)) {
            password.setError("Password is too weak");
            return false;
        }*/ else {
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