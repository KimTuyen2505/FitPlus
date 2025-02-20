package com.example.firstproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class HealthTrackingFragment extends Fragment {

    private LineChart weightChart;
    private LineChart heightChart;
    private Button addMeasurementButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_tracking, container, false);

        FrameLayout weightChartContainer = view.findViewById(R.id.chart_weight_container);
        FrameLayout heightChartContainer = view.findViewById(R.id.chart_height_container);
        addMeasurementButton = view.findViewById(R.id.btn_add_measurement);

        weightChart = new LineChart(getContext());
        heightChart = new LineChart(getContext());

        weightChartContainer.addView(weightChart);
        heightChartContainer.addView(heightChart);

        setupCharts();
        loadChartData();

        addMeasurementButton.setOnClickListener(v -> {
            // TODO: Implement add measurement functionality
        });

        return view;
    }

    private void setupCharts() {
        setupChart(weightChart, "Weight Over Time");
        setupChart(heightChart, "Height Over Time");
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
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        chart.getAxisRight().setEnabled(false);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

        chart.getLegend().setEnabled(false);
    }

    private void loadChartData() {
        // This is dummy data. In a real app, you'd load this from a database or API
        List<Entry> weightEntries = new ArrayList<>();
        weightEntries.add(new Entry(0, 70f));
        weightEntries.add(new Entry(1, 71f));
        weightEntries.add(new Entry(2, 70.5f));
        weightEntries.add(new Entry(3, 72f));

        List<Entry> heightEntries = new ArrayList<>();
        heightEntries.add(new Entry(0, 170f));
        heightEntries.add(new Entry(1, 170f));
        heightEntries.add(new Entry(2, 170.5f));
        heightEntries.add(new Entry(3, 170.5f));

        setChartData(weightChart, weightEntries, "Weight", Color.BLUE);
        setChartData(heightChart, heightEntries, "Height", Color.RED);
    }

    private void setChartData(LineChart chart, List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh chart
    }
}