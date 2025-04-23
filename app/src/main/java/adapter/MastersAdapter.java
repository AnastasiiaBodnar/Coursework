package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.coursework.R;

import java.util.List;

import models.Master;
import models.ScheduleItem;

public class MastersAdapter extends RecyclerView.Adapter<MastersAdapter.MasterViewHolder> {

    private List<Master> mastersList;
    private Context context;

    public MastersAdapter(List<Master> mastersList, Context context) {
        this.mastersList = mastersList;
        this.context = context;
    }

    @NonNull
    @Override
    public MasterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_master, parent, false);
        return new MasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterViewHolder holder, int position) {
        Master master = mastersList.get(position);
        holder.nameTextView.setText(master.getName() != null ? master.getName() : "");
        holder.specializationTextView.setText(master.getSpecializations() != null ? master.getSpecializations() : "");

        if (master.getSchedule() != null && !master.getSchedule().isEmpty()) {
            StringBuilder scheduleBuilder = new StringBuilder();
            for (ScheduleItem item : master.getSchedule()) {
                scheduleBuilder.append(item.getDay())
                        .append(": ")
                        .append(item.getStart())
                        .append("-")
                        .append(item.getEnd())
                        .append("\n");
            }
            holder.scheduleTextView.setText(scheduleBuilder.toString());
        } else {
            holder.scheduleTextView.setText("Розклад не вказано");
        }

        if (master.getPhotoURL() != null && !master.getPhotoURL().isEmpty()) {
            Glide.with(context)
                    .load(master.getPhotoURL())
                    .placeholder(R.drawable.person)
                    .error(R.drawable.person)
                    .into(holder.masterImageView);
        } else {
            holder.masterImageView.setImageResource(R.drawable.person);
        }
    }

    @Override
    public int getItemCount() {
        return mastersList.size();
    }

    static class MasterViewHolder extends RecyclerView.ViewHolder {
        ImageView masterImageView;
        TextView nameTextView;
        TextView specializationTextView;
        TextView scheduleTextView;

        public MasterViewHolder(@NonNull View itemView) {
            super(itemView);
            masterImageView = itemView.findViewById(R.id.masterImage);
            nameTextView = itemView.findViewById(R.id.masterName);
            specializationTextView = itemView.findViewById(R.id.masterSpecialization);
            scheduleTextView = itemView.findViewById(R.id.masterSchedule); // Ініціалізуємо
        }
    }
}