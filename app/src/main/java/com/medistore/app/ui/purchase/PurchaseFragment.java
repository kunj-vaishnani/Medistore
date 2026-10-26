package com.medistore.app.ui.purchase;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medistore.app.data.entities.Purchase;
import com.medistore.app.databinding.FragmentPurchaseBinding;
import com.medistore.app.viewmodel.PurchaseViewModel;

public class PurchaseFragment extends Fragment {
    private FragmentPurchaseBinding binding;
    private PurchaseViewModel viewModel;
    private PurchaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPurchaseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PurchaseViewModel.class);

        adapter = new PurchaseAdapter(new PurchaseAdapter.PurchaseListener() {
            @Override
            public void onEditClick(Purchase purchase) {
                Intent intent = new Intent(getContext(), AddPurchaseActivity.class);
                intent.putExtra("PURCHASE_ID", purchase.getPurchaseId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Purchase purchase) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Purchase")
                        .setMessage("Are you sure you want to delete this purchase?")
                        .setPositiveButton("Yes", (dialog, which) -> viewModel.delete(purchase))
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.fabAddPurchase.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddPurchaseActivity.class));
        });

        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences(
                com.medistore.app.utils.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        if ("Customer".equalsIgnoreCase(prefs.getString("user_role", "Store Administrator"))) {
            binding.fabAddPurchase.setVisibility(View.GONE);
        }

        viewModel.getFilteredPurchases().observe(getViewLifecycleOwner(), purchases -> {
            adapter.submitList(purchases);
            if (purchases == null || purchases.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });

        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
