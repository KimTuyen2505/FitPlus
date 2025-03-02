package com.example.firstproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AnalysisAlertsFragment extends Fragment {

    private AnalysisAlertsListener listener;
    private TextView textHealthAlerts, textHealthAnalysis;

    public interface AnalysisAlertsListener {
        void onAlertDismissed(String alertId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AnalysisAlertsListener) {
            listener = (AnalysisAlertsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AnalysisAlertsListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis_alerts, container, false);

        textHealthAlerts = view.findViewById(R.id.text_health_alerts);
        textHealthAnalysis = view.findViewById(R.id.text_health_analysis);

        loadHealthAlerts();
        loadHealthAnalysis();

        return view;
    }

    private void loadHealthAlerts() {

        textHealthAlerts.setText("Cảnh báo: Chỉ số huyết áp của bạn cao hơn bình thường trong tuần qua. Hãy cân nhắc việc tham khảo ý kiến bác sĩ.");
    }

    private void loadHealthAnalysis() {
        textHealthAnalysis.setText("Dựa trên dữ liệu sức khỏe gần đây của bạn, điểm sức khỏe tổng thể của bạn là 85/100. Chỉ số BMI của bạn nằm trong phạm vi bình thường, nhưng mô hình giấc ngủ cho thấy một số bất thường. Hãy cân nhắc cải thiện lịch trình ngủ để có kết quả sức khỏe tốt hơn.");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

