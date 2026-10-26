package com.medistore.app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.medistore.app.data.database.AppDatabase;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.databinding.ActivityLoginBinding;
import com.medistore.app.databinding.DialogRegisterCustomerBinding;
import com.medistore.app.ui.main.MainActivity;
import com.medistore.app.utils.Constants;
import com.medistore.app.utils.ValidationUtils;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            navigateToMain();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Restore credentials if remembered
        if (sharedPreferences.getBoolean(Constants.KEY_REMEMBER_ME, false)) {
            binding.etUsername.setText(sharedPreferences.getString(Constants.KEY_USERNAME, ""));
            binding.etPassword.setText(sharedPreferences.getString(Constants.KEY_PASSWORD, ""));
            binding.cbRememberMe.setChecked(true);
        }

        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.btnRegister.setOnClickListener(v -> showRegistrationDialog());
    }

    private void performLogin() {
        String username = binding.etUsername.getText() != null ? binding.etUsername.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(username)) {
            binding.etUsername.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            return;
        }

        // 1. Check default Admin credentials
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            saveLoginSession("Admin", "Store Administrator", username, password);
            Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();
            navigateToMain();
            return;
        }

        // 2. Check registered Customer credentials in SharedPreferences
        String savedPwd = sharedPreferences.getString("user_pwd_" + username.toLowerCase(), null);
        if (savedPwd != null && savedPwd.equals(password)) {
            String displayName = sharedPreferences.getString("user_name_" + username.toLowerCase(), username);
            saveLoginSession(displayName, "Customer", username, password);
            Toast.makeText(this, "Welcome, " + displayName + "!", Toast.LENGTH_SHORT).show();
            navigateToMain();
            return;
        }

        Snackbar.make(binding.getRoot(), "Invalid credentials. If you are a new customer, please register below.", Snackbar.LENGTH_LONG).show();
    }

    private void saveLoginSession(String displayName, String role, String inputUser, String inputPass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_USERNAME, displayName);
        editor.putString("user_role", role);
        
        if (binding.cbRememberMe.isChecked()) {
            editor.putBoolean(Constants.KEY_REMEMBER_ME, true);
            editor.putString(Constants.KEY_USERNAME, inputUser);
            editor.putString(Constants.KEY_PASSWORD, inputPass);
        } else {
            editor.putBoolean(Constants.KEY_REMEMBER_ME, false);
            editor.remove(Constants.KEY_PASSWORD);
        }
        editor.apply();
    }

    private void showRegistrationDialog() {
        DialogRegisterCustomerBinding regBinding = DialogRegisterCustomerBinding.inflate(LayoutInflater.from(this));

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(regBinding.getRoot())
                .setCancelable(true)
                .create();

        regBinding.btnRegCancel.setOnClickListener(v -> dialog.dismiss());

        regBinding.btnRegSubmit.setOnClickListener(v -> {
            String name = regBinding.etRegName.getText() != null ? regBinding.etRegName.getText().toString().trim() : "";
            String phone = regBinding.etRegPhone.getText() != null ? regBinding.etRegPhone.getText().toString().trim() : "";
            String email = regBinding.etRegEmail.getText() != null ? regBinding.etRegEmail.getText().toString().trim() : "";
            String password = regBinding.etRegPassword.getText() != null ? regBinding.etRegPassword.getText().toString().trim() : "";
            String address = regBinding.etRegAddress.getText() != null ? regBinding.etRegAddress.getText().toString().trim() : "";

            if (!ValidationUtils.isNotEmpty(name)) {
                regBinding.tilRegName.setError("Name is required");
                return;
            }
            regBinding.tilRegName.setError(null);

            if (!ValidationUtils.isValidPhone(phone) || phone.length() != 10) {
                regBinding.tilRegPhone.setError("Valid 10-digit phone number is required");
                return;
            }
            regBinding.tilRegPhone.setError(null);

            if (!ValidationUtils.isNotEmpty(email)) {
                regBinding.tilRegEmail.setError("Email or Username is required");
                return;
            }
            regBinding.tilRegEmail.setError(null);

            if (password.length() < 4) {
                regBinding.tilRegPassword.setError("Password must be at least 4 characters");
                return;
            }
            regBinding.tilRegPassword.setError(null);

            // Save Customer to Room Database
            Customer customer = new Customer(name, phone, email, address);
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(LoginActivity.this).customerDao().insertCustomer(customer);
            });

            // Save credentials in SharedPreferences (support sign-in via username/email or phone)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_pwd_" + email.toLowerCase(), password);
            editor.putString("user_pwd_" + phone, password);
            editor.putString("user_name_" + email.toLowerCase(), name);
            editor.putString("user_name_" + phone, name);
            editor.putString("user_role_" + email.toLowerCase(), "Customer");
            editor.putString("user_role_" + phone, "Customer");
            editor.apply();

            dialog.dismiss();

            // Populate login fields for easy sign-in
            binding.etUsername.setText(email);
            binding.etPassword.setText(password);

            Snackbar.make(binding.getRoot(), "Account created for " + name + "! Tap LOGIN to sign in.", Snackbar.LENGTH_LONG).show();
        });

        dialog.show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
