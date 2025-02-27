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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HealthTrackingFragment extends Fragment {

    private HealthTrackingListener listener;
    private FrameLayout chartWeightContainer, chartHeightContainer;
    private Button btnAddMeasurement;

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

        // Initialize views
        chartWeightContainer = view.findViewById(R.id.chart_weight_container);
        chartHeightContainer = view.findViewById(R.id.chart_height_container);
        btnAddMeasurement = view.findViewById(R.id.btn_add_measurement);

        // Set click listener for add measurement button
        btnAddMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMeasurementDialog();
            }
        });

        // Load chart data (in a real app, this would come from a database)
        loadChartData();

        return view;
    }

    private void loadChartData() {
        View weightChartPlaceholder = new View(getContext());
        weightChartPlaceholder.setBackgroundColor(getResources().getColor(R.color.accent_blue));
        chartWeightContainer.addView(weightChartPlaceholder);

        View heightChartPlaceholder = new View(getContext());
        heightChartPlaceholder.setBackgroundColor(getResources().getColor(R.color.accent_blue));
        chartHeightContainer.addView(heightChartPlaceholder);
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
                // In a real app, this would save data to a database
                // For now, we'll just notify the listener
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

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}


