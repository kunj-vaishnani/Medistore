package com.medistore.app.ui.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.medistore.app.R;
import com.medistore.app.databinding.FragmentMoreBinding;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences(
                com.medistore.app.utils.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        boolean isCustomer = "Customer".equalsIgnoreCase(prefs.getString("user_role", "Store Administrator"));

        if (isCustomer) {
            // Hide administrative modules from customers
            binding.cardPurchase.setVisibility(View.GONE);
            binding.cardSuppliers.setVisibility(View.GONE);
            binding.cardCustomers.setVisibility(View.GONE);
            binding.cardExpiry.setVisibility(View.GONE);
            binding.cardLowStock.setVisibility(View.GONE);
            binding.cardReports.setVisibility(View.GONE);
        } else {
            binding.cardPurchase.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_purchase));
            binding.cardSuppliers.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_suppliers));
            binding.cardCustomers.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_customers));
            binding.cardExpiry.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_expiry));
            binding.cardLowStock.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_low_stock));
            binding.cardReports.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_reports));
        }

        binding.cardSettings.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_settings));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
