package com.medistore.app.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.databinding.FragmentCustomerBinding;
import com.medistore.app.viewmodel.CustomerViewModel;

public class CustomerFragment extends Fragment {

    private FragmentCustomerBinding binding;
    private CustomerViewModel viewModel;
    private CustomerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences(
                com.medistore.app.utils.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE);
        if ("Customer".equalsIgnoreCase(prefs.getString("user_role", "Store Administrator"))) {
            binding.fabAdd.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        adapter = new CustomerAdapter(new CustomerAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Customer customer) {
                Intent intent = new Intent(requireContext(), AddEditCustomerActivity.class);
                intent.putExtra("ACTION", "EDIT");
                intent.putExtra("CUSTOMER_ID", customer.getCustomerId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Customer customer) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Customer")
                        .setMessage("Are you sure you want to delete this customer?")
                        .setPositiveButton("Yes", (dialog, which) -> viewModel.delete(customer))
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditCustomerActivity.class);
            intent.putExtra("ACTION", "ADD");
            startActivity(intent);
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void observeViewModel() {
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), customers -> {
            adapter.submitList(customers);
            if (customers == null || customers.isEmpty()) {
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
