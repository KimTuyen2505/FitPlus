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

public class HealthSuggestionsFragment extends Fragment {

    private HealthSuggestionsListener listener;
    private TextView textExerciseRecommendations, textNutritionAdvice;

    public interface HealthSuggestionsListener {
        void onSuggestionSelected(String suggestionId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HealthSuggestionsListener) {
            listener = (HealthSuggestionsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HealthSuggestionsListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_suggestions, container, false);

        // Initialize views
        textExerciseRecommendations = view.findViewById(R.id.text_exercise_recommendations);
        textNutritionAdvice = view.findViewById(R.id.text_nutrition_advice);

        // Load data
        loadExerciseRecommendations();
        loadNutritionAdvice();

        return view;
    }

    private void loadExerciseRecommendations() {
        // Trong ứng dụng thực tế, dữ liệu này sẽ được tải từ cơ sở dữ liệu hoặc API
        // Hiện tại, chúng ta chỉ sử dụng dữ liệu mẫu
        textExerciseRecommendations.setText("Dựa trên hồ sơ của bạn, chúng tôi khuyến nghị 30 phút tập thể dục cường độ vừa phải 3-4 lần mỗi tuần. Hãy cân nhắc các hoạt động như đi bộ nhanh, đạp xe hoặc bơi lội. Ngoài ra, hãy bổ sung các bài tập sức mạnh 2 lần mỗi tuần để duy trì khối lượng cơ và mật độ xương.");
    }

    private void loadNutritionAdvice() {
        // Trong ứng dụng thực tế, dữ liệu này sẽ được tải từ cơ sở dữ liệu hoặc API
        // Hiện tại, chúng ta chỉ sử dụng dữ liệu mẫu
        textNutritionAdvice.setText("Chế độ ăn hiện tại của bạn có thể được cải thiện bằng cách bổ sung thêm trái cây và rau quả. Hãy cố gắng ăn 5 khẩu phần mỗi ngày. Giảm tiêu thụ thực phẩm chế biến sẵn và tăng tiêu thụ ngũ cốc nguyên hạt, protein nạc và chất béo lành mạnh. Hãy uống đủ nước, ít nhất 8 ly mỗi ngày.");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}