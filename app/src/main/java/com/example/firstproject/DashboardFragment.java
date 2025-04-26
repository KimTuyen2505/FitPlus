package com.example.firstproject;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.firstproject.dao.HealthMeasurementDAO;
import com.example.firstproject.dao.UserDAO;
import com.example.firstproject.models.HealthMeasurement;
import com.example.firstproject.models.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Locale;

public class DashboardFragment extends Fragment {
    public interface DashboardListener {
        void onAddMeasurementClicked();
        void onViewRemindersClicked();
        void onHealthTrackingClicked();
        void onHealthSuggestionsClicked();
        // Add new interface methods
        void onMenstrualCycleClicked();
        void onConsultDoctorClicked();
        void onBookAppointmentClicked();
    }

    private DashboardListener listener;
    private TextView textUserName, textUserStatus;
    private TextView textWeight, textHeight, textHeartRate, textBMI;
    private CardView cardWeight, cardHeight, cardHeartRate, cardBMI;
    private CardView btnMenstrualCycle, btnViewReminders, btnHealthTracking, btnHealthSuggestions;
    private Button btnConsultDoctor, btnBookAppointment;
    private ShapeableImageView profileImageSmall;
    private TextView textHealthTip;

    private UserDAO userDAO;
    private HealthMeasurementDAO healthMeasurementDAO;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashboardListener) {
            listener = (DashboardListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DashboardListener");
        }

        userDAO = new UserDAO(context);
        healthMeasurementDAO = new HealthMeasurementDAO(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        userDAO.open();
        healthMeasurementDAO.open();
        loadHealthData();
    }

    @Override
    public void onPause() {
        super.onPause();
        userDAO.close();
        healthMeasurementDAO.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize views
        textUserName = view.findViewById(R.id.text_user_name);
        textUserStatus = view.findViewById(R.id.text_user_status);
        textWeight = view.findViewById(R.id.text_weight);
        textHeight = view.findViewById(R.id.text_height);
        textHeartRate = view.findViewById(R.id.text_heart_rate);
        textBMI = view.findViewById(R.id.text_bmi);

        cardWeight = view.findViewById(R.id.card_weight);
        cardHeight = view.findViewById(R.id.card_height);
        cardHeartRate = view.findViewById(R.id.card_heart_rate);
        cardBMI = view.findViewById(R.id.card_bmi);

        // Updated button references
        btnMenstrualCycle = view.findViewById(R.id.btn_menstrual_cycle);
        btnViewReminders = view.findViewById(R.id.btn_view_reminders);
        btnHealthTracking = view.findViewById(R.id.btn_health_tracking);
        btnHealthSuggestions = view.findViewById(R.id.btn_health_suggestions);

        // New doctor consultation buttons
        btnConsultDoctor = view.findViewById(R.id.btn_consult_doctor);
        btnBookAppointment = view.findViewById(R.id.btn_book_appointment);

        profileImageSmall = view.findViewById(R.id.profile_image_small);
        textHealthTip = view.findViewById(R.id.text_health_tip);

        // Set click listeners
        btnMenstrualCycle.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMenstrualCycleClicked();
            }
        });

        btnViewReminders.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewRemindersClicked();
            }
        });

        btnHealthTracking.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHealthTrackingClicked();
            }
        });

        btnHealthSuggestions.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHealthSuggestionsClicked();
            }
        });

        // Set click listeners for new doctor consultation buttons
        btnConsultDoctor.setOnClickListener(v -> {
            try {
                if (listener != null) {
                    listener.onConsultDoctorClicked();
                    Toast.makeText(getContext(), "Đang kết nối với bác sĩ...", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                android.util.Log.e("DashboardFragment", "Error starting DoctorConsultationActivity", e);
                Toast.makeText(getContext(), "Không thể mở trang tư vấn bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });

        btnBookAppointment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookAppointmentClicked();
                Toast.makeText(getContext(), "Đang mở trang đặt lịch khám...", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listeners for new doctor consultation buttons
        btnBookAppointment.setOnClickListener(v -> {
            try {
                if (listener != null) {
                    listener.onBookAppointmentClicked();
                }
            } catch (Exception e) {
                android.util.Log.e("DashboardFragment", "Error starting AppointmentBookingActivity", e);
                Toast.makeText(getContext(), "Không thể mở trang đặt lịch khám", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listeners for health metric cards
        cardWeight.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHealthTrackingClicked();
                Toast.makeText(getContext(), "Đang chuyển đến biểu đồ cân nặng", Toast.LENGTH_SHORT).show();
            }
        });

        cardHeight.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHealthTrackingClicked();
                Toast.makeText(getContext(), "Đang chuyển đến biểu đồ chiều cao", Toast.LENGTH_SHORT).show();
            }
        });

        cardHeartRate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHealthTrackingClicked();
                Toast.makeText(getContext(), "Đang chuyển đến biểu đồ nhịp tim", Toast.LENGTH_SHORT).show();
            }
        });

        cardBMI.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHealthTrackingClicked();
                Toast.makeText(getContext(), "Đang chuyển đến thông tin BMI", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadHealthData() {
        // Load user data
        User user = userDAO.getCurrentUser();
        if (user != null) {
            // Set user name
            String userName = user.getName();
            if (userName != null && !userName.isEmpty()) {
                textUserName.setText("Xin chào, " + userName + "!");
            } else {
                textUserName.setText("Xin chào!");
            }

            // Set weight, height, and BMI from user data
            if (user.getWeight() > 0) {
                textWeight.setText(String.format(Locale.getDefault(), "%.1f kg", user.getWeight()));
            } else {
                textWeight.setText("-- kg");
            }

            if (user.getHeight() > 0) {
                textHeight.setText(String.format(Locale.getDefault(), "%.1f cm", user.getHeight()));
            } else {
                textHeight.setText("-- cm");
            }

            if (user.getHeartRate() > 0) {
                textHeartRate.setText(String.format(Locale.getDefault(), "%d bpm", user.getHeartRate()));
            } else {
                textHeartRate.setText("-- bpm");
            }

            // Calculate and display BMI
            float bmi = user.calculateBMI();
            if (bmi > 0) {
                String bmiCategory = user.getBMICategory();
                textBMI.setText(String.format(Locale.getDefault(), "%.1f (%s)", bmi, bmiCategory));
            } else {
                textBMI.setText("-- (--)");
            }

            // Set profile image if available
            if (user.getProfileImageUri() != null && !user.getProfileImageUri().isEmpty()) {
                try {
                    profileImageSmall.setImageURI(android.net.Uri.parse(user.getProfileImageUri()));
                } catch (Exception e) {
                    profileImageSmall.setImageResource(R.drawable.default_avatar);
                }
            }
        }

        // Try to get latest measurements from database
        HealthMeasurement latestWeight = healthMeasurementDAO.getLatestMeasurementByType(HealthMeasurement.TYPE_WEIGHT);
        if (latestWeight != null) {
            textWeight.setText(String.format(Locale.getDefault(), "%.1f kg", latestWeight.getValue()));
        }

        HealthMeasurement latestHeight = healthMeasurementDAO.getLatestMeasurementByType(HealthMeasurement.TYPE_HEIGHT);
        if (latestHeight != null) {
            textHeight.setText(String.format(Locale.getDefault(), "%.1f cm", latestHeight.getValue()));
        }

        HealthMeasurement latestHeartRate = healthMeasurementDAO.getLatestMeasurementByType(HealthMeasurement.TYPE_HEART_RATE);
        if (latestHeartRate != null) {
            textHeartRate.setText(String.format(Locale.getDefault(), "%.0f bpm", latestHeartRate.getValue()));
        }

        // Update health tips based on user data
        updateHealthTips(user);
    }

    private void updateHealthTips(User user) {
        StringBuilder tips = new StringBuilder();
        if (user != null) {
            float bmi = user.calculateBMI();

            if (bmi < 18.5) {
                tips.append("• Bạn đang thiếu cân. Hãy tăng cường dinh dưỡng và tham khảo ý kiến bác sĩ.\n");
            } else if (bmi > 25) {
                tips.append("• Bạn đang thừa cân. Hãy tập thể dục đều đặn và điều chỉnh chế độ ăn.\n");
            }
            tips.append("• Uống đủ 2L nước mỗi ngày\n");
            tips.append("• Tập thể dục ít nhất 30 phút/ngày\n");
            tips.append("• Đảm bảo ngủ đủ 7-8 tiếng mỗi ngày");
        } else {
            tips.append("Hãy cập nhật thông tin cá nhân để nhận được lời khuyên phù hợp.");
        }
        textHealthTip.setText(tips.toString());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
