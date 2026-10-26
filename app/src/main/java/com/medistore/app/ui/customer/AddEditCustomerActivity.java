package com.medistore.app.ui.customer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.databinding.ActivityAddEditCustomerBinding;
import com.medistore.app.utils.ValidationUtils;
import com.medistore.app.viewmodel.CustomerViewModel;

public class AddEditCustomerActivity extends AppCompatActivity {

    private ActivityAddEditCustomerBinding binding;
    private CustomerViewModel viewModel;
    private boolean isEditMode = false;
    private int customerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        String action = getIntent().getStringExtra("ACTION");
        if ("EDIT".equals(action)) {
            isEditMode = true;
            customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);
            getSupportActionBar().setTitle("Edit Customer");
            loadCustomerData();
        } else {
            getSupportActionBar().setTitle("Add Customer");
        }

        binding.btnSave.setOnClickListener(v -> saveCustomer());
    }

    private void loadCustomerData() {
        // Mock load
    }

    private void saveCustomer() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();

        if (!ValidationUtils.isNotEmpty(name)) {
            binding.tilName.setError("Name is required");
            return;
        }
        if (!ValidationUtils.isValidPhone(phone) || phone.length() != 10) {
            binding.tilPhone.setError("Valid 10-digit phone is required");
            return;
        }
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError("Valid email is required");
            return;
        }

        Customer customer = new Customer(name, phone, email, address);
        if (isEditMode) {
            customer.setCustomerId(customerId);
            viewModel.update(customer);
            Toast.makeText(this, "Customer updated", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(customer);
            Toast.makeText(this, "Customer added", Toast.LENGTH_SHORT).show();
        }

        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        if (!password.isEmpty()) {
            android.content.SharedPreferences prefs = getSharedPreferences(com.medistore.app.utils.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_pwd_" + phone, password);
            editor.putString("user_name_" + phone, name);
            editor.putString("user_role_" + phone, "Customer");
            if (!email.isEmpty()) {
                editor.putString("user_pwd_" + email.toLowerCase(), password);
                editor.putString("user_name_" + email.toLowerCase(), name);
                editor.putString("user_role_" + email.toLowerCase(), "Customer");
            }
            editor.apply();
        }

        finish();
    }
}
