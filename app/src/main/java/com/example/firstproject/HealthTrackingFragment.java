package com.example.firstproject;

import android.app.AlertDialog;
import android.content.Context;
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

import com.example.firstproject.R;
import com.example.firstproject.dao.HealthMeasurementDAO;
import com.example.firstproject.models.HealthMeasurement;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthTrackingFragment extends Fragment {

    private HealthTrackingListener listener;
    private FrameLayout chartWeightContainer, chartHeightContainer;
    private Button btnAddMeasurement;
    private HealthMeasurementDAO measurementDAO;
    private LineChart chartWeight, chartHeight;
    private SimpleDateFormat dateFormat;

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
        measurementDAO = new HealthMeasurementDAO(context);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public void onResume() {
        super.onResume();
        measurementDAO.open();
        loadChartData();
    }

    @Override
    public void onPause() {
        super.onPause();
        measurementDAO.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_tracking, container, false);

        chartWeightContainer = view.findViewById(R.id.chart_weight_container);
        chartHeightContainer = view.findViewById(R.id.chart_height_container);
        btnAddMeasurement = view.findViewById(R.id.btn_add_measurement);

        setupCharts();

        btnAddMeasurement.setOnClickListener(v -> showAddMeasurementDialog());

        return view;
    }

    private void setupCharts() {
        // Setup Weight Chart
        chartWeight = new LineChart(getContext());
        chartWeightContainer.addView(chartWeight);
        setupChart(chartWeight, "Cân nặng (kg)");

        // Setup Height Chart
        chartHeight = new LineChart(getContext());
        chartHeightContainer.addView(chartHeight);
        setupChart(chartHeight, "Chiều cao (cm)");
    }

    private void setupChart(LineChart chart, String label) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter());

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
    }

    private void loadChartData() {
        // Load Weight Data
        List<HealthMeasurement> weightMeasurements = measurementDAO.getMeasurementsByType("weight");
        updateChart(chartWeight, weightMeasurements, "Cân nặng");

        // Load Height Data
        List<HealthMeasurement> heightMeasurements = measurementDAO.getMeasurementsByType("height");
        updateChart(chartHeight, heightMeasurements, "Chiều cao");
    }

    private void updateChart(LineChart chart, List<HealthMeasurement> measurements, String label) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        for (int i = 0; i < measurements.size(); i++) {
            HealthMeasurement measurement = measurements.get(i);
            entries.add(new Entry(i, measurement.getMeasurementValue()));
            dates.add(measurement.getMeasurementDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(getResources().getColor(R.color.accent_blue));
        dataSet.setCircleColor(getResources().getColor(R.color.accent_blue));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

        chart.invalidate();
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

        btnSave.setOnClickListener(v -> {
            String weightStr = editWeight.getText().toString();
            String heightStr = editHeight.getText().toString();
            String currentDate = dateFormat.format(new Date());

            if (!weightStr.isEmpty()) {
                try {
                    float weight = Float.parseFloat(weightStr);
                    HealthMeasurement weightMeasurement = new HealthMeasurement(
                            "weight", weight, currentDate);
                    measurementDAO.insertMeasurement(weightMeasurement);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Cân nặng không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            if (!heightStr.isEmpty()) {
                try {
                    float height = Float.parseFloat(heightStr);
                    HealthMeasurement heightMeasurement = new HealthMeasurement(
                            "height", height, currentDate);
                    measurementDAO.insertMeasurement(heightMeasurement);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Chiều cao không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            loadChartData();

            if (listener != null) {
                listener.onMeasurementAdded();
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

