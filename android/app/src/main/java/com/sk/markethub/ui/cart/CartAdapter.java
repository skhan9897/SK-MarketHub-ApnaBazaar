package com.sk.markethub.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sk.markethub.R;
import com.sk.markethub.model.CartItemDetail;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnCartItemActionListener {
        void onRemoveItem(CartItemDetail item);
    }

    private List<CartItemDetail> items = new ArrayList<>();
    private final OnCartItemActionListener listener;

    public CartAdapter(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartItemDetail> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemDetail item = items.get(position);
        holder.tvName.setText(item.getProductName());
        holder.tvSku.setText("SKU: " + item.getSku());
        holder.tvUnitPrice.setText("₹" + (item.getUnitPrice() != null ? item.getUnitPrice().toString() : "0"));
        holder.tvQuantity.setText("Qty: " + item.getQuantity());

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveItem(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSku, tvUnitPrice, tvQuantity;
        ImageButton btnRemove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvSku = itemView.findViewById(R.id.tvCartSku);
            tvUnitPrice = itemView.findViewById(R.id.tvCartUnitPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemoveCartItem);
        }
    }
}
