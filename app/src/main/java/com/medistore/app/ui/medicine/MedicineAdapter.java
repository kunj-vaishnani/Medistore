package com.medistore.app.ui.medicine;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.medistore.app.R;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.databinding.ItemMedicineBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicines = new ArrayList<>();
    private final OnItemClickListener listener;
    private boolean isCustomer = false;

    public interface OnItemClickListener {
        void onEditClick(Medicine medicine);
        void onDeleteClick(Medicine medicine);
        void onItemClick(Medicine medicine);
    }

    public MedicineAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setCustomer(boolean customer) {
        this.isCustomer = customer;
        notifyDataSetChanged();
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines != null ? medicines : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMedicineBinding binding = ItemMedicineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MedicineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine current = medicines.get(position);
        holder.bind(current);
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    class MedicineViewHolder extends RecyclerView.ViewHolder {
        private final ItemMedicineBinding binding;

        public MedicineViewHolder(ItemMedicineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(medicines.get(position));
                }
            });

            binding.btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(medicines.get(position));
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(medicines.get(position));
                }
            });
        }

        public void bind(Medicine medicine) {
            Context context = binding.getRoot().getContext();
            binding.tvName.setText(medicine.getName());
            binding.tvCategory.setText(medicine.getCategory());
            binding.tvManufacturer.setText(medicine.getManufacturer());
            binding.tvQuantity.setText("Qty: " + medicine.getQuantity());
            binding.tvPrice.setText(String.format(Locale.getDefault(), "₹%.2f", medicine.getSellingPrice()));
            binding.tvExpiry.setText("Exp: " + medicine.getExpiryDate());

            String status = getStockStatus(medicine);
            binding.tvStatus.setText(status);

            int statusBgRes;
            int statusTextColor;
            switch (status) {
                case "Expired":
                    statusBgRes = R.drawable.bg_status_expired;
                    statusTextColor = ContextCompat.getColor(context, android.R.color.white);
                    break;
                case "Out of Stock":
                    statusBgRes = R.drawable.bg_status_out_of_stock;
                    statusTextColor = ContextCompat.getColor(context, android.R.color.white);
                    break;
                case "Low Stock":
                    statusBgRes = R.drawable.bg_status_low_stock;
                    statusTextColor = ContextCompat.getColor(context, android.R.color.white);
                    break;
                case "In Stock":
                default:
                    statusBgRes = R.drawable.bg_status_in_stock;
                    statusTextColor = ContextCompat.getColor(context, android.R.color.white);
                    break;
            }
            binding.tvStatus.setBackgroundResource(statusBgRes);
            binding.tvStatus.setTextColor(statusTextColor);

            if (isCustomer) {
                binding.btnEdit.setVisibility(View.GONE);
                binding.btnDelete.setVisibility(View.GONE);
            } else {
                binding.btnEdit.setVisibility(View.VISIBLE);
                binding.btnDelete.setVisibility(View.VISIBLE);
            }
        }

        private String getStockStatus(Medicine medicine) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date expiryDate = sdf.parse(medicine.getExpiryDate());
                if (expiryDate != null && expiryDate.before(new Date())) {
                    return "Expired";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (medicine.getQuantity() <= 0) {
                return "Out of Stock";
            } else if (medicine.getQuantity() <= medicine.getMinimumStock()) {
                return "Low Stock";
            } else {
                return "In Stock";
            }
        }
    }
}
