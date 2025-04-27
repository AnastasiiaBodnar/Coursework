package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import models.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final Context context;
    private final List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvServiceName.setText("Послуга: " + booking.getServiceName());
        holder.tvDateTime.setText("Дата: " + booking.getDate() + " • " + booking.getTime());
        holder.tvMaster.setText("Майстер: " + booking.getMasterName());
        holder.tvStatus.setText("Статус: " + booking.getStatus());

        if ("підтверджено".equalsIgnoreCase(booking.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
        }

        holder.btnCancel.setOnClickListener(v -> {
            String bookingId = booking.getId();
            FirebaseFirestore.getInstance()
                    .collection("bookings")
                    .document(bookingId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        int pos = holder.getAdapterPosition();
                        bookingList.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, bookingList.size());
                        Toast.makeText(context, "Запис відмінено", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Не вдалося відмінити запис", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvDateTime, tvMaster, tvStatus;
        Button btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDateTime    = itemView.findViewById(R.id.tvDateTime);
            tvMaster      = itemView.findViewById(R.id.tvMaster);
            tvStatus      = itemView.findViewById(R.id.tvStatus);
            btnCancel     = itemView.findViewById(R.id.btnCancel);
        }
    }
}
