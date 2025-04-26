package com.example.firstproject.doctor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.doctor.models.Doctor;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctors;
    private OnDoctorClickListener listener;
    private Doctor selectedDoctor;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(Context context, List<Doctor> doctors, OnDoctorClickListener listener) {
        this.context = context;
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.bind(doctor);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public void updateDoctors(List<Doctor> newDoctors) {
        this.doctors = newDoctors;
        notifyDataSetChanged();
    }

    public Doctor getSelectedDoctor() {
        return selectedDoctor;
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgDoctor;
        private TextView textName;
        private TextView textSpecialty;
        private TextView textHospital;
        private RatingBar ratingBar;
        private TextView textConsultations;
        private TextView textAvailability;
        private Button btnConsult;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDoctor = itemView.findViewById(R.id.img_doctor);
            textName = itemView.findViewById(R.id.text_doctor_name);
            textSpecialty = itemView.findViewById(R.id.text_specialty);
            textHospital = itemView.findViewById(R.id.text_hospital);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            textConsultations = itemView.findViewById(R.id.text_consultations);
            textAvailability = itemView.findViewById(R.id.text_availability);
            btnConsult = itemView.findViewById(R.id.btn_consult);
        }

        public void bind(Doctor doctor) {
            textName.setText(doctor.getName());
            textSpecialty.setText("Chuyên khoa: " + doctor.getSpecialty());
            textHospital.setText(doctor.getHospital());
            ratingBar.setRating(doctor.getRating());
            textConsultations.setText(doctor.getConsultations() + " lượt tư vấn");

            if (doctor.isAvailable()) {
                textAvailability.setText("Đang trực tuyến");
                textAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                btnConsult.setEnabled(true);
                btnConsult.setAlpha(1.0f);
            } else {
                textAvailability.setText("Không trực tuyến");
                textAvailability.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                btnConsult.setEnabled(false);
                btnConsult.setAlpha(0.5f);
            }

            btnConsult.setOnClickListener(v -> {
                if (doctor.isAvailable() && listener != null) {
                    selectedDoctor = doctor;
                    listener.onDoctorClick(doctor);
                }
            });
        }
    }
}
