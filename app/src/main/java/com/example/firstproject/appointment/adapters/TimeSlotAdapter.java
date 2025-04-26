package com.example.firstproject.appointment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.appointment.models.TimeSlot;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private Context context;
    private List<TimeSlot> timeSlots;
    private OnTimeSlotClickListener listener;
    private int selectedPosition = -1;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot timeSlot);
    }

    public TimeSlotAdapter(Context context, List<TimeSlot> timeSlots, OnTimeSlotClickListener listener) {
        this.context = context;
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        holder.bind(timeSlot, position);
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private CardView cardTimeSlot;
        private TextView textTime;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTimeSlot = itemView.findViewById(R.id.card_time_slot);
            textTime = itemView.findViewById(R.id.text_time);
        }

        public void bind(TimeSlot timeSlot, int position) {
            textTime.setText(timeSlot.getTime());

            if (timeSlot.isAvailable()) {
                if (selectedPosition == position) {
                    cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(R.color.accent_blue));
                    textTime.setTextColor(context.getResources().getColor(android.R.color.white));
                } else {
                    cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                    textTime.setTextColor(context.getResources().getColor(R.color.text_primary));
                }

                itemView.setOnClickListener(v -> {
                    int previousSelected = selectedPosition;
                    selectedPosition = getAdapterPosition();

                    // Update UI
                    notifyItemChanged(previousSelected);
                    notifyItemChanged(selectedPosition);

                    if (listener != null) {
                        listener.onTimeSlotClick(timeSlot);
                    }
                });
            } else {
                cardTimeSlot.setCardBackgroundColor(context.getResources().getColor(R.color.light_gray));
                textTime.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                itemView.setOnClickListener(null);
            }
        }
    }
}
