package com.medistore.app.ui.lowstock;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.medistore.app.R;
import com.medistore.app.data.entities.Medicine;

public class LowStockAdapter extends ListAdapter<Medicine, LowStockAdapter.LowStockViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRestockClick(Medicine medicine);
    }

    public LowStockAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Medicine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Medicine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Medicine oldItem, @NonNull Medicine newItem) {
            return oldItem.getMedicineId() == newItem.getMedicineId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull Medicine oldItem, @NonNull Medicine newItem) {
            return oldItem.getQuantity() == newItem.getQuantity();
        }
    };

    @NonNull
    @Override
    public LowStockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_low_stock, parent, false);
        return new LowStockViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LowStockViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class LowStockViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStock, tvStatus;
        MaterialCardView statusBadge;
        Button btnRestock;

        public LowStockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            btnRestock = itemView.findViewById(R.id.btnRestock);
        }

        public void bind(Medicine medicine, OnItemClickListener listener) {
            tvName.setText(medicine.getName());
            tvStock.setText("Current: " + medicine.getQuantity() + " / Minimum: " + medicine.getMinimumStock());

            if (medicine.getQuantity() <= 0) {
                tvStatus.setText("OUT OF STOCK");
                statusBadge.setCardBackgroundColor(Color.parseColor("#D32F2F"));
                tvStatus.setTextColor(Color.WHITE);
            } else {
                tvStatus.setText("LOW STOCK");
                statusBadge.setCardBackgroundColor(Color.parseColor("#E65100"));
                tvStatus.setTextColor(Color.WHITE);
            }

            btnRestock.setOnClickListener(v -> listener.onRestockClick(medicine));
        }
    }
}
