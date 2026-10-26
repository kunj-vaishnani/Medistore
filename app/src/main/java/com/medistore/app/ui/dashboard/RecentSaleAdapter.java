package com.medistore.app.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.databinding.ItemRecentSaleBinding;
import com.medistore.app.utils.CurrencyUtils;

public class RecentSaleAdapter extends ListAdapter<Sale, RecentSaleAdapter.SaleViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Sale sale);
    }

    public RecentSaleAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Sale> DIFF_CALLBACK = new DiffUtil.ItemCallback<Sale>() {
        @Override
        public boolean areItemsTheSame(@NonNull Sale oldItem, @NonNull Sale newItem) {
            return oldItem.getSaleId() == newItem.getSaleId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Sale oldItem, @NonNull Sale newItem) {
            return oldItem.getInvoiceNumber().equals(newItem.getInvoiceNumber()) &&
                   oldItem.getTotal() == newItem.getTotal();
        }
    };

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentSaleBinding binding = ItemRecentSaleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SaleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class SaleViewHolder extends RecyclerView.ViewHolder {

        private final ItemRecentSaleBinding binding;

        public SaleViewHolder(ItemRecentSaleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getItem(position));
                }
            });
        }

        public void bind(Sale sale) {
            binding.tvInvoice.setText(sale.getInvoiceNumber());
            binding.tvCustomer.setText(sale.getCustomerName());
            binding.tvDate.setText(sale.getSaleDate());
            binding.tvAmount.setText(CurrencyUtils.formatCurrency(sale.getTotal()));
            binding.tvPayment.setText(sale.getPaymentMethod());
        }
    }
}
