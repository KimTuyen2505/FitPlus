package com.example.firstproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.models.HealthSuggestionCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<HealthSuggestionCategory> categories;
    private Context context;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0; // Mặc định chọn danh mục đầu tiên

    public interface OnCategoryClickListener {
        void onCategoryClick(HealthSuggestionCategory category, int position);
    }

    public CategoryAdapter(Context context, List<HealthSuggestionCategory> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;

        // Đánh dấu danh mục đầu tiên là đã chọn
        if (categories != null && !categories.isEmpty()) {
            categories.get(0).setSelected(true);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        HealthSuggestionCategory category = categories.get(position);
        holder.textCategoryName.setText(category.getName());

        // Thiết lập icon dựa trên tên icon
        int iconResId = getIconResourceId(category.getIconName());
        holder.imageCategoryIcon.setImageResource(iconResId);

        // Thiết lập màu nền và icon dựa trên trạng thái chọn
        if (position == selectedPosition) {
            holder.layoutCategory.setBackgroundColor(category.getColor());
            holder.textCategoryName.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.imageCategoryIcon.setColorFilter(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.layoutCategory.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.textCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            holder.imageCategoryIcon.setColorFilter(category.getColor());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // Cập nhật trạng thái chọn
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Thông báo thay đổi để cập nhật giao diện
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                listener.onCategoryClick(category, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textCategoryName;
        ImageView imageCategoryIcon;
        LinearLayout layoutCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoryName = itemView.findViewById(R.id.text_category_name);
            imageCategoryIcon = itemView.findViewById(R.id.image_category_icon);
            layoutCategory = itemView.findViewById(R.id.layout_category);
        }
    }

    // Phương thức để lấy ID tài nguyên icon dựa trên tên
    private int getIconResourceId(String iconName) {
        switch (iconName) {
            case "ic_heart":
                return R.drawable.ic_heart;
            case "ic_liver":
                return R.drawable.ic_liver;
            case "ic_kidney":
                return R.drawable.ic_kidney;
            case "ic_diabetes":
                return R.drawable.ic_diabetes;
            case "ic_nutrition":
                return R.drawable.ic_nutrition;
            case "ic_exercise":
                return R.drawable.ic_exercise;
            default:
                return R.drawable.ic_health; // Icon mặc định
        }
    }
}
