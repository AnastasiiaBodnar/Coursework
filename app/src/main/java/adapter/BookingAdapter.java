package adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;

import java.util.List;

import models.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvServiceName.setText("Послуга: " + booking.getServiceName());
        holder.tvDateTime.setText("Дата: " + booking.getDate() + " • " + booking.getTime());
        holder.tvMaster.setText("Майстер: " + booking.getMasterName());
        holder.tvStatus.setText("Статус: " + booking.getStatus());

        if (booking.getStatus().equalsIgnoreCase("підтверджено")) {
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")); // зелений
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")); // червоний
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvDateTime, tvMaster, tvStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvMaster = itemView.findViewById(R.id.tvMaster);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
