package com.medistore.app.ui.sales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medistore.app.R;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.entities.SaleItem;
import com.medistore.app.data.repository.SaleRepository;
import com.medistore.app.databinding.ActivitySaleDetailBinding;
import com.medistore.app.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class SaleDetailActivity extends AppCompatActivity {
    private ActivitySaleDetailBinding binding;
    private SaleRepository saleRepository;
    private SaleItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySaleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        saleRepository = new SaleRepository(getApplication());
        setupToolbar();

        itemsAdapter = new SaleItemsAdapter();
        binding.rvItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvItems.setAdapter(itemsAdapter);

        int saleId = getIntent().getIntExtra("SALE_ID", -1);
        if (saleId != -1) {
            loadSaleDetails(saleId);
        }

        binding.btnShare.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                .setTitle("Print & Export")
                .setMessage("Invoice receipt generated successfully!\nPrinter / PDF export is simulated for college viva demonstration.")
                .setPositiveButton("OK", null)
                .show();
        });
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Invoice Details");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadSaleDetails(int saleId) {
        saleRepository.getSaleById(saleId, sale -> {
            if (sale != null) {
                runOnUiThread(() -> {
                    binding.tvInvoiceNum.setText("Invoice: " + sale.invoiceNumber);
                    binding.tvDate.setText("Date: " + sale.saleDate);
                    binding.tvCustomerName.setText("Customer: " + (sale.customerName != null ? sale.customerName : "Walk-in"));
                    binding.tvCustomerPhone.setText("Phone: " + (sale.customerPhone != null && !sale.customerPhone.isEmpty() ? sale.customerPhone : "N/A"));
                    binding.tvSubtotal.setText(CurrencyUtils.formatCurrency(sale.subtotal));
                    binding.tvDiscount.setText(String.format("%.1f%%", sale.discount));
                    binding.tvGst.setText(CurrencyUtils.formatCurrency(sale.gst));
                    binding.tvTotal.setText(CurrencyUtils.formatCurrency(sale.total));
                    binding.tvPaymentChip.setText("Paid via: " + (sale.paymentMethod != null ? sale.paymentMethod : "Cash"));
                });
            }
        });

        saleRepository.getItemsBySaleId(saleId).observe(this, items -> {
            if (items != null) {
                itemsAdapter.setItems(items);
            }
        });
    }

    private static class SaleItemsAdapter extends RecyclerView.Adapter<SaleItemsAdapter.ViewHolder> {
        private final List<SaleItem> items = new ArrayList<>();

        public void setItems(List<SaleItem> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_detail, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SaleItem item = items.get(position);
            holder.tvMedicineName.setText(item.medicineName != null ? item.medicineName : "Item #" + item.medicineId);
            holder.tvQtyPrice.setText(item.quantity + " x " + CurrencyUtils.formatCurrency(item.price));
            holder.tvItemTotal.setText(CurrencyUtils.formatCurrency(item.total));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMedicineName, tvQtyPrice, tvItemTotal;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
                tvQtyPrice = itemView.findViewById(R.id.tvQtyPrice);
                tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            }
        }
    }
}
