package com.medistore.app.ui.sales;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medistore.app.databinding.FragmentSalesHistoryBinding;
import com.medistore.app.viewmodel.SalesViewModel;
import com.medistore.app.data.entities.Sale;

public class SalesHistoryFragment extends Fragment {
    private FragmentSalesHistoryBinding binding;
    private SalesViewModel viewModel;
    private SaleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSalesHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SalesViewModel.class);

        adapter = new SaleAdapter(sale -> {
            Intent intent = new Intent(getContext(), SaleDetailActivity.class);
            intent.putExtra("SALE_ID", sale.getSaleId());
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.fabAddSale.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddSaleActivity.class));
        });

        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences(
                com.medistore.app.utils.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        if ("Customer".equalsIgnoreCase(prefs.getString("user_role", "Store Administrator"))) {
            binding.fabAddSale.setVisibility(View.GONE);
        }

        viewModel.getFilteredSales().observe(getViewLifecycleOwner(), sales -> {
            adapter.submitList(sales);
            if (sales == null || sales.isEmpty()) {
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

        setupSwipeToDelete();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Sale sale = adapter.getSaleAt(position);
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Sale")
                        .setMessage("Are you sure you want to delete this sale?")
                        .setPositiveButton("Yes", (dialog, which) -> viewModel.deleteSale(sale))
                        .setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position))
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
