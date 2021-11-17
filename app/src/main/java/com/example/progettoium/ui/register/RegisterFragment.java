package com.example.progettoium.ui.register;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentRegisterBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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

        //getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getContext().getResources().getString(R.string.connection));

        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), user -> {
            if(user.second.equals("registration")) {
                progressDialog.dismiss();
                if (user.first == null) {
                    String out = getContext().getResources().getString(R.string.no_db_connection);
                    Snackbar.make(getView(), out, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if (user.first.isEmpty()) {
                        String out = getContext().getResources().getString(R.string.registration_failed);
                        Toast.makeText(getContext(), out, Toast.LENGTH_LONG).show();
                    }else {
                        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                        navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
                        navigationView.getMenu().findItem(R.id.nav_booked).setVisible(true);
                        getActivity().findViewById(R.id.btnLogOut).setVisibility(View.VISIBLE);

                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SESSION", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("session_token", user.first.getToken());
                        editor.apply();

                        networkViewModel.setSessionToken(user.first.getToken());
                        networkViewModel.fetchFreeRepetitions(getWeekDay(0));
                        NavHostFragment.findNavController(RegisterFragment.this).navigate(R.id.action_nav_register_to_nav_home);
                    }
                }
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateSurname(binding.surname) && validateName(binding.name) && validateEmail(binding.email) && validatePassword(binding.password)) {
                    String password = binding.password.getText().toString();
                    if (valideteRetypePassword(binding.retypePassword, password)) {
                        // N.B. Gli Admin vanno aggiunti direttamente dal phpmyadmin!
                        //QUI TUTTE LE REGISTRAZIONI SONO DI ROLE 'CLIENT'
                        progressDialog.show();
                        networkViewModel.registerUser(binding.email.getText().toString(), password, binding.name.getText().toString(), binding.surname.getText().toString());
                    }
                }
            }
        });

        return root;
    }

    private boolean validateSurname(EditText surname) {
        String val = surname.getText().toString();
        if (val.isEmpty()) {
            surname.setError("Field cannot be empty");
            return false;
        } else {
            surname.setError(null);
            return true;
        }
    }


    private boolean validateName(EditText name) {
        String val = name.getText().toString();
        if (val.isEmpty()) {
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
        String passwordVal = "^(?=.*?[a-z].*?[a-z].*?[a-z])(?=.*?[\\d]).*$";  //at least 1 digit and 3 general character

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password must contain at least 1 digit and 3 characters lowercase");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean valideteRetypePassword(EditText retypePassword, String password) {
        String val = retypePassword.getText().toString();

        if (!val.equals(password)) {
            retypePassword.setError("Password must be equals");
            return false;
        } else {
            retypePassword.setError(null);
            return true;
        }
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