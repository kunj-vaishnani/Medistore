package com.medistore.app.ui.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.databinding.FragmentInventoryBinding;
import com.medistore.app.viewmodel.InventoryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InventoryFragment extends Fragment {
    private InventoryViewModel inventoryViewModel;
    private FragmentInventoryBinding binding;
    private InventoryAdapter adapter;
    private String currentFilter = "All";
    private List<Medicine> allCurrentMedicines = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inventoryViewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        adapter = new InventoryAdapter();
        binding.rvInventory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvInventory.setAdapter(adapter);

        inventoryViewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            allCurrentMedicines = medicines;
            applyFilter();
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inventoryViewModel.setSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                inventoryViewModel.setSearchQuery(newText);
                return false;
            }
        });

        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = "All";
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    currentFilter = chip.getText().toString();
                }
            }
            applyFilter();
        });
    }

    private void applyFilter() {
        if (allCurrentMedicines == null) return;

        List<Medicine> filtered = new ArrayList<>();
        if ("All".equals(currentFilter)) {
            filtered.addAll(allCurrentMedicines);
        } else {
            for (Medicine m : allCurrentMedicines) {
                String status = getStockStatus(m);
                if (status.equals(currentFilter)) {
                    filtered.add(m);
                }
            }
        }

        adapter.setMedicines(filtered);
        
        if (filtered.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvInventory.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvInventory.setVisibility(View.VISIBLE);
        }
    }

    private String getStockStatus(Medicine medicine) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date expiryDate = sdf.parse(medicine.getExpiryDate());
            if (expiryDate != null && expiryDate.before(new Date())) {
                return "Expired";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (medicine.getQuantity() <= 0) {
            return "Out of Stock";
        } else if (medicine.getQuantity() <= medicine.getMinimumStock()) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
