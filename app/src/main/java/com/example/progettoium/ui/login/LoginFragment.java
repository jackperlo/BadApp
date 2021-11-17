package com.example.progettoium.ui.login;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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

import com.example.progettoium.ui.MainActivity;
import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.R;
import com.example.progettoium.databinding.FragmentLoginBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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

        networkViewModel.testServerConnection("0", "check_connection_server");

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_nav_login_to_nav_register);
            }
        });

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Connection...");
        networkViewModel.getRegisteredUser().observe(getViewLifecycleOwner(), user -> {
            if(user.second.equals("login")) {
                progressDialog.dismiss();
                if (user.first == null) {
                    String out = getContext().getResources().getString(R.string.no_db_connection);
                    Snackbar.make(getView(), out, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else if (user.first.isEmpty()) {
                    String out = getContext().getResources().getString(R.string.login_failed_fragment);
                    Toast.makeText(getContext(), out, Toast.LENGTH_LONG).show();
                } else {
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
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_nav_login_to_nav_home);
                }
            }
        });

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail(binding.email) && validatePassword(binding.password)) {
                    progressDialog.show();
                    networkViewModel.loginUser(binding.email.getText().toString(), binding.password.getText().toString());
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