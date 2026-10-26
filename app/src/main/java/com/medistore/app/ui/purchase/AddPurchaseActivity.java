package com.medistore.app.ui.purchase;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Purchase;
import com.medistore.app.data.entities.Supplier;
import com.medistore.app.databinding.ActivityAddPurchaseBinding;
import com.medistore.app.utils.CurrencyUtils;
import com.medistore.app.utils.DateUtils;
import com.medistore.app.viewmodel.PurchaseViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddPurchaseActivity extends AppCompatActivity {
    private ActivityAddPurchaseBinding binding;
    private PurchaseViewModel viewModel;
    
    private Supplier selectedSupplier;
    private Medicine selectedMedicine;
    private List<Supplier> supplierList = new ArrayList<>();
    private List<Medicine> medicineList = new ArrayList<>();
    private ArrayAdapter<Supplier> supplierAdapter;
    private ArrayAdapter<Medicine> medicineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPurchaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PurchaseViewModel.class);

        setupToolbar();
        setupDefaultDates();
        setupObservers();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Purchase (Restock)");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDefaultDates() {
        // Pre-fill today's date
        binding.etDate.setText(DateUtils.getTodayDate());

        // Default expiry date to 1 year ahead
        setExpiryYearsAhead(1);
    }

    private void setExpiryYearsAhead(int years) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, years);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.etExpiry.setText(sdf.format(cal.getTime()));
    }

    private void setupObservers() {
        viewModel.getAllSuppliers().observe(this, suppliers -> {
            if (suppliers != null) {
                supplierList = suppliers;
                supplierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suppliers);
                binding.acSupplier.setAdapter(supplierAdapter);
                binding.acSupplier.setThreshold(1);
            }
        });

        viewModel.getAllMedicines().observe(this, medicines -> {
            if (medicines != null) {
                medicineList = medicines;
                medicineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, medicines);
                binding.acMedicine.setAdapter(medicineAdapter);
                binding.acMedicine.setThreshold(1);

                // Check if opened with a pre-selected MEDICINE_ID (e.g. from Low Stock Restock button)
                int preselectedId = getIntent().getIntExtra("MEDICINE_ID", -1);
                if (preselectedId != -1) {
                    for (Medicine m : medicines) {
                        if (m.getMedicineId() == preselectedId) {
                            selectMedicine(m);
                            binding.acMedicine.setText(m.getName(), false);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setupListeners() {
        // Medicine click & selection
        binding.acMedicine.setOnClickListener(v -> binding.acMedicine.showDropDown());
        binding.acMedicine.setOnItemClickListener((parent, view, position, id) -> {
            selectedMedicine = (Medicine) parent.getItemAtPosition(position);
            if (selectedMedicine != null) {
                selectMedicine(selectedMedicine);
            }
        });

        // Supplier click & selection
        binding.acSupplier.setOnClickListener(v -> binding.acSupplier.showDropDown());
        binding.acSupplier.setOnItemClickListener((parent, view, position, id) -> {
            selectedSupplier = (Supplier) parent.getItemAtPosition(position);
        });

        // Auto-generate Batch Number button
        binding.btnAutoBatch.setOnClickListener(v -> generateNewBatch());

        // Quick Expiry buttons
        binding.btnExpiry1yr.setOnClickListener(v -> setExpiryYearsAhead(1));
        binding.btnExpiry2yr.setOnClickListener(v -> setExpiryYearsAhead(2));

        // Date picker for Purchase Date
        binding.etDate.setOnClickListener(v -> showDatePicker(binding.etDate, false));

        // Date picker for Expiry Date
        binding.etExpiry.setOnClickListener(v -> showDatePicker(binding.etExpiry, true));

        // Live Cost calculation watcher
        TextWatcher costWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalCostPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        binding.etQty.addTextChangedListener(costWatcher);
        binding.etPrice.addTextChangedListener(costWatcher);

        // Save button
        binding.btnSave.setOnClickListener(v -> savePurchase());
    }

    private void selectMedicine(Medicine medicine) {
        selectedMedicine = medicine;

        // Auto pre-fill price
        if (medicine.getPurchasePrice() > 0) {
            binding.etPrice.setText(String.format(Locale.getDefault(), "%.2f", medicine.getPurchasePrice()));
        }

        // Auto pre-fill batch if exists or generate one
        if (medicine.getBatchNumber() != null && !medicine.getBatchNumber().isEmpty()) {
            binding.etBatch.setText(medicine.getBatchNumber());
        } else {
            generateNewBatch();
        }

        // Auto pre-fill expiry if exists
        if (medicine.getExpiryDate() != null && !medicine.getExpiryDate().isEmpty()) {
            binding.etExpiry.setText(medicine.getExpiryDate());
        }

        // If medicine has a linked supplier, auto-select it
        if (medicine.getSupplierId() != null && supplierList != null) {
            for (Supplier s : supplierList) {
                if (s.getSupplierId() == medicine.getSupplierId()) {
                    selectedSupplier = s;
                    binding.acSupplier.setText(s.getName(), false);
                    break;
                }
            }
        }

        updateTotalCostPreview();
    }

    private void generateNewBatch() {
        int randomNum = 1000 + new Random().nextInt(9000);
        binding.etBatch.setText("BAT-" + randomNum);
    }

    private void updateTotalCostPreview() {
        String qtyStr = binding.etQty.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();

        if (!qtyStr.isEmpty() && !priceStr.isEmpty()) {
            try {
                int qty = Integer.parseInt(qtyStr);
                double price = Double.parseDouble(priceStr);
                double total = qty * price;
                binding.tvTotalPurchaseCost.setText(CurrencyUtils.formatCurrency(total));
                return;
            } catch (NumberFormatException ignored) {}
        }
        binding.tvTotalPurchaseCost.setText("₹0.00");
    }

    private void showDatePicker(com.google.android.material.textfield.TextInputEditText targetField, boolean isExpiry) {
        Calendar cal = Calendar.getInstance();
        if (isExpiry) {
            cal.add(Calendar.YEAR, 1);
        }

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            targetField.setText(formatted);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void savePurchase() {
        if (selectedMedicine == null) {
            Toast.makeText(this, "Please select a medicine", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSupplier == null) {
            // If user typed a supplier name directly, try matching or assign first supplier
            String typed = binding.acSupplier.getText().toString().trim();
            if (!typed.isEmpty() && supplierList != null) {
                for (Supplier s : supplierList) {
                    if (s.getName().equalsIgnoreCase(typed)) {
                        selectedSupplier = s;
                        break;
                    }
                }
            }
            if (selectedSupplier == null && supplierList != null && !supplierList.isEmpty()) {
                selectedSupplier = supplierList.get(0);
            }
        }

        if (selectedSupplier == null) {
            Toast.makeText(this, "Please select a supplier", Toast.LENGTH_SHORT).show();
            return;
        }

        String batch = binding.etBatch.getText().toString().trim();
        String qtyStr = binding.etQty.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        String date = binding.etDate.getText().toString().trim();
        String expiry = binding.etExpiry.getText().toString().trim();

        if (batch.isEmpty()) {
            generateNewBatch();
            batch = binding.etBatch.getText().toString().trim();
        }

        if (qtyStr.isEmpty()) {
            binding.etQty.setError("Quantity required");
            return;
        }

        if (priceStr.isEmpty()) {
            binding.etPrice.setError("Price required");
            return;
        }

        int qty;
        double price;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                binding.etQty.setError("Must be greater than 0");
                return;
            }
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                binding.etPrice.setError("Invalid price");
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number entered", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) date = DateUtils.getTodayDate();
        if (expiry.isEmpty()) expiry = DateUtils.getThresholdDate(365);

        Purchase purchase = new Purchase();
        purchase.setSupplierId(selectedSupplier.getSupplierId());
        purchase.setSupplierName(selectedSupplier.getName());
        purchase.setMedicineId(selectedMedicine.getMedicineId());
        purchase.setMedicineName(selectedMedicine.getName());
        purchase.setBatchNumber(batch);
        purchase.setQuantity(qty);
        purchase.setPurchasePrice(price);
        purchase.setPurchaseDate(date);
        purchase.setExpiryDate(expiry);
        purchase.setCreatedAt(System.currentTimeMillis());

        viewModel.insert(purchase);

        Toast.makeText(this, "✅ Successfully added " + qty + " units of " + selectedMedicine.getName() + " to stock!", Toast.LENGTH_LONG).show();
        finish();
    }
}
