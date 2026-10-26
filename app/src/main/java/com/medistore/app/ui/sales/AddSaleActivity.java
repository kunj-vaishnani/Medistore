package com.medistore.app.ui.sales;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medistore.app.data.entities.Customer;
import com.medistore.app.data.entities.Medicine;
import com.medistore.app.data.entities.Sale;
import com.medistore.app.data.entities.SaleItem;
import com.medistore.app.data.model.CartItem;
import com.medistore.app.databinding.ActivityAddSaleBinding;
import com.medistore.app.utils.CurrencyUtils;
import com.medistore.app.utils.DateUtils;
import com.medistore.app.viewmodel.SalesViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddSaleActivity extends AppCompatActivity {
    private ActivityAddSaleBinding binding;
    private SalesViewModel viewModel;
    private CartAdapter cartAdapter;
    private Medicine selectedMedicine;
    private List<Medicine> medicineList = new ArrayList<>();
    private List<Customer> customerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SalesViewModel.class);

        setupToolbar();
        setupCartRecyclerView();
        setupDefaultCustomer();
        setupObservers();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Sale (POS Billing)");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDefaultCustomer() {
        // Fast default for pharmacy checkout
        binding.etCustomerName.setText("Walk-in Customer");
        binding.etCustomerPhone.setText("");
    }

    private void setupCartRecyclerView() {
        cartAdapter = new CartAdapter(new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(int position, int newQty) {
                viewModel.updateCartQty(position, newQty);
            }

            @Override
            public void onRemoveClicked(int position) {
                viewModel.removeFromCart(position);
            }
        });
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(cartAdapter);
        binding.rvCart.setNestedScrollingEnabled(false);
    }

    private void setupObservers() {
        viewModel.getCartItems().observe(this, items -> {
            cartAdapter.submitList(new ArrayList<>(items));
            int count = (items != null) ? items.size() : 0;
            binding.tvCartCountBadge.setText(count + " items");
            binding.tvEmptyCart.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
            binding.rvCart.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            updateSummary();
        });

        viewModel.getAllMedicines().observe(this, medicines -> {
            if (medicines != null) {
                medicineList = medicines;
                ArrayAdapter<Medicine> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, medicines);
                binding.acMedicine.setAdapter(adapter);
                binding.acMedicine.setThreshold(1);
            }
        });

        viewModel.getAllCustomers().observe(this, customers -> {
            if (customers != null) {
                customerList = customers;
                ArrayAdapter<Customer> customerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, customers);
                binding.etCustomerName.setAdapter(customerAdapter);
                binding.etCustomerName.setThreshold(1);
                binding.etCustomerName.setOnItemClickListener((parent, view, position, id) -> {
                    Customer selectedCustomer = customerAdapter.getItem(position);
                    if (selectedCustomer != null) {
                        binding.etCustomerName.setText(selectedCustomer.getName());
                        if (selectedCustomer.getPhone() != null) {
                            binding.etCustomerPhone.setText(selectedCustomer.getPhone());
                        }
                    }
                });
            }
        });
    }

    private void setupListeners() {
        // Quick Walk-in button
        binding.btnWalkinCustomer.setOnClickListener(v -> setupDefaultCustomer());

        // When medicine dropdown clicked/selected
        binding.acMedicine.setOnClickListener(v -> binding.acMedicine.showDropDown());
        binding.acMedicine.setOnItemClickListener((parent, view, position, id) -> {
            selectedMedicine = (Medicine) parent.getItemAtPosition(position);
            if (selectedMedicine != null) {
                displaySelectedMedicine(selectedMedicine);
            }
        });

        // Quantity +/- Stepper
        binding.btnQtyMinus.setOnClickListener(v -> {
            int current = getEnteredQty();
            if (current > 1) {
                binding.etMedicineQty.setText(String.valueOf(current - 1));
            }
        });

        binding.btnQtyPlus.setOnClickListener(v -> {
            int current = getEnteredQty();
            if (selectedMedicine != null && current >= selectedMedicine.getQuantity()) {
                Toast.makeText(this, "Maximum available stock is " + selectedMedicine.getQuantity(), Toast.LENGTH_SHORT).show();
                return;
            }
            binding.etMedicineQty.setText(String.valueOf(current + 1));
        });

        // Add to Bill button
        binding.btnAddCart.setOnClickListener(v -> {
            if (selectedMedicine == null) {
                Toast.makeText(this, "Please select a medicine first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedMedicine.getQuantity() <= 0) {
                Toast.makeText(this, "Selected medicine is out of stock", Toast.LENGTH_SHORT).show();
                return;
            }
            int qty = getEnteredQty();
            if (qty <= 0) {
                Toast.makeText(this, "Quantity must be at least 1", Toast.LENGTH_SHORT).show();
                return;
            }
            if (qty > selectedMedicine.getQuantity()) {
                Toast.makeText(this, "Quantity exceeds available stock (" + selectedMedicine.getQuantity() + ")", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.addToCart(selectedMedicine, qty);
            Toast.makeText(this, "Added " + selectedMedicine.getName() + " (x" + qty + ") to bill", Toast.LENGTH_SHORT).show();

            // Reset selector
            binding.acMedicine.setText("");
            binding.layoutSelectedMedicineInfo.setVisibility(View.GONE);
            binding.etMedicineQty.setText("1");
            selectedMedicine = null;
        });

        // Real-time discount recalculation
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSummary();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Complete Sale button
        binding.btnGenerateBill.setOnClickListener(v -> generateBill());
    }

    private void displaySelectedMedicine(Medicine medicine) {
        binding.layoutSelectedMedicineInfo.setVisibility(View.VISIBLE);
        binding.tvSelectedMedName.setText(medicine.getName());
        binding.tvSelectedMedPrice.setText(CurrencyUtils.formatCurrency(medicine.getSellingPrice()) + " / unit");
        binding.tvMedicineInfo.setText("Category: " + medicine.getCategory() + " • Stock: " + medicine.getQuantity() + " available");
        binding.etMedicineQty.setText("1");
    }

    private int getEnteredQty() {
        String str = binding.etMedicineQty.getText().toString().trim();
        if (str.isEmpty()) return 1;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void updateSummary() {
        double subtotal = viewModel.calculateSubtotal();
        String discountStr = binding.etDiscount.getText().toString().trim();
        double discount = 0;
        try {
            if (!discountStr.isEmpty()) {
                discount = Double.parseDouble(discountStr);
            }
        } catch (NumberFormatException ignored) {}

        double gst = 12.0;
        double total = viewModel.calculateTotal(discount, gst);

        binding.tvSubtotal.setText(CurrencyUtils.formatCurrency(subtotal));
        binding.tvGst.setText("12%");
        binding.tvTotalAmount.setText(CurrencyUtils.formatCurrency(total));
    }

    private void generateBill() {
        String customerName = binding.etCustomerName.getText().toString().trim();
        if (customerName.isEmpty()) {
            customerName = "Walk-in Customer";
        }

        List<CartItem> cartItems = viewModel.getCartItems().getValue();
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cannot generate bill: Cart is empty. Add at least one medicine.", Toast.LENGTH_SHORT).show();
            return;
        }

        String invoiceNumber = "INV-" + (System.currentTimeMillis() % 1000000);
        int checkedId = binding.rgPayment.getCheckedRadioButtonId();
        String paymentMethod = "Cash";
        if (checkedId == binding.rbUpi.getId()) {
            paymentMethod = "UPI";
        } else if (checkedId == binding.rbCard.getId()) {
            paymentMethod = "Card";
        }

        double subtotal = viewModel.calculateSubtotal();
        String discountStr = binding.etDiscount.getText().toString().trim();
        double discount = 0;
        try {
            if (!discountStr.isEmpty()) discount = Double.parseDouble(discountStr);
        } catch (NumberFormatException ignored) {}

        double gst = 12.0;
        double total = viewModel.calculateTotal(discount, gst);

        Sale sale = new Sale();
        sale.setInvoiceNumber(invoiceNumber);
        sale.setCustomerId(0);
        sale.setCustomerName(customerName);
        sale.setCustomerPhone(binding.etCustomerPhone.getText().toString().trim());
        sale.setSaleDate(DateUtils.getCurrentDateTime());
        sale.setSubtotal(subtotal);
        sale.setDiscount(discount);
        sale.setGst(gst);
        sale.setTotal(total);
        sale.setPaymentMethod(paymentMethod);

        List<SaleItem> saleItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            SaleItem si = new SaleItem();
            si.setMedicineId(ci.getMedicineId());
            si.setMedicineName(ci.getMedicineName());
            si.setQuantity(ci.getQuantity());
            si.setPrice(ci.getPrice());
            si.setTotal(ci.getTotal());
            saleItems.add(si);
        }

        viewModel.insertSaleWithItems(sale, saleItems);

        final String finalCustomer = customerName;
        final String finalInvoice = invoiceNumber;
        final double finalTotal = total;

        new MaterialAlertDialogBuilder(this)
                .setTitle("✅ Bill Generated Successfully")
                .setMessage("Invoice #: " + finalInvoice + "\nCustomer: " + finalCustomer + "\nPayment: " + paymentMethod + "\nGrand Total: " + CurrencyUtils.formatCurrency(finalTotal))
                .setPositiveButton("Done", (dialog, which) -> finish())
                .setNeutralButton("New Sale", (dialog, which) -> {
                    viewModel.clearCart();
                    setupDefaultCustomer();
                    binding.etDiscount.setText("0");
                    binding.rbCash.setChecked(true);
                })
                .setCancelable(false)
                .show();
    }
}
