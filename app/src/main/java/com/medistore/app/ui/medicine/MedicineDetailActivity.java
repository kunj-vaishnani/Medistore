package com.medistore.app.ui.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.medistore.app.R;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.databinding.ActivityMedicineDetailBinding;
import com.medistore.app.utils.DateUtils;
import com.medistore.app.viewmodel.MedicineViewModel;

import java.util.Locale;

public class MedicineDetailActivity extends AppCompatActivity {
    private ActivityMedicineDetailBinding binding;
    private MedicineViewModel medicineViewModel;
    private Medicine currentMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicineDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        int medicineId = getIntent().getIntExtra("MEDICINE_ID", -1);
        if (medicineId != -1) {
            medicineViewModel.getMedicineById(medicineId).observe(this, medicine -> {
                if (medicine != null) {
                    this.currentMedicine = medicine;
                    populateDetails(medicine);
                } else {
                    finish();
                }
            });
        }

        binding.btnEdit.setOnClickListener(v -> {
            if (currentMedicine != null) {
                Intent intent = new Intent(this, AddEditMedicineActivity.class);
                intent.putExtra("ACTION", "EDIT");
                intent.putExtra("MEDICINE_ID", currentMedicine.getMedicineId());
                startActivity(intent);
            }
        });

        binding.btnDelete.setOnClickListener(v -> {
            if (currentMedicine != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Medicine")
                        .setMessage("Are you sure you want to delete this medicine?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            medicineViewModel.delete(currentMedicine);
                            Toast.makeText(this, "Medicine deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void populateDetails(Medicine medicine) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(medicine.getName());
        }

        binding.tvName.setText(medicine.getName());
        binding.tvCategory.setText(medicine.getCategory());
        
        String status = getStockStatus(medicine);
        binding.tvStatus.setText(status);
        updateStatusStyle(status);

        binding.tvManufacturer.setText(medicine.getManufacturer());
        binding.tvBatchNumber.setText(medicine.getBatchNumber());
        binding.tvQuantity.setText(String.valueOf(medicine.getQuantity()));
        binding.tvMinStock.setText(String.valueOf(medicine.getMinimumStock()));
        binding.tvPurchasePrice.setText(String.format(Locale.getDefault(), "₹%.2f", medicine.getPurchasePrice()));
        binding.tvSellingPrice.setText(String.format(Locale.getDefault(), "₹%.2f", medicine.getSellingPrice()));
        binding.tvMfgDate.setText(medicine.getManufacturingDate());
        binding.tvExpiryDate.setText(medicine.getExpiryDate());
        binding.tvDescription.setText(medicine.getDescription() != null && !medicine.getDescription().isEmpty() ? medicine.getDescription() : "N/A");
        
        binding.tvSupplier.setText(medicine.getSupplierId() != null ? "Supplier ID: " + medicine.getSupplierId() : "N/A");

        // Role-based visibility
        android.content.SharedPreferences prefs = getSharedPreferences(
                com.medistore.app.utils.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        boolean isCustomer = "Customer".equalsIgnoreCase(prefs.getString("user_role", "Store Administrator"));
        if (isCustomer) {
            binding.btnEdit.setVisibility(android.view.View.GONE);
            binding.btnDelete.setVisibility(android.view.View.GONE);
            binding.layoutPurchasePrice.setVisibility(android.view.View.GONE);
            binding.tvSupplier.setVisibility(android.view.View.GONE);
        }
    }

    private String getStockStatus(Medicine medicine) {
        if (DateUtils.isExpired(medicine.getExpiryDate())) {
            return "Expired";
        } else if (medicine.getQuantity() <= 0) {
            return "Out of Stock";
        } else if (medicine.getQuantity() <= medicine.getMinimumStock()) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
    
    private void updateStatusStyle(String status) {
        int statusBgRes;
        int statusTextColor = ContextCompat.getColor(this, android.R.color.white);
        switch (status) {
            case "Expired":
                statusBgRes = R.drawable.bg_status_expired;
                break;
            case "Out of Stock":
                statusBgRes = R.drawable.bg_status_out_of_stock;
                break;
            case "Low Stock":
                statusBgRes = R.drawable.bg_status_low_stock;
                break;
            case "In Stock":
            default:
                statusBgRes = R.drawable.bg_status_in_stock;
                break;
        }
        binding.tvStatus.setBackgroundResource(statusBgRes);
        binding.tvStatus.setTextColor(statusTextColor);
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
