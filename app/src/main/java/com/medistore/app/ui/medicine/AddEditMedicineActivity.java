package com.medistore.app.ui.medicine;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Supplier;
import com.medistore.app.databinding.ActivityAddEditMedicineBinding;
import com.medistore.app.viewmodel.MedicineViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditMedicineActivity extends AppCompatActivity {
    private ActivityAddEditMedicineBinding binding;
    private MedicineViewModel medicineViewModel;
    private String action;
    private int medicineId = -1;
    private List<Supplier> suppliersList;
    private final String[] categories = {"Tablets", "Capsule", "Syrup", "Injection", "Cream", "Drops", "Powder", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        binding.actvCategory.setAdapter(categoryAdapter);

        medicineViewModel.getAllSuppliers().observe(this, suppliers -> {
            this.suppliersList = suppliers;
            String[] supplierNames = new String[suppliers.size()];
            for (int i = 0; i < suppliers.size(); i++) {
                supplierNames[i] = suppliers.get(i).getName();
            }
            ArrayAdapter<String> supplierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, supplierNames);
            binding.actvSupplier.setAdapter(supplierAdapter);
        });

        action = getIntent().getStringExtra("ACTION");
        if ("EDIT".equals(action)) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Medicine");
            medicineId = getIntent().getIntExtra("MEDICINE_ID", -1);
            if (medicineId != -1) {
                medicineViewModel.getMedicineById(medicineId).observe(this, this::populateFields);
            }
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Add Medicine");
        }

        setupDatePickers();

        binding.btnSave.setOnClickListener(v -> saveMedicine());
    }

    private void setupDatePickers() {
        binding.etMfgDate.setOnClickListener(v -> showDatePicker(true));
        binding.etExpiryDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isMfgDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isMfgDate ? "Select Manufacturing Date" : "Select Expiry Date")
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = sdf.format(new Date(selection));
            if (isMfgDate) {
                binding.etMfgDate.setText(date);
            } else {
                binding.etExpiryDate.setText(date);
            }
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void populateFields(Medicine medicine) {
        if (medicine == null) return;
        binding.etName.setText(medicine.getName());
        binding.actvCategory.setText(medicine.getCategory(), false);
        binding.etManufacturer.setText(medicine.getManufacturer());
        binding.etBatchNumber.setText(medicine.getBatchNumber());
        binding.etPurchasePrice.setText(String.valueOf(medicine.getPurchasePrice()));
        binding.etSellingPrice.setText(String.valueOf(medicine.getSellingPrice()));
        binding.etQuantity.setText(String.valueOf(medicine.getQuantity()));
        binding.etMinStock.setText(String.valueOf(medicine.getMinimumStock()));
        binding.etMfgDate.setText(medicine.getManufacturingDate());
        binding.etExpiryDate.setText(medicine.getExpiryDate());
        binding.etDescription.setText(medicine.getDescription());
        
        if (medicine.getSupplierId() != null && suppliersList != null) {
            for (Supplier supplier : suppliersList) {
                if (supplier.getSupplierId() == medicine.getSupplierId()) {
                    binding.actvSupplier.setText(supplier.getName(), false);
                    break;
                }
            }
        }
    }

    private void saveMedicine() {
        String name = binding.etName.getText().toString().trim();
        String category = binding.actvCategory.getText().toString().trim();
        String manufacturer = binding.etManufacturer.getText().toString().trim();
        String batchNumber = binding.etBatchNumber.getText().toString().trim();
        String purchasePriceStr = binding.etPurchasePrice.getText().toString().trim();
        String sellingPriceStr = binding.etSellingPrice.getText().toString().trim();
        String quantityStr = binding.etQuantity.getText().toString().trim();
        String minStockStr = binding.etMinStock.getText().toString().trim();
        String mfgDate = binding.etMfgDate.getText().toString().trim();
        String expiryDate = binding.etExpiryDate.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String supplierName = binding.actvSupplier.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || manufacturer.isEmpty() || batchNumber.isEmpty() || expiryDate.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Please fill all required fields", Snackbar.LENGTH_LONG).show();
            return;
        }

        double purchasePrice = purchasePriceStr.isEmpty() ? 0 : Double.parseDouble(purchasePriceStr);
        double sellingPrice = sellingPriceStr.isEmpty() ? 0 : Double.parseDouble(sellingPriceStr);
        int quantity = quantityStr.isEmpty() ? 0 : Integer.parseInt(quantityStr);
        int minStock = minStockStr.isEmpty() ? 0 : Integer.parseInt(minStockStr);

        Integer supplierId = null;
        if (!supplierName.isEmpty() && suppliersList != null) {
            for (Supplier s : suppliersList) {
                if (s.getName().equals(supplierName)) {
                    supplierId = s.getSupplierId();
                    break;
                }
            }
        }

        Medicine medicine = new Medicine();
        if (medicineId != -1) {
            medicine.setMedicineId(medicineId);
        }
        medicine.setName(name);
        medicine.setCategory(category);
        medicine.setManufacturer(manufacturer);
        medicine.setBatchNumber(batchNumber);
        medicine.setPurchasePrice(purchasePrice);
        medicine.setSellingPrice(sellingPrice);
        medicine.setQuantity(quantity);
        medicine.setMinimumStock(minStock);
        medicine.setManufacturingDate(mfgDate);
        medicine.setExpiryDate(expiryDate);
        medicine.setSupplierId(supplierId);
        medicine.setDescription(description);

        if ("EDIT".equals(action)) {
            medicineViewModel.update(medicine);
            Toast.makeText(this, "Medicine updated", Toast.LENGTH_SHORT).show();
        } else {
            medicine.setCreatedAt(System.currentTimeMillis());
            medicineViewModel.insert(medicine);
            Toast.makeText(this, "Medicine added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
