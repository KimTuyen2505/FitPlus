package com.example.firstproject;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firstproject.database.HealthMeasurementDAO;
import com.example.firstproject.models.HealthMeasurement;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthTrackingFragment extends Fragment {

    private HealthTrackingListener listener;
    private FrameLayout chartWeightContainer, chartHeightContainer;
    private Button btnAddMeasurement;
    private HealthMeasurementDAO healthMeasurementDAO;
    private LineChart weightChart, heightChart;

    public interface HealthTrackingListener {
        void onMeasurementAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HealthTrackingListener) {
            listener = (HealthTrackingListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HealthTrackingListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_tracking, container, false);

        // Khởi tạo DAO
        healthMeasurementDAO = new HealthMeasurementDAO(getContext());
        healthMeasurementDAO.open();

        // Khởi tạo views
        chartWeightContainer = view.findViewById(R.id.chart_weight_container);
        chartHeightContainer = view.findViewById(R.id.chart_height_container);
        btnAddMeasurement = view.findViewById(R.id.btn_add_measurement);

        // Đặt sự kiện click cho nút thêm chỉ số
        btnAddMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMeasurementDialog();
            }
        });

        // Tạo và cấu hình biểu đồ
        setupCharts();

        // Tải dữ liệu cho biểu đồ
        loadChartData();

        return view;
    }

    private void setupCharts() {
        // Tạo biểu đồ cân nặng
        weightChart = new LineChart(getContext());
        chartWeightContainer.addView(weightChart);
        configureChart(weightChart, "Cân nặng (kg)");

        // Tạo biểu đồ chiều cao
        heightChart = new LineChart(getContext());
        chartHeightContainer.addView(heightChart);
        configureChart(heightChart, "Chiều cao (cm)");
    }

    private void configureChart(LineChart chart, String label) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(Color.WHITE);

        // Cấu hình trục X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return mFormat.format(new Date(millis));
            }
        });

        // Cấu hình trục Y bên trái
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);

        // Cấu hình trục Y bên phải (ẩn)
        chart.getAxisRight().setEnabled(false);

        // Cấu hình legend
        chart.getLegend().setEnabled(false);
    }

    private void loadChartData() {
        // Tải dữ liệu cân nặng
        List<HealthMeasurement> weightMeasurements = healthMeasurementDAO.getMeasurementsByType("weight");
        updateChart(weightChart, weightMeasurements, "Cân nặng", Color.BLUE);

        // Tải dữ liệu chiều cao
        List<HealthMeasurement> heightMeasurements = healthMeasurementDAO.getMeasurementsByType("height");
        updateChart(heightChart, heightMeasurements, "Chiều cao", Color.GREEN);
    }

    private void updateChart(LineChart chart, List<HealthMeasurement> measurements, String label, int color) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (HealthMeasurement measurement : measurements) {
            // Sử dụng timestamp làm giá trị X
            entries.add(new Entry(measurement.getDate().getTime(), measurement.getValue()));
        }

        // Nếu không có dữ liệu, thêm một điểm mẫu
        if (entries.isEmpty()) {
            entries.add(new Entry(new Date().getTime(), 0));
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(50);
        dataSet.setDrawValues(true);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // Refresh biểu đồ
    }

    private void showAddMeasurementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_measurement, null);
        builder.setView(dialogView);

        final EditText editWeight = dialogView.findViewById(R.id.edit_weight);
        final EditText editHeight = dialogView.findViewById(R.id.edit_height);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = editWeight.getText().toString().trim();
                String heightStr = editHeight.getText().toString().trim();

                boolean hasError = false;

                // Lưu cân nặng nếu đã nhập
                if (!weightStr.isEmpty()) {
                    try {
                        float weight = Float.parseFloat(weightStr);
                        HealthMeasurement weightMeasurement = new HealthMeasurement("weight", weight);
                        long result = healthMeasurementDAO.insertMeasurement(weightMeasurement);
                        if (result <= 0) {
                            hasError = true;
                        }
                    } catch (NumberFormatException e) {
                        hasError = true;
                    }
                }

                // Lưu chiều cao nếu đã nhập
                if (!heightStr.isEmpty()) {
                    try {
                        float height = Float.parseFloat(heightStr);
                        HealthMeasurement heightMeasurement = new HealthMeasurement("height", height);
                        long result = healthMeasurementDAO.insertMeasurement(heightMeasurement);
                        if (result <= 0) {
                            hasError = true;
                        }
                    } catch (NumberFormatException e) {
                        hasError = true;
                    }
                }

                if (hasError) {
                    Toast.makeText(getContext(), "Lỗi khi lưu chỉ số", Toast.LENGTH_SHORT).show();
                } else {
                    // Cập nhật biểu đồ
                    loadChartData();

                    // Thông báo cho activity
                    if (listener != null) {
                        listener.onMeasurementAdded();
                    }

                    Toast.makeText(getContext(), "Đã thêm chỉ số mới thành công", Toast.LENGTH_SHORT).show();
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


