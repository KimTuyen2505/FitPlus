package com.example.firstproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firstproject.R;
import com.example.firstproject.dao.MenstrualCycleDAO;
import com.example.firstproject.models.MenstrualCycle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MenstrualCycleFragment extends Fragment {

    private MenstrualCycleListener listener;
    private CalendarView calendarMenstrualCycle;
    private Button btnLogPeriod;
    private TextView textCycleInfo, textCycleAlerts;
    private MenstrualCycleDAO menstrualCycleDAO;
    private SimpleDateFormat dateFormat;
    private MenstrualCycle currentCycle;

    public interface MenstrualCycleListener {
        void onPeriodLogged(Date date);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MenstrualCycleListener) {
            listener = (MenstrualCycleListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MenstrualCycleListener");
        }
        menstrualCycleDAO = new MenstrualCycleDAO(context);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public void onResume() {
        super.onResume();
        menstrualCycleDAO.open();
        loadCycleData();
    }

    @Override
    public void onPause() {
        super.onPause();
        menstrualCycleDAO.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menstrual_cycle, container, false);

        calendarMenstrualCycle = view.findViewById(R.id.calendar_menstrual_cycle);
        btnLogPeriod = view.findViewById(R.id.btn_log_period);
        textCycleInfo = view.findViewById(R.id.text_cycle_info);
        textCycleAlerts = view.findViewById(R.id.text_cycle_alerts);

        calendarMenstrualCycle.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            // Handle date selection
        });

        btnLogPeriod.setOnClickListener(v -> showLogPeriodDialog());

        return view;
    }

    private void loadCycleData() {
        currentCycle = menstrualCycleDAO.getLatestMenstrualCycle();
        if (currentCycle != null) {
            updateCycleInfo();
        } else {
            textCycleInfo.setText("Chưa có dữ liệu chu kỳ");
            textCycleAlerts.setText("Hãy ghi lại chu kỳ đầu tiên của bạn");
        }
    }

    private void updateCycleInfo() {
        if (currentCycle == null) return;

        String startDate = dateFormat.format(currentCycle.getStartDate());
        String endDate = currentCycle.getEndDate() != null ?
                dateFormat.format(currentCycle.getEndDate()) : "Đang diễn ra";

        int cycleLength = currentCycle.calculatePeriodLength();

        StringBuilder info = new StringBuilder();
        info.append("Chu kỳ gần nhất:\n");
        info.append("Bắt đầu: ").append(startDate).append("\n");
        info.append("Kết thúc: ").append(endDate).append("\n");
        if (cycleLength > 0) {
            info.append("Độ dài chu kỳ: ").append(cycleLength).append(" ngày\n");
        }
        if (currentCycle.getSymptoms() != null && !currentCycle.getSymptoms().isEmpty()) {
            info.append("Triệu chứng: ").append(currentCycle.getSymptoms());
        }

        textCycleInfo.setText(info.toString());

        // Calculate and show alerts
        updateCycleAlerts();
    }

    private void updateCycleAlerts() {
        if (currentCycle == null || currentCycle.getStartDate() == null) return;

        Calendar today = Calendar.getInstance();
        Calendar nextPeriod = Calendar.getInstance();
        nextPeriod.setTime(currentCycle.getStartDate());
        nextPeriod.add(Calendar.DAY_OF_MONTH, 28); // Assuming 28-day cycle

        long daysDiff = (nextPeriod.getTimeInMillis() - today.getTimeInMillis()) /
                (24 * 60 * 60 * 1000);

        if (daysDiff <= 3 && daysDiff >= 0) {
            textCycleAlerts.setText("Chu kỳ tiếp theo dự kiến sẽ bắt đầu trong " +
                    daysDiff + " ngày nữa");
        } else if (daysDiff < 0) {
            textCycleAlerts.setText("Chu kỳ tiếp theo đã quá hạn " +
                    Math.abs(daysDiff) + " ngày");
        } else {
            textCycleAlerts.setText("Chu kỳ tiếp theo dự kiến sau " + daysDiff + " ngày");
        }
    }

    private void showLogPeriodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_log_period, null);
        builder.setView(dialogView);

        final CalendarView calendarView = dialogView.findViewById(R.id.calendar_select_date);
        final EditText editSymptoms = dialogView.findViewById(R.id.edit_symptoms);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Date[] selectedDate = {new Date()};
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate[0] = calendar.getTime();
        });

        btnSave.setOnClickListener(v -> {
            String symptoms = editSymptoms.getText().toString();

            // If there's an ongoing cycle, end it
            if (currentCycle != null && currentCycle.getEndDate() == null) {
                currentCycle.setEndDate(new Date());
                menstrualCycleDAO.updateMenstrualCycle(currentCycle);
            }

            // Start new cycle
            MenstrualCycle newCycle = new MenstrualCycle(selectedDate[0], null, symptoms);
            long id = menstrualCycleDAO.insertMenstrualCycle(newCycle);
            newCycle.setId(id);
            currentCycle = newCycle;

            loadCycleData();

            if (listener != null) {
                listener.onPeriodLogged(selectedDate[0]);
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

