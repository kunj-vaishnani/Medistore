package com.medistore.app.ui.expiry;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.medistore.app.R;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.utils.DateUtils;

public class ExpiryAdapter extends ListAdapter<Medicine, ExpiryAdapter.ExpiryViewHolder> {

    public ExpiryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Medicine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Medicine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Medicine oldItem, @NonNull Medicine newItem) {
            return oldItem.getMedicineId() == newItem.getMedicineId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull Medicine oldItem, @NonNull Medicine newItem) {
            return oldItem.getExpiryDate().equals(newItem.getExpiryDate());
        }
    };

    @NonNull
    @Override
    public ExpiryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expiry, parent, false);
        return new ExpiryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpiryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ExpiryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBatch, tvExpiryDate, tvQty, tvStatus;
        MaterialCardView statusBadge;

        public ExpiryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvBatch = itemView.findViewById(R.id.tvBatch);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            statusBadge = itemView.findViewById(R.id.statusBadge);
        }

        public void bind(Medicine medicine) {
            tvName.setText(medicine.getName());
            tvBatch.setText("Batch: " + medicine.getBatchNumber());
            tvExpiryDate.setText("Expiry: " + medicine.getExpiryDate());
            tvQty.setText("Qty: " + medicine.getQuantity());

            if (DateUtils.isExpired(medicine.getExpiryDate())) {
                tvStatus.setText("EXPIRED");
                statusBadge.setCardBackgroundColor(Color.parseColor("#D32F2F"));
                tvStatus.setTextColor(Color.WHITE);
            } else {
                long days = DateUtils.getDaysUntilExpiry(medicine.getExpiryDate());
                tvStatus.setText("EXPIRING IN " + days + " DAYS");
                statusBadge.setCardBackgroundColor(Color.parseColor("#E65100"));
                tvStatus.setTextColor(Color.WHITE);
            }
        }
    }
}
