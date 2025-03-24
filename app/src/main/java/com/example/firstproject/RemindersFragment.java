package com.example.firstproject;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.dao.ReminderDAO;
import com.example.firstproject.models.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RemindersFragment extends Fragment {

    private RemindersListener listener;
    private RecyclerView recyclerReminders;
    private FloatingActionButton fabAddReminder;
    private List<Reminder> reminderList;
    private ReminderAdapter adapter;
    private ReminderDAO reminderDAO;

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

        // Initialize DAO
        reminderDAO = new ReminderDAO(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        reminderDAO.open();
        loadReminders();
    }

    @Override
    public void onPause() {
        super.onPause();
        reminderDAO.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        recyclerReminders = view.findViewById(R.id.recycler_reminders);
        fabAddReminder = view.findViewById(R.id.fab_add_reminder);

        recyclerReminders.setLayoutManager(new LinearLayoutManager(getContext()));

        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(reminderList);
        recyclerReminders.setAdapter(adapter);

        fabAddReminder.setOnClickListener(v -> showAddReminderDialog());

        return view;
    }

    private void loadReminders() {
        reminderList.clear();
        reminderList.addAll(reminderDAO.getAllReminders());
        adapter.notifyDataSetChanged();
    }

    private void showAddReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);
        builder.setView(dialogView);

        final EditText editTitle = dialogView.findViewById(R.id.edit_title);
        final EditText editTime = dialogView.findViewById(R.id.edit_time);
        final RadioGroup radioFrequency = dialogView.findViewById(R.id.radio_frequency);

        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Xử lý chọn thời gian
        editTime.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, selectedMinute) -> {
                        editTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, selectedMinute));
                    },
                    hour,
                    minute,
                    true
            );

            timePickerDialog.show();
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String time = editTime.getText().toString();

            // Lấy tần suất từ RadioGroup
            String frequency = "";
            int selectedId = radioFrequency.getCheckedRadioButtonId();

            if (selectedId == R.id.radio_daily) {
                frequency = Reminder.FREQUENCY_DAILY;
            }  else if (selectedId == R.id.radio_minute) {
                frequency = Reminder.FREQUENCY_MINUTE;
            }

            if (!title.isEmpty() && !time.isEmpty() && !frequency.isEmpty()) {
                Reminder reminder = new Reminder(title, time, frequency);
                long id = reminderDAO.insertReminder(reminder);
                reminder.setId(id);

                // Lên lịch thông báo
                reminder.scheduleNotification(getContext());

                reminderList.add(0, reminder);
                adapter.notifyItemInserted(0);
                recyclerReminders.scrollToPosition(0);

                if (listener != null) {
                    listener.onReminderAdded();
                }

                Toast.makeText(getContext(),
                        "Đã thêm nhắc nhở và lên lịch thông báo",
                        Toast.LENGTH_SHORT).show();
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

    public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

        private List<Reminder> reminders;

        public ReminderAdapter(List<Reminder> reminders) {
            this.reminders = reminders;
        }

        @NonNull
        @Override
        public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reminder, parent, false);
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
            private SwitchMaterial switchActive;

            public ReminderViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.text_title);
                textTime = itemView.findViewById(R.id.text_time);
                textFrequency = itemView.findViewById(R.id.text_frequency);
                switchActive = itemView.findViewById(R.id.switch_active);
            }

            public void bind(Reminder reminder) {
                textTitle.setText(reminder.getTitle());
                textTime.setText(reminder.getTime());
                textFrequency.setText(reminder.getFrequency());
                switchActive.setChecked(reminder.isActive());

                // Xử lý khi bật/tắt nhắc nhở
                switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    reminder.setActive(isChecked);
                    reminderDAO.updateReminder(reminder);

                    if (isChecked) {
                        reminder.scheduleNotification(getContext());
                        Toast.makeText(getContext(),
                                "Đã bật nhắc nhở",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        reminder.cancelNotification(getContext());
                        Toast.makeText(getContext(),
                                "Đã tắt nhắc nhở",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
