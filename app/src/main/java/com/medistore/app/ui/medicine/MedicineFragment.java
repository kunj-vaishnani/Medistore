package com.medistore.app.ui.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.databinding.FragmentMedicineBinding;
import com.medistore.app.viewmodel.MedicineViewModel;

public class MedicineFragment extends Fragment implements MedicineAdapter.OnItemClickListener {
    private MedicineViewModel medicineViewModel;
    private FragmentMedicineBinding binding;
    private MedicineAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);

        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences(
                com.medistore.app.utils.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        boolean isCustomer = "Customer".equalsIgnoreCase(prefs.getString("user_role", "Store Administrator"));

        adapter = new MedicineAdapter(this);
        adapter.setCustomer(isCustomer);
        binding.rvMedicines.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMedicines.setAdapter(adapter);

        if (isCustomer) {
            binding.fabAdd.setVisibility(View.GONE);
        }

        medicineViewModel.getFilteredMedicines().observe(getViewLifecycleOwner(), medicines -> {
            adapter.setMedicines(medicines);
            if (medicines == null || medicines.isEmpty()) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvMedicines.setVisibility(View.GONE);
            } else {
                binding.tvEmpty.setVisibility(View.GONE);
                binding.rvMedicines.setVisibility(View.VISIBLE);
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                medicineViewModel.setSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                medicineViewModel.setSearchQuery(newText);
                return false;
            }
        });

        binding.chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                medicineViewModel.filterByCategory("All");
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    medicineViewModel.filterByCategory(chip.getText().toString());
                }
            }
        });

        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditMedicineActivity.class);
            intent.putExtra("ACTION", "ADD");
            startActivity(intent);
        });
    }

    @Override
    public void onEditClick(Medicine medicine) {
        Intent intent = new Intent(getContext(), AddEditMedicineActivity.class);
        intent.putExtra("ACTION", "EDIT");
        intent.putExtra("MEDICINE_ID", medicine.getMedicineId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Medicine medicine) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Medicine")
                .setMessage("Are you sure you want to delete " + medicine.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    medicineViewModel.delete(medicine);
                    Toast.makeText(getContext(), "Medicine deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onItemClick(Medicine medicine) {
        Intent intent = new Intent(getContext(), MedicineDetailActivity.class);
        intent.putExtra("MEDICINE_ID", medicine.getMedicineId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
