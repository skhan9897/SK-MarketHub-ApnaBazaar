package com.sk.markethub.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sk.markethub.R;
import com.sk.markethub.model.Order;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orders = new ArrayList<>();

    public void setOrders(List<Order> newOrders) {
        this.orders = newOrders != null ? newOrders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order o = orders.get(position);
        holder.tvNumber.setText("ORDER #" + o.getOrderNumber());
        holder.tvStatus.setText("Status: " + o.getOrderStatus() + " | Payment: " + o.getPaymentStatus());
        holder.tvTotal.setText("Total: ₹" + (o.getTotalAmount() != null ? o.getTotalAmount().toString() : "0"));

        if (o.getShipment() != null && o.getShipment().getTrackingNumber() != null) {
            holder.tvTracking.setVisibility(View.VISIBLE);
            holder.tvTracking.setText("Courier: " + o.getShipment().getCourierName() + " | Tracking: " + o.getShipment().getTrackingNumber());
        } else {
            holder.tvTracking.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvStatus, tvTotal, tvTracking;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvTracking = itemView.findViewById(R.id.tvOrderTracking);
        }
    }
}
