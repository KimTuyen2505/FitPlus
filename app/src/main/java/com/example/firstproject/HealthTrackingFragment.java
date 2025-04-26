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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private TextView textChartTitle, textChartStatus;
    private TextView textLatestWeight, textLatestHeight, textLatestHeartRate;
    private RadioGroup radioChartType;
    private RadioButton radioWeight, radioHeight, radioHeartRate;
    private FloatingActionButton fabAddMeasurement;
    private HealthMeasurementDAO healthMeasurementDAO;

    // Current selected chart type
    private String currentChartType = HealthMeasurement.TYPE_WEIGHT;

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

        // Initialize DAO
        healthMeasurementDAO = new HealthMeasurementDAO(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        healthMeasurementDAO.open();
        loadChartData();
        updateLatestMeasurements();
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
        textChartTitle = view.findViewById(R.id.text_chart_title);
        textChartStatus = view.findViewById(R.id.text_chart_status);
        textLatestWeight = view.findViewById(R.id.text_latest_weight);
        textLatestHeight = view.findViewById(R.id.text_latest_height);
        textLatestHeartRate = view.findViewById(R.id.text_latest_heart_rate);
        radioChartType = view.findViewById(R.id.radio_chart_type);
        radioWeight = view.findViewById(R.id.radio_weight);
        radioHeight = view.findViewById(R.id.radio_height);
        radioHeartRate = view.findViewById(R.id.radio_heart_rate);
        fabAddMeasurement = view.findViewById(R.id.fab_add_measurement);

        // Configure charts
        setupChart(chartWeight, "Cân nặng (kg)");
        setupChart(chartHeight, "Chiều cao (cm)");
        setupChart(chartHeartRate, "Nhịp tim (nhịp/phút)");

        // Set up radio group listener
        radioChartType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_weight) {
                showChart(HealthMeasurement.TYPE_WEIGHT);
            } else if (checkedId == R.id.radio_height) {
                showChart(HealthMeasurement.TYPE_HEIGHT);
            } else if (checkedId == R.id.radio_heart_rate) {
                showChart(HealthMeasurement.TYPE_HEART_RATE);
            }
        });

        // Set click listener for add measurement button
        fabAddMeasurement.setOnClickListener(v -> showAddMeasurementDialog());

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

        // Configure X axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new DateAxisValueFormatter());

        // Configure left Y axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);

        // Configure right Y axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Configure legend
        chart.getLegend().setEnabled(true);
        chart.getLegend().setTextColor(Color.BLACK);

        // Display "No data" when there's no data
        chart.setNoDataText("Không có dữ liệu");
        chart.setNoDataTextColor(Color.BLACK);
    }

    private void showChart(String chartType) {
        currentChartType = chartType;

        // Update chart title
        if (chartType.equals(HealthMeasurement.TYPE_WEIGHT)) {
            textChartTitle.setText("Biểu đồ cân nặng");
            chartWeight.setVisibility(View.VISIBLE);
            chartHeight.setVisibility(View.GONE);
            chartHeartRate.setVisibility(View.GONE);
        } else if (chartType.equals(HealthMeasurement.TYPE_HEIGHT)) {
            textChartTitle.setText("Biểu đồ chiều cao");
            chartWeight.setVisibility(View.GONE);
            chartHeight.setVisibility(View.VISIBLE);
            chartHeartRate.setVisibility(View.GONE);
        } else if (chartType.equals(HealthMeasurement.TYPE_HEART_RATE)) {
            textChartTitle.setText("Biểu đồ nhịp tim");
            chartWeight.setVisibility(View.GONE);
            chartHeight.setVisibility(View.GONE);
            chartHeartRate.setVisibility(View.VISIBLE);
        }

        // Update chart status
        updateChartStatus(chartType);

        // Load chart data
        loadChartData();
    }

    private void updateChartStatus(String chartType) {
        HealthMeasurement latestMeasurement = healthMeasurementDAO.getLatestMeasurementByType(chartType);

        if (latestMeasurement != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateStr = dateFormat.format(latestMeasurement.getDate());

            if (chartType.equals(HealthMeasurement.TYPE_WEIGHT)) {
                textChartStatus.setText(String.format(
                        "Cân nặng gần nhất: %.1f kg (%s)",
                        latestMeasurement.getValue(),
                        dateStr
                ));
            } else if (chartType.equals(HealthMeasurement.TYPE_HEIGHT)) {
                textChartStatus.setText(String.format(
                        "Chiều cao gần nhất: %.1f cm (%s)",
                        latestMeasurement.getValue(),
                        dateStr
                ));
            } else if (chartType.equals(HealthMeasurement.TYPE_HEART_RATE)) {
                textChartStatus.setText(String.format(
                        "Nhịp tim gần nhất: %.0f nhịp/phút (%s)\n%s",
                        latestMeasurement.getValue(),
                        dateStr,
                        latestMeasurement.getHeartRateStatus()
                ));

                if (latestMeasurement.isHeartRateNormal()) {
                    textChartStatus.setTextColor(getResources().getColor(R.color.accent_blue));
                } else {
                    textChartStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        } else {
            textChartStatus.setText("Trạng thái: Chưa có dữ liệu");
            textChartStatus.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void loadChartData() {
        List<HealthMeasurement> weightMeasurements = healthMeasurementDAO.getMeasurementsByType(HealthMeasurement.TYPE_WEIGHT);
        updateWeightChart(weightMeasurements);

        List<HealthMeasurement> heightMeasurements = healthMeasurementDAO.getMeasurementsByType(HealthMeasurement.TYPE_HEIGHT);
        updateHeightChart(heightMeasurements);

        List<HealthMeasurement> heartRateMeasurements = healthMeasurementDAO.getMeasurementsByType(HealthMeasurement.TYPE_HEART_RATE);
        updateHeartRateChart(heartRateMeasurements);
    }

    private void updateLatestMeasurements() {
        // Update latest weight
        HealthMeasurement latestWeight = healthMeasurementDAO.getLatestMeasurementByType(HealthMeasurement.TYPE_WEIGHT);
        if (latestWeight != null) {
            textLatestWeight.setText(String.format(Locale.getDefault(), "%.1f kg", latestWeight.getValue()));
        } else {
            textLatestWeight.setText("-- kg");
        }

        // Update latest height
        HealthMeasurement latestHeight = healthMeasurementDAO.getLatestMeasurementByType(HealthMeasurement.TYPE_HEIGHT);
        if (latestHeight != null) {
            textLatestHeight.setText(String.format(Locale.getDefault(), "%.1f cm", latestHeight.getValue()));
        } else {
            textLatestHeight.setText("-- cm");
        }

        // Update latest heart rate
        HealthMeasurement latestHeartRate = healthMeasurementDAO.getLatestMeasurementByType(HealthMeasurement.TYPE_HEART_RATE);
        if (latestHeartRate != null) {
            textLatestHeartRate.setText(String.format(Locale.getDefault(), "%.0f bpm", latestHeartRate.getValue()));
        } else {
            textLatestHeartRate.setText("-- bpm");
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

        final TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        final RadioGroup radioGroup = dialogView.findViewById(R.id.radio_measurement_type);
        final TextInputLayout layoutWeight = dialogView.findViewById(R.id.layout_weight);
        final TextInputLayout layoutHeight = dialogView.findViewById(R.id.layout_height);
        final TextInputLayout layoutHeartRate = dialogView.findViewById(R.id.layout_heart_rate);

        final EditText editWeight = dialogView.findViewById(R.id.edit_weight);
        final EditText editHeight = dialogView.findViewById(R.id.edit_height);
        final EditText editHeartRate = dialogView.findViewById(R.id.edit_heart_rate);

        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Set initial selection based on current chart type
        if (currentChartType.equals(HealthMeasurement.TYPE_WEIGHT)) {
            radioGroup.check(R.id.radio_weight);
            layoutWeight.setVisibility(View.VISIBLE);
            layoutHeight.setVisibility(View.GONE);
            layoutHeartRate.setVisibility(View.GONE);
            dialogTitle.setText("Thêm chỉ số cân nặng");
        } else if (currentChartType.equals(HealthMeasurement.TYPE_HEIGHT)) {
            radioGroup.check(R.id.radio_height);
            layoutWeight.setVisibility(View.GONE);
            layoutHeight.setVisibility(View.VISIBLE);
            layoutHeartRate.setVisibility(View.GONE);
            dialogTitle.setText("Thêm chỉ số chiều cao");
        } else if (currentChartType.equals(HealthMeasurement.TYPE_HEART_RATE)) {
            radioGroup.check(R.id.radio_heart_rate);
            layoutWeight.setVisibility(View.GONE);
            layoutHeight.setVisibility(View.GONE);
            layoutHeartRate.setVisibility(View.VISIBLE);
            dialogTitle.setText("Thêm chỉ số nhịp tim");
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                layoutWeight.setVisibility(View.GONE);
                layoutHeight.setVisibility(View.GONE);
                layoutHeartRate.setVisibility(View.GONE);

                if (checkedId == R.id.radio_weight) {
                    layoutWeight.setVisibility(View.VISIBLE);
                    dialogTitle.setText("Thêm chỉ số cân nặng");
                } else if (checkedId == R.id.radio_height) {
                    layoutHeight.setVisibility(View.VISIBLE);
                    dialogTitle.setText("Thêm chỉ số chiều cao");
                } else if (checkedId == R.id.radio_heart_rate) {
                    layoutHeartRate.setVisibility(View.VISIBLE);
                    dialogTitle.setText("Thêm chỉ số nhịp tim");
                }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save data based on selected measurement type
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
                updateLatestMeasurements();
                updateChartStatus(currentChartType);

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
