package com.medistore.app.ui.expiry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.ChipGroup;
import com.medistore.app.R;
import com.medistore.app.databinding.FragmentExpiryBinding;
import com.medistore.app.viewmodel.ExpiryViewModel;

public class ExpiryFragment extends Fragment {

    private FragmentExpiryBinding binding;
    private ExpiryViewModel viewModel;
    private ExpiryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExpiryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ExpiryViewModel.class);
        setupRecyclerView();
        setupFilters();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new ExpiryAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll) {
                viewModel.setFilter("ALL");
            } else if (id == R.id.chipExpired) {
                viewModel.setFilter("EXPIRED");
            } else if (id == R.id.chip7Days) {
                viewModel.setFilter("SOON_7");
            } else if (id == R.id.chip30Days) {
                viewModel.setFilter("SOON_30");
            }
        });
    }

    private void observeViewModel() {
        viewModel.getExpiredMedicines().observe(getViewLifecycleOwner(), expired -> {
            if (expired != null && !expired.isEmpty()) {
                binding.cardWarning.setVisibility(View.VISIBLE);
                binding.tvWarningText.setText(expired.size() + " medicines have expired!");
            } else {
                binding.cardWarning.setVisibility(View.GONE);
            }
        });

        viewModel.getDisplayedMedicines().observe(getViewLifecycleOwner(), medicines -> {
            adapter.submitList(medicines);
            if (medicines == null || medicines.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
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
