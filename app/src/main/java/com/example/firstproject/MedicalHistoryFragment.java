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

import com.example.firstproject.R;
import com.example.firstproject.dao.MedicalRecordDAO;
import com.example.firstproject.models.MedicalRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicalHistoryFragment extends Fragment {

    private MedicalHistoryListener listener;
    private RecyclerView recyclerMedicalHistory;
    private Button btnAddMedicalRecord;
    private List<MedicalRecord> medicalRecordList;
    private MedicalRecordAdapter adapter;
    private MedicalRecordDAO medicalRecordDAO;

    public interface MedicalHistoryListener {
        void onMedicalRecordAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MedicalHistoryListener) {
            listener = (MedicalHistoryListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MedicalHistoryListener");
        }
        medicalRecordDAO = new MedicalRecordDAO(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        medicalRecordDAO.open();
        loadMedicalRecords();
    }

    @Override
    public void onPause() {
        super.onPause();
        medicalRecordDAO.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history, container, false);

        recyclerMedicalHistory = view.findViewById(R.id.recycler_medical_history);
        btnAddMedicalRecord = view.findViewById(R.id.btn_add_medical_record);

        recyclerMedicalHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        medicalRecordList = new ArrayList<>();
        adapter = new MedicalRecordAdapter(medicalRecordList);
        recyclerMedicalHistory.setAdapter(adapter);

        btnAddMedicalRecord.setOnClickListener(v -> showAddMedicalRecordDialog());

        return view;
    }

    private void loadMedicalRecords() {
        medicalRecordList.clear();
        medicalRecordList.addAll(medicalRecordDAO.getAllMedicalRecords());
        adapter.notifyDataSetChanged();
    }

    private void showAddMedicalRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medical_record, null);
        builder.setView(dialogView);

        final EditText editTitle = dialogView.findViewById(R.id.edit_title);
        final EditText editDate = dialogView.findViewById(R.id.edit_date);
        final EditText editDoctor = dialogView.findViewById(R.id.edit_doctor);
        final EditText editNotes = dialogView.findViewById(R.id.edit_notes);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Gợi ý định dạng ngày tháng cho người dùng
        editDate.setHint("Nhập ngày (dd/MM/yyyy)");

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String doctor = editDoctor.getText().toString();
            String notes = editNotes.getText().toString();
            Date date = new Date(); // Mặc định là ngày hiện tại

            // Xử lý chuỗi ngày tháng
            String dateStr = editDate.getText().toString();
            if (!dateStr.isEmpty()) {
                try {
                    // Định dạng ngày tháng kiểu Việt Nam: ngày/tháng/năm
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                    sdf.setLenient(false); // Kiểm tra nghiêm ngặt định dạng ngày
                    date = sdf.parse(dateStr);
                    if (date == null) {
                        date = new Date(); // Sử dụng ngày hiện tại nếu không phân tích được
                    }
                } catch (java.text.ParseException e) {
                    Toast.makeText(getContext(), "Ngày tháng không hợp lệ. Vui lòng nhập theo định dạng ngày/tháng/năm (VD: 31/12/2023)", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (!title.isEmpty()) {
                MedicalRecord record = new MedicalRecord(title, notes, date, doctor);
                long id = medicalRecordDAO.insertMedicalRecord(record);
                record.setId(id);

                medicalRecordList.add(0, record);
                adapter.notifyItemInserted(0);
                recyclerMedicalHistory.scrollToPosition(0);

                if (listener != null) {
                    listener.onMedicalRecordAdded();
                }

                Toast.makeText(getContext(), "Đã lưu hồ sơ y tế thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(),
                        "Vui lòng nhập tiêu đề cho hồ sơ y tế",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.MedicalRecordViewHolder> {

        private List<MedicalRecord> records;

        public MedicalRecordAdapter(List<MedicalRecord> records) {
            this.records = records;
        }

        @NonNull
        @Override
        public MedicalRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medical_record, parent, false);
            return new MedicalRecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MedicalRecordViewHolder holder, int position) {
            MedicalRecord record = records.get(position);
            holder.bind(record);

            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(record);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        class MedicalRecordViewHolder extends RecyclerView.ViewHolder {

            private TextView textTitle, textDate, textDoctor, textNotes;

            public MedicalRecordViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.text_title);
                textDate = itemView.findViewById(R.id.text_date);
                textDoctor = itemView.findViewById(R.id.text_doctor);
                textNotes = itemView.findViewById(R.id.text_notes);
            }

            public void bind(MedicalRecord record) {
                textTitle.setText(record.getTitle());

                // Chỉ hiển thị ngày tháng năm
                String formattedDate = formatDateVietnamese(record.getDate());
                textDate.setText(formattedDate);

                textDoctor.setText(record.getDoctor());
                textNotes.setText(record.getDescription());
            }

            // Phương thức định dạng chỉ hiển thị ngày tháng năm
            private String formatDateVietnamese(Date date) {
                if (date == null) return "";

                // Định dạng ngày/tháng/năm
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                return dateFormat.format(date);
            }
        }
    }

    private void showDeleteDialog(MedicalRecord record) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa hồ sơ y tế")
                .setMessage("Bạn có chắc muốn xóa hồ sơ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    medicalRecordDAO.deleteMedicalRecord(record.getId());
                    medicalRecordList.remove(record);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Đã xóa hồ sơ", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

