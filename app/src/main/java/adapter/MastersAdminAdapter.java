package adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coursework.R;

import java.util.List;

import admin.ChangeMastersActivity;
import models.Master;

public class MastersAdminAdapter extends RecyclerView.Adapter<MastersAdminAdapter.MasterViewHolder> {

    private final List<Master> masterList;
    private final ChangeMastersActivity activity;

    public MastersAdminAdapter(List<Master> masterList, ChangeMastersActivity activity) {
        this.masterList = masterList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MasterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master_admin, parent, false);
        return new MasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterViewHolder holder, int position) {
        Master master = masterList.get(position);
        holder.name.setText(master.getName());
        holder.category.setText(master.getCategory());
        Glide.with(holder.itemView.getContext()).load(master.getPhotoURL()).into(holder.photo);

        holder.btnEdit.setOnClickListener(v -> activity.showMasterDialog(master));

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(activity)
                    .setTitle("Підтвердження")
                    .setMessage("Видалити майстра?")
                    .setPositiveButton("Так", (dialog, which) -> activity.deleteMaster(master.getId()))
                    .setNegativeButton("Ні", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

    static class MasterViewHolder extends RecyclerView.ViewHolder {
        TextView name, category;
        ImageView photo;
        Button btnEdit, btnDelete;

        public MasterViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.master_name);
            category = itemView.findViewById(R.id.master_category);
            photo = itemView.findViewById(R.id.master_photo);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
