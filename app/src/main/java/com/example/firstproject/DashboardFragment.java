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

import com.example.firstproject.database.HealthMeasurementDAO;
import com.example.firstproject.database.UserDAO;
import com.example.firstproject.models.HealthMeasurement;
import com.example.firstproject.models.User;

public class DashboardFragment extends Fragment {

    private DashboardListener listener;
    private TextView textWeight, textHeight, textBMI, textHealthTip;
    private Button btnAddMeasurement, btnViewReminders;
    private UserDAO userDAO;
    private HealthMeasurementDAO healthMeasurementDAO;

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

        // Khởi tạo DAO
        userDAO = new UserDAO(getContext());
        userDAO.open();
        healthMeasurementDAO = new HealthMeasurementDAO(getContext());
        healthMeasurementDAO.open();

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

        // Load data from database
        loadHealthData();

        return view;
    }

    private void loadHealthData() {
        // Lấy thông tin người dùng từ cơ sở dữ liệu
        User user = userDAO.getUser();

        // Lấy chỉ số sức khỏe mới nhất từ cơ sở dữ liệu
        HealthMeasurement latestWeight = healthMeasurementDAO.getLatestMeasurementByType("weight");
        HealthMeasurement latestHeight = healthMeasurementDAO.getLatestMeasurementByType("height");

        // Hiển thị thông tin
        if (latestWeight != null) {
            textWeight.setText("Cân nặng: " + latestWeight.getValue() + " kg");
        } else if (user != null) {
            textWeight.setText("Cân nặng: " + user.getWeight() + " kg");
        } else {
            textWeight.setText("Cân nặng: Chưa có dữ liệu");
        }

        if (latestHeight != null) {
            textHeight.setText("Chiều cao: " + latestHeight.getValue() + " cm");
        } else if (user != null) {
            textHeight.setText("Chiều cao: " + user.getHeight() + " cm");
        } else {
            textHeight.setText("Chiều cao: Chưa có dữ liệu");
        }

        // Tính BMI
        float bmi = 0;
        String bmiCategory = "Chưa có dữ liệu";

        if (user != null) {
            bmi = user.calculateBMI();
            bmiCategory = user.getBMICategory();
        } else if (latestWeight != null && latestHeight != null) {
            float weight = latestWeight.getValue();
            float height = latestHeight.getValue() / 100; // Chuyển từ cm sang m
            bmi = weight / (height * height);

            if (bmi < 18.5) {
                bmiCategory = "Thiếu cân";
            } else if (bmi < 25) {
                bmiCategory = "Bình thường";
            } else if (bmi < 30) {
                bmiCategory = "Thừa cân";
            } else {
                bmiCategory = "Béo phì";
            }
        }

        if (bmi > 0) {
            textBMI.setText(String.format("BMI: %.1f (%s)", bmi, bmiCategory));
        } else {
            textBMI.setText("BMI: Chưa có dữ liệu");
        }

        // Hiển thị lời khuyên sức khỏe
        String[] healthTips = {
                "Hãy nhớ uống đủ nước! Mục tiêu 8 ly nước mỗi ngày.",
                "Tập thể dục ít nhất 30 phút mỗi ngày để duy trì sức khỏe tốt.",
                "Ăn nhiều rau xanh và trái cây để cung cấp đủ vitamin và khoáng chất.",
                "Ngủ đủ 7-8 tiếng mỗi đêm để cơ thể được nghỉ ngơi và phục hồi.",
                "Hạn chế thức ăn nhanh và đồ uống có đường để giảm nguy cơ béo phì."
        };

        // Chọn ngẫu nhiên một lời khuyên
        int randomIndex = (int) (Math.random() * healthTips.length);
        textHealthTip.setText(healthTips[randomIndex]);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay lại fragment
        loadHealthData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Đóng kết nối cơ sở dữ liệu
        if (userDAO != null) {
            userDAO.close();
        }
        if (healthMeasurementDAO != null) {
            healthMeasurementDAO.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}


