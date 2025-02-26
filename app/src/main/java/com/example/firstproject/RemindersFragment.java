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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.database.ReminderDAO;
import com.example.firstproject.models.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RemindersFragment extends Fragment {

    private RemindersListener listener;
    private RecyclerView recyclerReminders;
    private FloatingActionButton fabAddReminder;
    private ReminderDAO reminderDAO;
    private ReminderAdapter adapter;

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

        // Khởi tạo DAO
        reminderDAO = new ReminderDAO(getContext());
        reminderDAO.open();

        // Khởi tạo views
        recyclerReminders = view.findViewById(R.id.recycler_reminders);
        fabAddReminder = view.findViewById(R.id.fab_add_reminder);

        // Thiết lập RecyclerView
        recyclerReminders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải danh sách nhắc nhở
        loadReminders();

        // Đặt sự kiện click cho nút thêm nhắc nhở
        fabAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddReminderDialog();
            }
        });

        return view;
    }

    private void loadReminders() {
        // Lấy danh sách nhắc nhở từ cơ sở dữ liệu
        List<Reminder> reminders = reminderDAO.getAllReminders();

        // Nếu danh sách trống, thêm một số nhắc nhở mẫu
        if (reminders.isEmpty()) {
            // Thêm nhắc nhở mẫu vào cơ sở dữ liệu
            reminderDAO.insertReminder(new Reminder("Uống thuốc", "8:00 sáng", "Hàng ngày"));
            reminderDAO.insertReminder(new Reminder("Uống nước", "Mỗi 2 giờ", "Hàng ngày"));
            reminderDAO.insertReminder(new Reminder("Tập thể dục", "5:00 chiều", "Thứ Hai, Thứ Tư, Thứ Sáu"));

            // Tải lại danh sách
            reminders = reminderDAO.getAllReminders();
        }

        // Tạo adapter và gán cho RecyclerView
        adapter = new ReminderAdapter(reminders);
        recyclerReminders.setAdapter(adapter);
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
                String title = editTitle.getText().toString().trim();
                String time = editTime.getText().toString().trim();
                String frequency = editFrequency.getText().toString().trim();

                if (!title.isEmpty() && !time.isEmpty() && !frequency.isEmpty()) {
                    // Tạo đối tượng Reminder mới
                    Reminder reminder = new Reminder(title, time, frequency);

                    // Lưu vào cơ sở dữ liệu
                    long result = reminderDAO.insertReminder(reminder);

                    if (result > 0) {
                        // Cập nhật ID cho đối tượng Reminder
                        reminder.setId(result);

                        // Thêm vào adapter
                        adapter.addReminder(reminder);

                        // Thông báo cho activity
                        if (listener != null) {
                            listener.onReminderAdded();
                        }

                        Toast.makeText(getContext(), "Đã thêm nhắc nhở mới thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm nhắc nhở", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        // Đóng kết nối cơ sở dữ liệu
        if (reminderDAO != null) {
            reminderDAO.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Adapter cho RecyclerView
    public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

        private List<Reminder> reminders;

        public ReminderAdapter(List<Reminder> reminders) {
            this.reminders = reminders;
        }

        public void addReminder(Reminder reminder) {
            reminders.add(0, reminder); // Thêm vào đầu danh sách
            notifyItemInserted(0);
            recyclerReminders.scrollToPosition(0);
        }

        public void updateReminder(Reminder reminder) {
            for (int i = 0; i < reminders.size(); i++) {
                if (reminders.get(i).getId() == reminder.getId()) {
                    reminders.set(i, reminder);
                    notifyItemChanged(i);
                    break;
                }
            }
        }

        public void deleteReminder(long id) {
            for (int i = 0; i < reminders.size(); i++) {
                if (reminders.get(i).getId() == id) {
                    reminders.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
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

                // Đặt sự kiện click dài để xóa nhắc nhở
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            showDeleteReminderDialog(reminders.get(position));
                            return true;
                        }
                        return false;
                    }
                });
            }

            public void bind(Reminder reminder) {
                textTitle.setText(reminder.getTitle());
                textTime.setText(reminder.getTime());
                textFrequency.setText(reminder.getFrequency());
            }
        }
    }

    private void showDeleteReminderDialog(final Reminder reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa nhắc nhở");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhắc nhở này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xóa khỏi cơ sở dữ liệu
            int result = reminderDAO.deleteReminder(reminder.getId());
            if (result > 0) {
                // Xóa khỏi adapter
                adapter.deleteReminder(reminder.getId());
                Toast.makeText(getContext(), "Đã xóa nhắc nhở", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Lỗi khi xóa nhắc nhở", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

