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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MenstrualCycleFragment extends Fragment {

    private MenstrualCycleListener listener;
    private CalendarView calendarMenstrualCycle;
    private Button btnLogPeriod;
    private TextView textCycleInfo, textCycleAlerts;
    private Date lastPeriodStartDate;
    private int averageCycleLength = 28; // Default cycle length

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menstrual_cycle, container, false);

        // Initialize views
        calendarMenstrualCycle = view.findViewById(R.id.calendar_menstrual_cycle);
        btnLogPeriod = view.findViewById(R.id.btn_log_period);
        textCycleInfo = view.findViewById(R.id.text_cycle_info);
        textCycleAlerts = view.findViewById(R.id.text_cycle_alerts);

        // Set up calendar
        calendarMenstrualCycle.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                // Handle date selection
            }
        });

        // Set click listener for log period button
        btnLogPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogPeriodDialog();
            }
        });

        // Load cycle data
        loadCycleData();

        return view;
    }

    private void loadCycleData() {
        // In a real app, this would load data from a database
        // For now, we'll just use dummy data
        lastPeriodStartDate = new Date(System.currentTimeMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String lastPeriodDate = dateFormat.format(lastPeriodStartDate);

        // Tính toán chu kỳ tiếp theo
        Calendar nextPeriodCal = Calendar.getInstance();
        nextPeriodCal.setTime(lastPeriodStartDate);
        nextPeriodCal.add(Calendar.DAY_OF_MONTH, averageCycleLength);
        String nextPeriodDate = dateFormat.format(nextPeriodCal.getTime());

        // Tính toán cửa sổ sinh sản
        Calendar fertileStartCal = Calendar.getInstance();
        fertileStartCal.setTime(lastPeriodStartDate);
        fertileStartCal.add(Calendar.DAY_OF_MONTH, averageCycleLength - 19);

        Calendar fertileEndCal = Calendar.getInstance();
        fertileEndCal.setTime(lastPeriodStartDate);
        fertileEndCal.add(Calendar.DAY_OF_MONTH, averageCycleLength - 11);

        String fertileWindowStart = dateFormat.format(fertileStartCal.getTime());
        String fertileWindowEnd = dateFormat.format(fertileEndCal.getTime());

        textCycleInfo.setText("Chu kỳ gần nhất: " + lastPeriodDate + "\n" +
                "Chu kỳ tiếp theo: " + nextPeriodDate + "\n" +
                "Cửa sổ sinh sản: " + fertileWindowStart + " đến " + fertileWindowEnd + "\n" +
                "Độ dài chu kỳ trung bình: " + averageCycleLength + " ngày");

        // Đặt cảnh báo dựa trên chu kỳ
        Calendar today = Calendar.getInstance();
        long daysDiff = (today.getTimeInMillis() - lastPeriodStartDate.getTime()) / (24 * 60 * 60 * 1000);

        if (daysDiff >= averageCycleLength - 3 && daysDiff <= averageCycleLength) {
            textCycleAlerts.setText("Chu kỳ của bạn dự kiến sẽ bắt đầu sớm. Hãy chuẩn bị!");
        } else if (daysDiff >= averageCycleLength - 14 && daysDiff <= averageCycleLength - 11) {
            textCycleAlerts.setText("Bạn đang trong cửa sổ sinh sản.");
        } else {
            textCycleAlerts.setText("Không có cảnh báo vào lúc này.");
        }
    }

    private void showLogPeriodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_log_period, null);
        builder.setView(dialogView);

        final CalendarView calendarView = dialogView.findViewById(R.id.calendar_select_date);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Date[] selectedDate = {new Date()};
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                selectedDate[0] = calendar.getTime();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPeriodStartDate = selectedDate[0];
                loadCycleData();

                // Notify the listener
                if (listener != null) {
                    listener.onPeriodLogged(lastPeriodStartDate);
                }

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

