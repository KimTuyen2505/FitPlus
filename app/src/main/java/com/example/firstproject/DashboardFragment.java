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
import androidx.cardview.widget.CardView;

import com.example.firstproject.R;
import com.example.firstproject.dao.UserDAO;
import com.example.firstproject.dao.HealthMeasurementDAO;
import com.example.firstproject.dao.ReminderDAO;
import com.example.firstproject.models.User;
import com.example.firstproject.models.HealthMeasurement;
import com.example.firstproject.models.Reminder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardListener listener;
    private TextView textWeight, textHeight, textBMI, textHealthTip;
    private TextView textLastMeasurement, textNextReminder;
    private Button btnAddMeasurement, btnViewReminders;
    private CardView cardHealth, cardReminders, cardTips;

    private UserDAO userDAO;
    private HealthMeasurementDAO healthMeasurementDAO;
    private ReminderDAO reminderDAO;

    private SimpleDateFormat dateFormat;

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

        // Initialize DAOs
        userDAO = new UserDAO(context);
        healthMeasurementDAO = new HealthMeasurementDAO(context);
        reminderDAO = new ReminderDAO(context);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public void onResume() {
        super.onResume();
        userDAO.open();
        healthMeasurementDAO.open();
        reminderDAO.open();
        loadHealthData();
    }

    @Override
    public void onPause() {
        super.onPause();
        userDAO.close();
        healthMeasurementDAO.close();
        reminderDAO.close();
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
        textLastMeasurement = view.findViewById(R.id.text_last_measurement);
        textNextReminder = view.findViewById(R.id.text_next_reminder);
        btnAddMeasurement = view.findViewById(R.id.btn_add_measurement);
        btnViewReminders = view.findViewById(R.id.btn_view_reminders);
        cardHealth = view.findViewById(R.id.card_health);
        cardReminders = view.findViewById(R.id.card_reminders);
        cardTips = view.findViewById(R.id.card_tips);

        // Set click listeners
        btnAddMeasurement.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddMeasurementClicked();
            }
        });

        btnViewReminders.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewRemindersClicked();
            }
        });

        return view;
    }

    private void loadHealthData() {
        // Load user data
        User user = userDAO.getCurrentUser();
        if (user != null) {
            textWeight.setText(String.format(Locale.getDefault(), "Cân nặng: %.1f kg", user.getWeight()));
            textHeight.setText(String.format(Locale.getDefault(), "Chiều cao: %.1f cm", user.getHeight()));

            float bmi = user.calculateBMI();
            String bmiStatus = user.getBMICategory();
            textBMI.setText(String.format(Locale.getDefault(), "BMI: %.1f (%s)", bmi, bmiStatus));
        }

        // Load latest measurements
        HealthMeasurement latestWeight = healthMeasurementDAO.getLatestMeasurementByType("weight");
        HealthMeasurement latestHeight = healthMeasurementDAO.getLatestMeasurementByType("height");

        if (latestWeight != null || latestHeight != null) {
            StringBuilder measurementText = new StringBuilder("Lần đo gần nhất:\n");
            if (latestWeight != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String dateStr = dateFormat.format(latestWeight.getDate());
                measurementText.append(String.format(Locale.getDefault(),
                        "Cân nặng: %.1f kg (%s)\n",
                        latestWeight.getValue(),
                        dateStr));
            }
            if (latestHeight != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String dateStr = dateFormat.format(latestHeight.getDate());
                measurementText.append(String.format(Locale.getDefault(),
                        "Chiều cao: %.1f cm (%s)",
                        latestHeight.getValue(),
                        dateStr));
            }
            textLastMeasurement.setText(measurementText.toString());
        } else {
            textLastMeasurement.setText("Chưa có dữ liệu đo lường");
        }

        // Load reminders
        List<Reminder> reminders = reminderDAO.getAllReminders();
        if (!reminders.isEmpty()) {
            StringBuilder reminderText = new StringBuilder("Nhắc nhở sắp tới:\n");
            int count = 0;
            for (Reminder reminder : reminders) {
                if (count < 3) { // Show only 3 latest reminders
                    reminderText.append(String.format("• %s (%s)\n",
                            reminder.getTitle(),
                            reminder.getTime()));
                    count++;
                }
            }
            textNextReminder.setText(reminderText.toString());
        } else {
            textNextReminder.setText("Không có nhắc nhở nào");
        }

        // Update health tips based on user data
        updateHealthTips(user);
    }

    private void updateHealthTips(User user) {
        StringBuilder tips = new StringBuilder();

        if (user != null) {
            float bmi = user.calculateBMI();

            // BMI-based tips
            if (bmi < 18.5) {
                tips.append("• Bạn đang thiếu cân. Hãy tăng cường dinh dưỡng và tham khảo ý kiến bác sĩ.\n");
            } else if (bmi > 25) {
                tips.append("• Bạn đang thừa cân. Hãy tập thể dục đều đặn và điều chỉnh chế độ ăn.\n");
            }

            // General health tips
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


