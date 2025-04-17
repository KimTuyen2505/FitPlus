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

    // RecyclerView để hiển thị danh sách danh mục (horizontal)
    private RecyclerView recyclerCategories;
    // RecyclerView để hiển thị danh sách lời khuyên (vertical)
    private RecyclerView recyclerSuggestions;
    // TextView hiển thị tên danh mục đang được chọn
    private TextView textSelectedCategory;

    // Adapter cho danh mục và lời khuyên
    private CategoryAdapter categoryAdapter;
    private SuggestionAdapter suggestionAdapter;

    // DAO để truy xuất dữ liệu danh mục và lời khuyên từ cơ sở dữ liệu
    private HealthSuggestionCategoryDAO categoryDAO;
    private HealthSuggestionDAO suggestionDAO;

    // Danh sách dữ liệu
    private List<HealthSuggestionCategory> categories;
    private List<HealthSuggestion> suggestions;

    /**
     * Interface để Activity chứa Fragment xử lý sự kiện chọn lời khuyên (nếu cần)
     */
    public interface HealthSuggestionsListener {
        void onSuggestionSelected(String suggestionId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout fragment_health_suggestions.xml
        View view = inflater.inflate(R.layout.fragment_health_suggestions, container, false);

        // Khởi tạo DAO với context của Fragment
        categoryDAO = new HealthSuggestionCategoryDAO(getContext());
        suggestionDAO = new HealthSuggestionDAO(getContext());

        // Ánh xạ các view từ layout
        recyclerCategories = view.findViewById(R.id.recycler_categories);
        recyclerSuggestions = view.findViewById(R.id.recycler_suggestions);
        textSelectedCategory = view.findViewById(R.id.text_selected_category);

        // Thiết lập LayoutManager cho RecyclerView danh mục (horizontal)
        recyclerCategories.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Thiết lập LayoutManager cho RecyclerView lời khuyên (vertical)
        recyclerSuggestions.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        // Tải dữ liệu danh mục và lời khuyên mặc định
        loadData();

        return view;
    }

    /**
     * Tải danh sách danh mục từ DB và khởi tạo adapter.
     * Sau đó, tự động tải lời khuyên của danh mục đầu tiên (nếu có).
     */
    private void loadData() {
        // Lấy tất cả danh mục từ DB
        categories = categoryDAO.getAll();
        // Tạo adapter cho danh mục, truyền this làm listener
        categoryAdapter = new CategoryAdapter(getContext(), categories, this);
        recyclerCategories.setAdapter(categoryAdapter);

        // Nếu có ít nhất một danh mục, hiển thị lời khuyên của danh mục đầu tiên
        if (!categories.isEmpty()) {
            HealthSuggestionCategory firstCategory = categories.get(0);
            loadSuggestionsByCategory(firstCategory);
        }
    }

    /**
     * Tải và hiển thị lời khuyên dựa trên danh mục được chọn.
     *
     * @param category Danh mục lời khuyên được chọn
     */
    private void loadSuggestionsByCategory(HealthSuggestionCategory category) {
        // Cập nhật tiêu đề danh mục đang chọn
        textSelectedCategory.setText(
                "Lời khuyên về " + category.getName().toLowerCase()
        );

        // Lấy danh sách lời khuyên theo categoryId từ DB
        suggestions = suggestionDAO.getByCategoryId(category.getId());

        if (suggestionAdapter == null) {
            // Nếu chưa có adapter, tạo mới và gán cho RecyclerView
            suggestionAdapter = new SuggestionAdapter(getContext(), suggestions);
            recyclerSuggestions.setAdapter(suggestionAdapter);
        } else {
            // Nếu đã có adapter, chỉ cần cập nhật dữ liệu và notify
            suggestionAdapter.updateSuggestions(suggestions);
        }
    }

    /**
     * Xử lý sự kiện khi người dùng click vào một danh mục.
     * Tải lại lời khuyên tương ứng.
     *
     * @param category Danh mục được click
     * @param position Vị trí trong danh sách
     */
    @Override
    public void onCategoryClick(HealthSuggestionCategory category, int position) {
        loadSuggestionsByCategory(category);
    }
}
