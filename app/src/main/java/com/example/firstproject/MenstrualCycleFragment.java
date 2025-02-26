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

import com.example.firstproject.database.MenstrualCycleDAO;
import com.example.firstproject.models.MenstrualCycle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenstrualCycleFragment extends Fragment {

    private MenstrualCycleListener listener;
    private CalendarView calendarMenstrualCycle;
    private Button btnLogPeriod;
    private TextView textCycleInfo, textCycleAlerts;
    private MenstrualCycleDAO menstrualCycleDAO;
    private int averageCycleLength = 28; // Độ dài chu kỳ mặc định

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

        // Khởi tạo DAO
        menstrualCycleDAO = new MenstrualCycleDAO(getContext());
        menstrualCycleDAO.open();

        // Khởi tạo views
        calendarMenstrualCycle = view.findViewById(R.id.calendar_menstrual_cycle);
        btnLogPeriod = view.findViewById(R.id.btn_log_period);
        textCycleInfo = view.findViewById(R.id.text_cycle_info);
        textCycleAlerts = view.findViewById(R.id.text_cycle_alerts);

        // Thiết lập calendar
        calendarMenstrualCycle.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                // Xử lý khi chọn ngày
            }
        });

        // Đặt sự kiện click cho nút ghi nhận chu kỳ
        btnLogPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogPeriodDialog();
            }
        });

        // Tải dữ liệu chu kỳ
        loadCycleData();

        return view;
    }

    private void loadCycleData() {
        // Lấy chu kỳ gần nhất từ cơ sở dữ liệu
        MenstrualCycle latestCycle = menstrualCycleDAO.getLatestMenstrualCycle();

        // Nếu không có chu kỳ nào, tạo một chu kỳ mẫu
        if (latestCycle == null) {
            // Tạo chu kỳ mẫu (14 ngày trước)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -14);
            latestCycle = new MenstrualCycle(calendar.getTime());

            // Lưu vào cơ sở dữ liệu
            long result = menstrualCycleDAO.insertMenstrualCycle(latestCycle);
            if (result > 0) {
                latestCycle.setId(result);
            }
        }

        // Tính toán và hiển thị thông tin chu kỳ
        displayCycleInfo(latestCycle);
    }

    private void displayCycleInfo(MenstrualCycle cycle) {
        if (cycle == null) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String lastPeriodDate = dateFormat.format(cycle.getStartDate());

        // Tính toán chu kỳ tiếp theo
        Calendar nextPeriodCal = Calendar.getInstance();
        nextPeriodCal.setTime(cycle.getStartDate());
        nextPeriodCal.add(Calendar.DAY_OF_MONTH, averageCycleLength);
        String nextPeriodDate = dateFormat.format(nextPeriodCal.getTime());

        // Tính toán cửa sổ sinh sản
        Calendar fertileStartCal = Calendar.getInstance();
        fertileStartCal.setTime(cycle.getStartDate());
        fertileStartCal.add(Calendar.DAY_OF_MONTH, averageCycleLength - 19);

        Calendar fertileEndCal = Calendar.getInstance();
        fertileEndCal.setTime(cycle.getStartDate());
        fertileEndCal.add(Calendar.DAY_OF_MONTH, averageCycleLength - 11);

        String fertileWindowStart = dateFormat.format(fertileStartCal.getTime());
        String fertileWindowEnd = dateFormat.format(fertileEndCal.getTime());

        // Hiển thị thông tin
        textCycleInfo.setText("Chu kỳ gần nhất: " + lastPeriodDate + "\n" +
                "Chu kỳ tiếp theo: " + nextPeriodDate + "\n" +
                "Cửa sổ sinh sản: " + fertileWindowStart + " đến " + fertileWindowEnd + "\n" +
                "Độ dài chu kỳ trung bình: " + averageCycleLength + " ngày");

        // Đặt cảnh báo dựa trên chu kỳ
        Calendar today = Calendar.getInstance();
        long daysDiff = (today.getTimeInMillis() - cycle.getStartDate().getTime()) / (24 * 60 * 60 * 1000);

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
        final EditText editSymptoms = dialogView.findViewById(R.id.edit_symptoms);
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
                String symptoms = editSymptoms.getText().toString().trim();

                // Tạo đối tượng MenstrualCycle mới
                MenstrualCycle cycle = new MenstrualCycle(selectedDate[0]);
                cycle.setSymptoms(symptoms);

                // Lưu vào cơ sở dữ liệu
                long result = menstrualCycleDAO.insertMenstrualCycle(cycle);

                if (result > 0) {
                    // Cập nhật ID cho đối tượng MenstrualCycle
                    cycle.setId(result);

                    // Cập nhật thông tin chu kỳ
                    displayCycleInfo(cycle);

                    // Thông báo cho activity
                    if (listener != null) {
                        listener.onPeriodLogged(selectedDate[0]);
                    }

                    Toast.makeText(getContext(), "Đã ghi nhận chu kỳ thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi ghi nhận chu kỳ", Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        // Đóng kết nối cơ sở dữ liệu
        if (menstrualCycleDAO != null) {
            menstrualCycleDAO.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

