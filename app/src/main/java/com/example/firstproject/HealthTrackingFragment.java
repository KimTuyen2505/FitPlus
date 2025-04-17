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
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firstproject.dao.HealthMeasurementDAO;
import com.example.firstproject.models.HealthMeasurement;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthTrackingFragment extends Fragment {

    private HealthTrackingListener listener;
    private LineChart chartWeight, chartHeight, chartHeartRate;
    private TextView textHeartRateStatus;
    private Button btnAddMeasurement;
    private HealthMeasurementDAO healthMeasurementDAO;

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

        // Khởi tạo DAO
        healthMeasurementDAO = new HealthMeasurementDAO(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        healthMeasurementDAO.open();
        loadChartData();
    }

    @Override
    public void onPause() {
        super.onPause();
        healthMeasurementDAO.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_tracking, container, false);

        // Initialize views
        chartWeight = view.findViewById(R.id.chart_weight);
        chartHeight = view.findViewById(R.id.chart_height);
        chartHeartRate = view.findViewById(R.id.chart_heart_rate);
        textHeartRateStatus = view.findViewById(R.id.text_heart_rate_status);
        btnAddMeasurement = view.findViewById(R.id.btn_add_measurement);

        // Cấu hình biểu đồ
        setupChart(chartWeight, "Cân nặng (kg)");
        setupChart(chartHeight, "Chiều cao (cm)");
        setupChart(chartHeartRate, "Nhịp tim (nhịp/phút)");

        // Set click listener for add measurement button
        btnAddMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMeasurementDialog();
            }
        });

        return view;
    }

    private void setupChart(LineChart chart, String label) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // Cấu hình trục X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new DateAxisValueFormatter());

        // Cấu hình trục Y bên trái
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);

        // Cấu hình trục Y bên phải
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Cấu hình legend
        chart.getLegend().setEnabled(true);
        chart.getLegend().setTextColor(Color.BLACK);

        // Hiển thị "Không có dữ liệu" khi không có dữ liệu
        chart.setNoDataText("Không có dữ liệu");
        chart.setNoDataTextColor(Color.BLACK);
    }

    private void loadChartData() {
        List<HealthMeasurement> weightMeasurements =
                healthMeasurementDAO.getMeasurementsByType(HealthMeasurement.TYPE_WEIGHT);
        updateWeightChart(weightMeasurements);

        List<HealthMeasurement> heightMeasurements =
                healthMeasurementDAO.getMeasurementsByType(HealthMeasurement.TYPE_HEIGHT);
        updateHeightChart(heightMeasurements);

        List<HealthMeasurement> heartRateMeasurements =
                healthMeasurementDAO.getMeasurementsByType(HealthMeasurement.TYPE_HEART_RATE);
        updateHeartRateChart(heartRateMeasurements);

        HealthMeasurement latestHeartRate =
                healthMeasurementDAO.getLatestMeasurementByType(HealthMeasurement.TYPE_HEART_RATE);

        if (latestHeartRate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateStr = dateFormat.format(latestHeartRate.getDate());

            textHeartRateStatus.setText(String.format(
                    "Nhịp tim gần nhất: %.0f nhịp/phút (%s)\n%s",
                    latestHeartRate.getValue(),
                    dateStr,
                    latestHeartRate.getHeartRateStatus()
            ));

            if (latestHeartRate.isHeartRateNormal()) {
                textHeartRateStatus.setTextColor(getResources().getColor(R.color.accent_blue));
            } else {
                textHeartRateStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        } else {
            textHeartRateStatus.setText("Trạng thái nhịp tim: Chưa có dữ liệu");
            textHeartRateStatus.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void updateWeightChart(List<HealthMeasurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            chartWeight.setData(null);
            chartWeight.invalidate();
            return;
        }

        Collections.sort(measurements, (m1, m2) -> m1.getDate().compareTo(m2.getDate()));

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (int i = 0; i < measurements.size(); i++) {
            HealthMeasurement measurement = measurements.get(i);
            entries.add(new Entry(i, measurement.getValue()));
            xLabels.add(dateFormat.format(measurement.getDate()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng (kg)");
        styleLineDataSet(dataSet, Color.rgb(65, 105, 225)); // Royal Blue

        LineData lineData = new LineData(dataSet);
        chartWeight.setData(lineData);

        XAxis xAxis = chartWeight.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));

        chartWeight.invalidate();
    }

    private void updateHeightChart(List<HealthMeasurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            chartHeight.setData(null);
            chartHeight.invalidate();
            return;
        }

        Collections.sort(measurements, (m1, m2) -> m1.getDate().compareTo(m2.getDate()));

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (int i = 0; i < measurements.size(); i++) {
            HealthMeasurement measurement = measurements.get(i);
            entries.add(new Entry(i, measurement.getValue()));
            xLabels.add(dateFormat.format(measurement.getDate()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Chiều cao (cm)");
        styleLineDataSet(dataSet, Color.rgb(46, 139, 87)); // Sea Green

        LineData lineData = new LineData(dataSet);
        chartHeight.setData(lineData);

        XAxis xAxis = chartHeight.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));

        chartHeight.invalidate();
    }

    private void updateHeartRateChart(List<HealthMeasurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            chartHeartRate.setData(null);
            chartHeartRate.invalidate();
            return;
        }
        Collections.sort(measurements, (m1, m2) -> m1.getDate().compareTo(m2.getDate()));

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

        for (int i = 0; i < measurements.size(); i++) {
            HealthMeasurement measurement = measurements.get(i);
            entries.add(new Entry(i, measurement.getValue()));
            xLabels.add(dateFormat.format(measurement.getDate()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Nhịp tim (nhịp/phút)");
        styleLineDataSet(dataSet, Color.rgb(220, 20, 60)); // Crimson

        LineData lineData = new LineData(dataSet);
        chartHeartRate.setData(lineData);

        XAxis xAxis = chartHeartRate.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));

        chartHeartRate.invalidate();
    }

    private void styleLineDataSet(LineDataSet dataSet, int color) {
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(50);
        dataSet.setFillColor(color);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    }

    private void showAddMeasurementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_measurement, null);
        builder.setView(dialogView);

        final RadioGroup radioGroup = dialogView.findViewById(R.id.radio_measurement_type);
        final TextInputLayout layoutWeight = dialogView.findViewById(R.id.layout_weight);
        final TextInputLayout layoutHeight = dialogView.findViewById(R.id.layout_height);
        final TextInputLayout layoutHeartRate = dialogView.findViewById(R.id.layout_heart_rate);

        final EditText editWeight = dialogView.findViewById(R.id.edit_weight);
        final EditText editHeight = dialogView.findViewById(R.id.edit_height);
        final EditText editHeartRate = dialogView.findViewById(R.id.edit_heart_rate);

        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                layoutWeight.setVisibility(View.GONE);
                layoutHeight.setVisibility(View.GONE);
                layoutHeartRate.setVisibility(View.GONE);

                if (checkedId == R.id.radio_weight) {
                    layoutWeight.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radio_height) {
                    layoutHeight.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radio_heart_rate) {
                    layoutHeartRate.setVisibility(View.VISIBLE);
                }
            }
        });

        radioGroup.check(R.id.radio_weight);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu dữ liệu dựa trên loại đo lường được chọn
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == R.id.radio_weight) {
                    String weightStr = editWeight.getText().toString();
                    if (!weightStr.isEmpty()) {
                        float weight = Float.parseFloat(weightStr);
                        HealthMeasurement measurement = new HealthMeasurement(
                                HealthMeasurement.TYPE_WEIGHT, weight);

                        healthMeasurementDAO.insertMeasurement(measurement);
                    }
                } else if (selectedId == R.id.radio_height) {
                    String heightStr = editHeight.getText().toString();
                    if (!heightStr.isEmpty()) {
                        float height = Float.parseFloat(heightStr);
                        HealthMeasurement measurement = new HealthMeasurement(
                                HealthMeasurement.TYPE_HEIGHT, height);

                        healthMeasurementDAO.insertMeasurement(measurement);
                    }
                } else if (selectedId == R.id.radio_heart_rate) {
                    String heartRateStr = editHeartRate.getText().toString();
                    if (!heartRateStr.isEmpty()) {
                        float heartRate = Float.parseFloat(heartRateStr);
                        HealthMeasurement measurement = new HealthMeasurement(
                                HealthMeasurement.TYPE_HEART_RATE, heartRate);

                        healthMeasurementDAO.insertMeasurement(measurement);
                    }
                }

                loadChartData();

                if (listener != null) {
                    listener.onMeasurementAdded();
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

    private class DateAxisValueFormatter extends ValueFormatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        @Override
        public String getFormattedValue(float value) {

            return "";
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}



