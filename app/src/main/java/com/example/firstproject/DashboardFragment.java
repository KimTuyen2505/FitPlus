package com.example.firstproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private DashboardListener listener;
    private TextView textWeight, textHeight, textBMI, textHealthTip;
    private Button btnAddMeasurement, btnViewReminders;

    public interface DashboardListener {
        void onAddMeasurementClicked();
        void onViewRemindersClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashboardListener) {
            listener = (DashboardListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DashboardListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize views
        textWeight = view.findViewById(R.id.text_weight);
        textHeight = view.findViewById(R.id.text_height);
        textBMI = view.findViewById(R.id.text_bmi);
        textHealthTip = view.findViewById(R.id.text_health_tip);
        btnAddMeasurement = view.findViewById(R.id.btn_add_measurement);
        btnViewReminders = view.findViewById(R.id.btn_view_reminders);

        // Set click listeners
        btnAddMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAddMeasurementClicked();
                }
            }
        });

        btnViewReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onViewRemindersClicked();
                }
            }
        });

        // Load data (in a real app, this would come from a database or shared preferences)
        loadHealthData();

        return view;
    }

    private void loadHealthData() {
        // Trong ứng dụng thực tế, dữ liệu này sẽ được tải từ cơ sở dữ liệu hoặc shared preferences
        // Hiện tại, chúng ta chỉ sử dụng dữ liệu mẫu
        textWeight.setText("Cân nặng: 70 kg");
        textHeight.setText("Chiều cao: 175 cm");
        textBMI.setText("BMI: 22.9 (Bình thường)");
        textHealthTip.setText("Hãy nhớ uống đủ nước! Mục tiêu 8 ly nước mỗi ngày.");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}


