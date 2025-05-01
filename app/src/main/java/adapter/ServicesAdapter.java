package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;

import java.util.List;

import admin.ChangeServicesActivity;
import models.Service;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {

    private final List<Service> serviceList;
    private final ChangeServicesActivity activity;

    public ServicesAdapter(List<Service> serviceList, ChangeServicesActivity activity) {
        this.serviceList = serviceList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_admin, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.name.setText(service.getName());
        holder.price.setText(service.getPrice());
        holder.description.setText(service.getDescription());

        holder.btnEdit.setOnClickListener(v -> activity.showServiceDialog(service));
        holder.btnDelete.setOnClickListener(v -> {
            activity.deleteService(service.getId());
            Toast.makeText(v.getContext(), "Послугу видалено", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description;
        Button btnEdit, btnDelete;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.service_name);
            price = itemView.findViewById(R.id.service_price);
            description = itemView.findViewById(R.id.service_description);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
