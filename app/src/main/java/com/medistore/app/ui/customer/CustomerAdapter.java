package com.medistore.app.ui.customer;

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
import com.medistore.app.data.entities.Customer;

public class CustomerAdapter extends ListAdapter<Customer, CustomerAdapter.CustomerViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Customer customer);
        void onDeleteClick(Customer customer);
    }

    public CustomerAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Customer> DIFF_CALLBACK = new DiffUtil.ItemCallback<Customer>() {
        @Override
        public boolean areItemsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
            return oldItem.getCustomerId() == newItem.getCustomerId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getPhone().equals(newItem.getPhone());
        }
    };

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = getItem(position);
        holder.bind(customer, listener);
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvEmail, tvAddress;
        ImageButton btnEdit, btnDelete;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Customer customer, OnItemClickListener listener) {
            tvName.setText(customer.getName());
            tvPhone.setText(customer.getPhone());
            tvEmail.setText(customer.getEmail());
            tvAddress.setText(customer.getAddress());

            btnEdit.setOnClickListener(v -> listener.onEditClick(customer));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(customer));
        }
    }
}
