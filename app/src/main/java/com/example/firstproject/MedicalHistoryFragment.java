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
        medicalRecordList.addAll(medicalRecordDAO.getAllRecords());
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

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String date = editDate.getText().toString();
            String doctor = editDoctor.getText().toString();
            String notes = editNotes.getText().toString();

            if (!title.isEmpty() && !date.isEmpty()) {
                MedicalRecord record = new MedicalRecord(title, date, doctor, notes);
                long id = medicalRecordDAO.insertRecord(record);
                record.setId(id);

                medicalRecordList.add(0, record);
                adapter.notifyItemInserted(0);
                recyclerMedicalHistory.scrollToPosition(0);

                if (listener != null) {
                    listener.onMedicalRecordAdded();
                }
            } else {
                Toast.makeText(getContext(),
                        "Vui lòng nhập tiêu đề và ngày",
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
                textDate.setText(record.getDate());
                textDoctor.setText(record.getDoctor());
                textNotes.setText(record.getNotes());
            }
        }
    }

    private void showDeleteDialog(MedicalRecord record) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa hồ sơ y tế")
                .setMessage("Bạn có chắc muốn xóa hồ sơ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    medicalRecordDAO.deleteRecord(record.getId());
                    medicalRecordList.remove(record);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Đã xóa hồ sơ", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

