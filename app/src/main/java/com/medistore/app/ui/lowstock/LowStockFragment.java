package com.medistore.app.ui.lowstock;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.medistore.app.R;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.databinding.FragmentLowStockBinding;
import com.medistore.app.viewmodel.LowStockViewModel;
import com.medistore.app.ui.purchase.AddPurchaseActivity; // Assuming exists

public class LowStockFragment extends Fragment {

    private FragmentLowStockBinding binding;
    private LowStockViewModel viewModel;
    private LowStockAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLowStockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LowStockViewModel.class);
        setupRecyclerView();
        setupFilters();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new LowStockAdapter(medicine -> {
            Intent intent = new Intent(requireContext(), AddPurchaseActivity.class); // Adjust as needed
            intent.putExtra("MEDICINE_ID", medicine.getMedicineId());
            startActivity(intent);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll) {
                viewModel.setFilter("ALL");
            } else if (id == R.id.chipLow) {
                viewModel.setFilter("LOW");
            } else if (id == R.id.chipOut) {
                viewModel.setFilter("OUT");
            }
        });
    }

    private void observeViewModel() {
        viewModel.getDisplayedMedicines().observe(getViewLifecycleOwner(), medicines -> {
            adapter.submitList(medicines);
            
            int count = medicines == null ? 0 : medicines.size();
            binding.tvSummary.setText(count + " medicines are low/out of stock");
            
            if (medicines == null || medicines.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                binding.tvSummary.setText("All medicines are well-stocked!");
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
