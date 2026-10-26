package com.medistore.app.ui.supplier;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.medistore.app.data.entities.Supplier;
import com.medistore.app.databinding.ActivityAddEditSupplierBinding;
import com.medistore.app.utils.ValidationUtils;
import com.medistore.app.viewmodel.SupplierViewModel;

public class AddEditSupplierActivity extends AppCompatActivity {

    private ActivityAddEditSupplierBinding binding;
    private SupplierViewModel viewModel;
    private boolean isEditMode = false;
    private int supplierId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditSupplierBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SupplierViewModel.class);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        String action = getIntent().getStringExtra("ACTION");
        if ("EDIT".equals(action)) {
            isEditMode = true;
            supplierId = getIntent().getIntExtra("SUPPLIER_ID", -1);
            getSupportActionBar().setTitle("Edit Supplier");
            loadSupplierData();
        } else {
            getSupportActionBar().setTitle("Add Supplier");
        }

        binding.btnSave.setOnClickListener(v -> saveSupplier());
    }

    private void loadSupplierData() {
        // Normally you'd get this from DB via ViewModel.
        // Assuming we pass it or get it from repository
        // For simplicity, repository needs to provide a way to get by ID, but here we can just assume
        // we observe or pass the data.
    }

    private void saveSupplier() {
        String name = binding.etName.getText().toString().trim();
        String company = binding.etCompany.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();

        if (!ValidationUtils.isNotEmpty(name)) {
            binding.tilName.setError("Name is required");
            return;
        }
        if (!ValidationUtils.isNotEmpty(company)) {
            binding.tilCompany.setError("Company is required");
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

        Supplier supplier = new Supplier(name, company, phone, email, address);
        if (isEditMode) {
            supplier.setSupplierId(supplierId);
            viewModel.update(supplier);
            Toast.makeText(this, "Supplier updated", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(supplier);
            Toast.makeText(this, "Supplier added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
