package com.medistore.app.ui.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.medistore.app.R;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.databinding.ItemInventoryBinding;
import com.medistore.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<Medicine> medicines = new ArrayList<>();

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines != null ? medicines : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryBinding binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new InventoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Medicine current = medicines.get(position);
        holder.bind(current);
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemInventoryBinding binding;

        public InventoryViewHolder(ItemInventoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Medicine medicine) {
            Context context = binding.getRoot().getContext();
            binding.tvName.setText(medicine.getName());
            binding.tvBatchNumber.setText("Batch: " + medicine.getBatchNumber());
            binding.tvQuantity.setText(String.format(Locale.getDefault(), "%d / Min: %d", medicine.getQuantity(), medicine.getMinimumStock()));
            binding.tvExpiry.setText("Exp: " + medicine.getExpiryDate());

            String status = getStockStatus(medicine);
            binding.tvStatus.setText(status);

            int statusBgRes;
            int statusTextColor = ContextCompat.getColor(context, android.R.color.white);
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
    }
}
