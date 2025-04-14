package com.example.firstproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.adapters.CategoryAdapter;
import com.example.firstproject.adapters.SuggestionAdapter;
import com.example.firstproject.dao.HealthSuggestionCategoryDAO;
import com.example.firstproject.dao.HealthSuggestionDAO;
import com.example.firstproject.models.HealthSuggestion;
import com.example.firstproject.models.HealthSuggestionCategory;

import java.util.List;

public class HealthSuggestionsFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerCategories;
    private RecyclerView recyclerSuggestions;
    private TextView textSelectedCategory;

    private CategoryAdapter categoryAdapter;
    private SuggestionAdapter suggestionAdapter;

    private HealthSuggestionCategoryDAO categoryDAO;
    private HealthSuggestionDAO suggestionDAO;

    private List<HealthSuggestionCategory> categories;
    private List<HealthSuggestion> suggestions;

    public interface HealthSuggestionsListener {
        void onSuggestionSelected(String suggestionId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_suggestions, container, false);

        // Khởi tạo DAO
        categoryDAO = new HealthSuggestionCategoryDAO(getContext());
        suggestionDAO = new HealthSuggestionDAO(getContext());

        // Ánh xạ view
        recyclerCategories = view.findViewById(R.id.recycler_categories);
        recyclerSuggestions = view.findViewById(R.id.recycler_suggestions);
        textSelectedCategory = view.findViewById(R.id.text_selected_category);

        // Thiết lập RecyclerView cho danh mục
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Thiết lập RecyclerView cho lời khuyên
        recyclerSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải dữ liệu
        loadData();

        return view;
    }

    private void loadData() {
        // Tải danh sách danh mục
        categories = categoryDAO.getAll();
        categoryAdapter = new CategoryAdapter(getContext(), categories, this);
        recyclerCategories.setAdapter(categoryAdapter);

        // Tải lời khuyên của danh mục đầu tiên
        if (!categories.isEmpty()) {
            HealthSuggestionCategory firstCategory = categories.get(0);
            loadSuggestionsByCategory(firstCategory);
        }
    }

    private void loadSuggestionsByCategory(HealthSuggestionCategory category) {
        // Cập nhật tiêu đề
        textSelectedCategory.setText("Lời khuyên về " + category.getName().toLowerCase());

        // Tải lời khuyên theo danh mục
        suggestions = suggestionDAO.getByCategoryId(category.getId());

        // Nếu adapter chưa được khởi tạo, tạo mới
        if (suggestionAdapter == null) {
            suggestionAdapter = new SuggestionAdapter(getContext(), suggestions);
            recyclerSuggestions.setAdapter(suggestionAdapter);
        } else {
            // Nếu đã có adapter, chỉ cập nhật dữ liệu
            suggestionAdapter.updateSuggestions(suggestions);
        }
    }

    @Override
    public void onCategoryClick(HealthSuggestionCategory category, int position) {
        // Khi người dùng chọn danh mục, tải lời khuyên tương ứng
        loadSuggestionsByCategory(category);
    }
}
