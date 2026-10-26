package com.medistore.app.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.medistore.app.databinding.FragmentSettingsBinding;
import com.medistore.app.utils.Constants;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        loadSettings();
        applyRoleRestrictions();

        binding.btnSave.setOnClickListener(v -> saveSettings());
        binding.btnLogout.setOnClickListener(v -> logout());

        // Glitch-free dark mode toggle: only react to physical user touch
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                // Immediately persist setting
                prefs.edit().putBoolean(Constants.KEY_DARK_MODE, isChecked).apply();
                // Apply theme mode
                AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void applyRoleRestrictions() {
        String role = prefs.getString("user_role", "Store Administrator");
        boolean isCustomer = "Customer".equalsIgnoreCase(role);

        if (isCustomer) {
            // Customer cannot change store information
            binding.tvStoreInfoTitle.setText("Store Contact Information");
            binding.tvCustomerNote.setText("Official store contact details (Read-only for customers)");
            binding.tvCustomerNote.setVisibility(View.VISIBLE);

            // Make inputs read-only / disabled
            binding.etStoreName.setEnabled(false);
            binding.etStoreAddress.setEnabled(false);
            binding.etStorePhone.setEnabled(false);
            binding.etStoreEmail.setEnabled(false);

            binding.etStoreName.setFocusable(false);
            binding.etStoreAddress.setFocusable(false);
            binding.etStorePhone.setFocusable(false);
            binding.etStoreEmail.setFocusable(false);

            // Hide the Save Settings button for customers
            binding.btnSave.setVisibility(View.GONE);
        } else {
            binding.tvStoreInfoTitle.setText("Store Information");
            binding.tvCustomerNote.setText("Configure store branding and receipt details");
            binding.btnSave.setVisibility(View.VISIBLE);
        }
    }

    private void loadSettings() {
        binding.etStoreName.setText(prefs.getString(Constants.KEY_STORE_NAME, "MediStore"));
        binding.etStoreAddress.setText(prefs.getString(Constants.KEY_STORE_ADDRESS, "123 Medical Center Road, City"));
        binding.etStorePhone.setText(prefs.getString(Constants.KEY_STORE_PHONE, "9876543210"));
        binding.etStoreEmail.setText(prefs.getString(Constants.KEY_STORE_EMAIL, "support@medistore.com"));

        // Set switches without triggering buttonView.isPressed()
        binding.switchDarkMode.setChecked(prefs.getBoolean(Constants.KEY_DARK_MODE, false));
        binding.switchNotifications.setChecked(prefs.getBoolean(Constants.KEY_NOTIFICATIONS, true));
    }

    private void saveSettings() {
        String role = prefs.getString("user_role", "Store Administrator");
        if ("Customer".equalsIgnoreCase(role)) {
            Toast.makeText(requireContext(), "Only Administrators can modify store information", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_STORE_NAME, binding.etStoreName.getText().toString());
        editor.putString(Constants.KEY_STORE_ADDRESS, binding.etStoreAddress.getText().toString());
        editor.putString(Constants.KEY_STORE_PHONE, binding.etStorePhone.getText().toString());
        editor.putString(Constants.KEY_STORE_EMAIL, binding.etStoreEmail.getText().toString());
        editor.putBoolean(Constants.KEY_DARK_MODE, binding.switchDarkMode.isChecked());
        editor.putBoolean(Constants.KEY_NOTIFICATIONS, binding.switchNotifications.isChecked());
        editor.apply();

        Toast.makeText(requireContext(), "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        prefs.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, false).apply();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), com.medistore.app.ui.login.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
