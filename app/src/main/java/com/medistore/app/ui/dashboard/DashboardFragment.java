package com.medistore.app.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medistore.app.R;
import com.medistore.app.databinding.FragmentDashboardBinding;
import com.medistore.app.ui.customer.AddEditCustomerActivity;
import com.medistore.app.ui.login.LoginActivity;
import com.medistore.app.ui.medicine.AddEditMedicineActivity;
import com.medistore.app.ui.purchase.AddPurchaseActivity;
import com.medistore.app.ui.sales.AddSaleActivity;
import com.medistore.app.ui.sales.SaleDetailActivity;
import com.medistore.app.utils.Constants;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private RecentSaleAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        
        setupRecyclerView();
        setupObservers();
        setupClickListeners();
        applyRoleAccess();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refreshStats();
        
        SharedPreferences prefs = requireActivity().getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        String username = prefs.getString(Constants.KEY_USERNAME, "Admin");
        binding.tvWelcomeUser.setText("Welcome, " + username);
        applyRoleAccess();
    }

    private void applyRoleAccess() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        String role = prefs.getString("user_role", "Store Administrator");
        boolean isCustomer = "Customer".equalsIgnoreCase(role);

        if (isCustomer) {
            // Customer view: hide admin operational & financial cards
            binding.cardLowStock.setVisibility(View.GONE);
            binding.cardExpired.setVisibility(View.GONE);
            binding.cardTodaySales.setVisibility(View.GONE);
            binding.cardCustomers.setVisibility(View.GONE);

            // Hide admin quick actions
            binding.tvTitleQuickActions.setVisibility(View.GONE);
            binding.gridQuickActions.setVisibility(View.GONE);

            // Hide recent sales
            binding.layoutSalesHeader.setVisibility(View.GONE);
            binding.rvRecentSales.setVisibility(View.GONE);
            binding.tvEmptySales.setVisibility(View.GONE);

            // Show Customer welcome & catalog exploration card
            binding.cardCustomerInfo.setVisibility(View.VISIBLE);
            binding.btnBrowseCatalog.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.nav_medicines));
        } else {
            // Admin view: show full control
            binding.cardLowStock.setVisibility(View.VISIBLE);
            binding.cardExpired.setVisibility(View.VISIBLE);
            binding.cardTodaySales.setVisibility(View.VISIBLE);
            binding.cardCustomers.setVisibility(View.VISIBLE);

            binding.tvTitleQuickActions.setVisibility(View.VISIBLE);
            binding.gridQuickActions.setVisibility(View.VISIBLE);

            binding.layoutSalesHeader.setVisibility(View.VISIBLE);
            binding.cardCustomerInfo.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        adapter = new RecentSaleAdapter(sale -> {
            Intent intent = new Intent(requireContext(), SaleDetailActivity.class);
            intent.putExtra("SALE_ID", sale.getSaleId());
            startActivity(intent);
        });
        binding.rvRecentSales.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentSales.setAdapter(adapter);
        binding.rvRecentSales.setNestedScrollingEnabled(false);
    }

    private void setupObservers() {
        viewModel.getTotalMedicines().observe(getViewLifecycleOwner(), count -> 
            binding.tvTotalMedicines.setText(String.valueOf(count != null ? count : 0)));
            
        viewModel.getTotalStock().observe(getViewLifecycleOwner(), count -> 
            binding.tvTotalStock.setText(String.valueOf(count != null ? count : 0)));
            
        viewModel.getLowStockCount().observe(getViewLifecycleOwner(), count -> 
            binding.tvLowStock.setText(String.valueOf(count != null ? count : 0)));
            
        viewModel.getExpiredCount().observe(getViewLifecycleOwner(), count -> 
            binding.tvExpired.setText(String.valueOf(count != null ? count : 0)));
            
        viewModel.getTodaySales().observe(getViewLifecycleOwner(), salesAmount -> 
            binding.tvTodaySales.setText(salesAmount != null ? salesAmount : "₹0"));
            
        viewModel.getTotalCustomers().observe(getViewLifecycleOwner(), count -> 
            binding.tvTotalCustomers.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getRecentSales().observe(getViewLifecycleOwner(), sales -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(
                    Constants.PREF_NAME, Context.MODE_PRIVATE);
            boolean isCustomer = "Customer".equalsIgnoreCase(prefs.getString("user_role", "Store Administrator"));
            if (isCustomer) {
                binding.rvRecentSales.setVisibility(View.GONE);
                binding.tvEmptySales.setVisibility(View.GONE);
                return;
            }
            if (sales != null && !sales.isEmpty()) {
                adapter.submitList(sales);
                binding.rvRecentSales.setVisibility(View.VISIBLE);
                binding.tvEmptySales.setVisibility(View.GONE);
            } else {
                binding.rvRecentSales.setVisibility(View.GONE);
                binding.tvEmptySales.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupClickListeners() {
        // Quick Actions
        binding.cvAddMedicine.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddEditMedicineActivity.class)));
        
        binding.cvNewSale.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddSaleActivity.class)));

        binding.cvAddPurchase.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddPurchaseActivity.class)));

        binding.cvAddCustomer.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), AddEditCustomerActivity.class)));

        // View All Recent Sales
        binding.tvViewAllSales.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.nav_sales));

        // Notifications Button
        binding.btnNotification.setOnClickListener(v -> showNotificationDialog());

        // Profile Button
        binding.btnProfile.setOnClickListener(v -> showProfileDialog());
    }

    private void showNotificationDialog() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        String role = prefs.getString("user_role", "Store Administrator");
        boolean isCustomer = "Customer".equalsIgnoreCase(role);

        if (isCustomer) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Notifications")
                    .setMessage("Welcome to MediStore!\nBrowse our wide collection of genuine medicines and check availability anytime.")
                    .setPositiveButton("Close", null)
                    .show();
            return;
        }

        Integer lowCount = viewModel.getLowStockCount().getValue();
        Integer expCount = viewModel.getExpiredCount().getValue();
        int low = lowCount != null ? lowCount : 0;
        int exp = expCount != null ? expCount : 0;

        StringBuilder sb = new StringBuilder();
        if (low == 0 && exp == 0) {
            sb.append("✅ All stock levels are healthy!\nNo expired or low stock medicines detected.");
        } else {
            if (low > 0) {
                sb.append("⚠️ Low Stock Alert: ").append(low).append(" medicine(s) running low.\n\n");
            }
            if (exp > 0) {
                sb.append("🚨 Expiry Alert: ").append(exp).append(" medicine(s) already expired.\n\n");
            }
            sb.append("Tap below to inspect and manage items.");
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Notifications & Alerts")
                .setMessage(sb.toString())
                .setPositiveButton("Close", null);

        if (low > 0) {
            builder.setNeutralButton("View Low Stock", (dialog, which) -> 
                Navigation.findNavController(requireView()).navigate(R.id.nav_low_stock));
        } else if (exp > 0) {
            builder.setNeutralButton("View Expired", (dialog, which) -> 
                Navigation.findNavController(requireView()).navigate(R.id.nav_expiry));
        }
        builder.show();
    }

    private void showProfileDialog() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(
                Constants.PREF_NAME, Context.MODE_PRIVATE);
        String username = prefs.getString(Constants.KEY_USERNAME, "admin");
        String role = prefs.getString("user_role", "Store Administrator");

        String info = "👤 Logged in as: " + username +
                "\n🏷️ Role: " + role +
                "\n🏪 System: MediStore Medical Store Management";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("User Profile")
                .setMessage(info)
                .setPositiveButton("Settings", (dialog, which) -> 
                    Navigation.findNavController(requireView()).navigate(R.id.nav_settings))
                .setNegativeButton("Logout", (dialog, which) -> {
                    prefs.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, false).apply();
                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
