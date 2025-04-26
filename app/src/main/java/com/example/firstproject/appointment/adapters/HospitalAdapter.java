package com.example.firstproject.appointment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.appointment.models.Hospital;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private Context context;
    private List<Hospital> hospitals;
    private OnHospitalClickListener listener;
    private int selectedPosition = -1;

    public interface OnHospitalClickListener {
        void onHospitalClick(Hospital hospital);
    }

    public HospitalAdapter(Context context, List<Hospital> hospitals, OnHospitalClickListener listener) {
        this.context = context;
        this.hospitals = hospitals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hospital, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.bind(hospital, position);
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public void updateHospitals(List<Hospital> newHospitals) {
        this.hospitals = newHospitals;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public class HospitalViewHolder extends RecyclerView.ViewHolder {
        private CardView cardHospital;
        private TextView textName;
        private TextView textAddress;
        private RatingBar ratingBar;
        private TextView textSpecialties;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardHospital = itemView.findViewById(R.id.card_hospital);
            textName = itemView.findViewById(R.id.text_hospital_name);
            textAddress = itemView.findViewById(R.id.text_address);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            textSpecialties = itemView.findViewById(R.id.text_specialties);
        }

        public void bind(Hospital hospital, int position) {
            textName.setText(hospital.getName());
            textAddress.setText(hospital.getAddress());
            ratingBar.setRating(hospital.getRating());

            // Format specialties
            StringBuilder specialtiesText = new StringBuilder("Chuyên khoa: ");
            for (int i = 0; i < hospital.getSpecialties().length; i++) {
                specialtiesText.append(hospital.getSpecialties()[i]);
                if (i < hospital.getSpecialties().length - 1) {
                    specialtiesText.append(", ");
                }
            }
            textSpecialties.setText(specialtiesText.toString());

            // Highlight selected hospital
            if (selectedPosition == position) {
                cardHospital.setCardBackgroundColor(context.getResources().getColor(R.color.primary_light_blue));
            } else {
                cardHospital.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            }

            itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = getAdapterPosition();

                // Update UI
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                if (listener != null) {
                    listener.onHospitalClick(hospital);
                }
            });
        }
    }
}
