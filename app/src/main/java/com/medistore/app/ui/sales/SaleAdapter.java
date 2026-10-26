package com.medistore.app.ui.sales;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.medistore.app.databinding.ItemSaleBinding;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.utils.CurrencyUtils;

public class SaleAdapter extends ListAdapter<Sale, SaleAdapter.SaleViewHolder> {
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Sale sale);
    }

    public SaleAdapter(OnItemClickListener listener) {
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
            return oldItem.getInvoiceNumber().equals(newItem.getInvoiceNumber());
        }
    };

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSaleBinding binding = ItemSaleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SaleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        Sale currentSale = getItem(position);
        holder.bind(currentSale);
    }

    public Sale getSaleAt(int position) {
        return getItem(position);
    }

    class SaleViewHolder extends RecyclerView.ViewHolder {
        private final ItemSaleBinding binding;

        public SaleViewHolder(ItemSaleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }

        public void bind(Sale sale) {
            binding.tvInvoice.setText(sale.getInvoiceNumber());
            binding.tvCustomer.setText(sale.getCustomerName() + " - " + sale.getCustomerPhone());
            binding.tvDate.setText(sale.getSaleDate());
            binding.tvTotal.setText(CurrencyUtils.formatCurrency(sale.getTotal()));
            binding.tvPayment.setText(sale.getPaymentMethod());

            switch (sale.getPaymentMethod()) {
                case "Cash":
                    binding.tvPayment.setTextColor(Color.GREEN);
                    break;
                case "UPI":
                    binding.tvPayment.setTextColor(Color.BLUE);
                    break;
                case "Card":
                    binding.tvPayment.setTextColor(Color.parseColor("#FFA500"));
                    break;
                default:
                    binding.tvPayment.setTextColor(Color.BLACK);
            }
        }
    }
}
