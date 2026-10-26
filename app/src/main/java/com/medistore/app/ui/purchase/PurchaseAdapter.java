package com.medistore.app.ui.purchase;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.medistore.app.databinding.ItemPurchaseBinding;
import com.medistore.app.data.entities.Purchase;
import com.medistore.app.utils.CurrencyUtils;

public class PurchaseAdapter extends ListAdapter<Purchase, PurchaseAdapter.PurchaseViewHolder> {
    private final PurchaseListener listener;

    public interface PurchaseListener {
        void onEditClick(Purchase purchase);
        void onDeleteClick(Purchase purchase);
    }

    public PurchaseAdapter(PurchaseListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Purchase> DIFF_CALLBACK = new DiffUtil.ItemCallback<Purchase>() {
        @Override
        public boolean areItemsTheSame(@NonNull Purchase oldItem, @NonNull Purchase newItem) {
            return oldItem.getPurchaseId() == newItem.getPurchaseId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Purchase oldItem, @NonNull Purchase newItem) {
            return oldItem.getMedicineName().equals(newItem.getMedicineName());
        }
    };

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPurchaseBinding binding = ItemPurchaseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PurchaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class PurchaseViewHolder extends RecyclerView.ViewHolder {
        private final ItemPurchaseBinding binding;

        public PurchaseViewHolder(ItemPurchaseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnEdit.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getItem(pos));
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(pos));
                }
            });
        }

        public void bind(Purchase purchase) {
            binding.tvMedicine.setText(purchase.getMedicineName());
            binding.tvSupplier.setText(purchase.getSupplierName());
            binding.tvBatch.setText("Batch: " + purchase.getBatchNumber());
            binding.tvQty.setText("Qty: " + purchase.getQuantity());
            binding.tvPrice.setText("Price: " + CurrencyUtils.formatCurrency(purchase.getPurchasePrice()));
            binding.tvDate.setText("Purchase Date: " + purchase.getPurchaseDate());
            binding.tvExpiry.setText("Expiry: " + purchase.getExpiryDate());
        }
    }
}
