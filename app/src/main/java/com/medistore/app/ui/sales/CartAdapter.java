package com.medistore.app.ui.sales;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.medistore.app.databinding.ItemCartBinding;
import com.medistore.app.data.model.CartItem;
import com.medistore.app.utils.CurrencyUtils;

public class CartAdapter extends ListAdapter<CartItem, CartAdapter.CartViewHolder> {
    private final CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(int position, int newQty);
        void onRemoveClicked(int position);
    }

    public CartAdapter(CartItemListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CartItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getMedicineId() == newItem.getMedicineId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getQuantity() == newItem.getQuantity() && oldItem.getTotal() == newItem.getTotal();
        }
    };

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;

        public CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnIncrease.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CartItem item = getItem(pos);
                    listener.onQuantityChanged(pos, item.getQuantity() + 1);
                }
            });

            binding.btnDecrease.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CartItem item = getItem(pos);
                    if (item.getQuantity() > 1) {
                        listener.onQuantityChanged(pos, item.getQuantity() - 1);
                    }
                }
            });

            binding.btnRemove.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onRemoveClicked(pos);
                }
            });
        }

        public void bind(CartItem item) {
            binding.tvMedicineName.setText(item.getMedicineName());
            binding.tvPrice.setText(CurrencyUtils.formatCurrency(item.getPrice()));
            binding.etQuantity.setText(String.valueOf(item.getQuantity()));
            binding.tvTotal.setText(CurrencyUtils.formatCurrency(item.getTotal()));
        }
    }
}
