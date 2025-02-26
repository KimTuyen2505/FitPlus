package com.example.firstproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RemindersFragment extends Fragment {

    private RemindersListener listener;
    private RecyclerView recyclerReminders;
    private FloatingActionButton fabAddReminder;
    private List<Reminder> reminderList;

    public interface RemindersListener {
        void onReminderAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RemindersListener) {
            listener = (RemindersListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RemindersListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        // Initialize views
        recyclerReminders = view.findViewById(R.id.recycler_reminders);
        fabAddReminder = view.findViewById(R.id.fab_add_reminder);

        // Set up RecyclerView
        recyclerReminders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize reminder list
        reminderList = new ArrayList<>();

        // Add some dummy reminders
        reminderList.add(new Reminder("Uống thuốc", "8:00 sáng", "Hàng ngày"));
        reminderList.add(new Reminder("Uống nước", "Mỗi 2 giờ", "Hàng ngày"));
        reminderList.add(new Reminder("Tập thể dục", "5:00 chiều", "Thứ Hai, Thứ Tư, Thứ Sáu"));

        // Set adapter
        ReminderAdapter adapter = new ReminderAdapter(reminderList);
        recyclerReminders.setAdapter(adapter);

        // Set click listener for add reminder button
        fabAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddReminderDialog();
            }
        });

        return view;
    }

    private void showAddReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);
        builder.setView(dialogView);

        final EditText editTitle = dialogView.findViewById(R.id.edit_title);
        final EditText editTime = dialogView.findViewById(R.id.edit_time);
        final EditText editFrequency = dialogView.findViewById(R.id.edit_frequency);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String time = editTime.getText().toString();
                String frequency = editFrequency.getText().toString();

                if (!title.isEmpty() && !time.isEmpty() && !frequency.isEmpty()) {
                    // Add new reminder to the list
                    reminderList.add(new Reminder(title, time, frequency));

                    // Update the adapter
                    recyclerReminders.getAdapter().notifyItemInserted(reminderList.size() - 1);

                    // Notify the listener
                    if (listener != null) {
                        listener.onReminderAdded();
                    }
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

    // Reminder model class
    public static class Reminder {
        private String title;
        private String time;
        private String frequency;

        public Reminder(String title, String time, String frequency) {
            this.title = title;
            this.time = time;
            this.frequency = frequency;
        }

        public String getTitle() {
            return title;
        }

        public String getTime() {
            return time;
        }

        public String getFrequency() {
            return frequency;
        }
    }

    // Reminder adapter class
    public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

        private List<Reminder> reminders;

        public ReminderAdapter(List<Reminder> reminders) {
            this.reminders = reminders;
        }

        @NonNull
        @Override
        public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
            return new ReminderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
            Reminder reminder = reminders.get(position);
            holder.bind(reminder);
        }

        @Override
        public int getItemCount() {
            return reminders.size();
        }

        class ReminderViewHolder extends RecyclerView.ViewHolder {

            private TextView textTitle, textTime, textFrequency;

            public ReminderViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.text_title);
                textTime = itemView.findViewById(R.id.text_time);
                textFrequency = itemView.findViewById(R.id.text_frequency);
            }

            public void bind(Reminder reminder) {
                textTitle.setText(reminder.getTitle());
                textTime.setText(reminder.getTime());
                textFrequency.setText(reminder.getFrequency());
            }
        }
    }
}

