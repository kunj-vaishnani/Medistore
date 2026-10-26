package com.medistore.app.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.medistore.app.databinding.FragmentReportsBinding;
import com.medistore.app.utils.CurrencyUtils;
import com.medistore.app.viewmodel.ReportsViewModel;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private ReportsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refreshAll();
    }

    private void observeViewModel() {
        viewModel.todaySales.observe(getViewLifecycleOwner(), val -> binding.tvTodaySales.setText(CurrencyUtils.formatCurrency(val)));
        viewModel.monthlySales.observe(getViewLifecycleOwner(), val -> binding.tvMonthlySales.setText(CurrencyUtils.formatCurrency(val)));
        viewModel.totalRevenue.observe(getViewLifecycleOwner(), val -> binding.tvTotalRevenue.setText(CurrencyUtils.formatCurrency(val)));

        viewModel.totalMedicines.observe(getViewLifecycleOwner(), val -> binding.tvTotalMedicines.setText(String.valueOf(val)));
        viewModel.totalStock.observe(getViewLifecycleOwner(), val -> binding.tvTotalStock.setText(String.valueOf(val)));
        viewModel.lowStockCount.observe(getViewLifecycleOwner(), val -> binding.tvLowStock.setText(String.valueOf(val)));
        viewModel.expiredCount.observe(getViewLifecycleOwner(), val -> binding.tvExpired.setText(String.valueOf(val)));

        viewModel.totalCustomers.observe(getViewLifecycleOwner(), val -> binding.tvTotalCustomers.setText(String.valueOf(val)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
