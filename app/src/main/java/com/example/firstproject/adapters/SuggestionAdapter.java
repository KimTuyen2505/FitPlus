package com.example.firstproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.models.HealthSuggestion;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private List<HealthSuggestion> suggestions;
    private Context context;

    public SuggestionAdapter(Context context, List<HealthSuggestion> suggestions) {
        this.context = context;
        this.suggestions = suggestions;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        HealthSuggestion suggestion = suggestions.get(position);
        holder.textTitle.setText(suggestion.getTitle());
        holder.textContent.setText(suggestion.getContent());
        holder.textSource.setText("Nguồn: " + suggestion.getSource());
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    public void updateSuggestions(List<HealthSuggestion> newSuggestions) {
        this.suggestions = newSuggestions;
        notifyDataSetChanged();
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textContent;
        TextView textSource;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_suggestion_title);
            textContent = itemView.findViewById(R.id.text_suggestion_content);
            textSource = itemView.findViewById(R.id.text_suggestion_source);
        }
    }
}
