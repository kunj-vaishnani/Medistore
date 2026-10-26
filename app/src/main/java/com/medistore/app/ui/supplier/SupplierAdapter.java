package com.medistore.app.ui.supplier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.medistore.app.R;
import com.medistore.app.data.entities.Supplier;

public class SupplierAdapter extends ListAdapter<Supplier, SupplierAdapter.SupplierViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Supplier supplier);
        void onDeleteClick(Supplier supplier);
    }

    public SupplierAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Supplier> DIFF_CALLBACK = new DiffUtil.ItemCallback<Supplier>() {
        @Override
        public boolean areItemsTheSame(@NonNull Supplier oldItem, @NonNull Supplier newItem) {
            return oldItem.getSupplierId() == newItem.getSupplierId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull Supplier oldItem, @NonNull Supplier newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getCompany().equals(newItem.getCompany()) &&
                   oldItem.getPhone().equals(newItem.getPhone());
        }
    };

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier, parent, false);
        return new SupplierViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, int position) {
        Supplier supplier = getItem(position);
        holder.bind(supplier, listener);
    }

    static class SupplierViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCompany, tvPhone, tvEmail;
        ImageButton btnEdit, btnDelete;

        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Supplier supplier, OnItemClickListener listener) {
            tvName.setText(supplier.getName());
            tvCompany.setText(supplier.getCompany());
            tvPhone.setText(supplier.getPhone());
            tvEmail.setText(supplier.getEmail());

            btnEdit.setOnClickListener(v -> listener.onEditClick(supplier));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(supplier));
        }
    }
}
