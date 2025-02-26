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

import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryFragment extends Fragment {

    private MedicalHistoryListener listener;
    private RecyclerView recyclerMedicalHistory;
    private Button btnAddMedicalRecord;
    private List<MedicalRecord> medicalRecordList;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_history, container, false);

        // Initialize views
        recyclerMedicalHistory = view.findViewById(R.id.recycler_medical_history);
        btnAddMedicalRecord = view.findViewById(R.id.btn_add_medical_record);

        // Set up RecyclerView
        recyclerMedicalHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize medical record list
        medicalRecordList = new ArrayList<>();

        // Add some dummy medical records
        medicalRecordList.add(new MedicalRecord("Khám sức khỏe định kỳ", "15/01/2023", "Bác sĩ Nguyễn", "Khám sức khỏe tổng quát. Tất cả các chỉ số đều bình thường."));
        medicalRecordList.add(new MedicalRecord("Tiêm vắc-xin cúm", "05/10/2022", "Bác sĩ Trần", "Tiêm vắc-xin cúm theo mùa."));
        medicalRecordList.add(new MedicalRecord("Khám răng", "20/08/2022", "Bác sĩ Lê", "Khám và vệ sinh răng định kỳ. Không phát hiện sâu răng."));

        // Set adapter
        MedicalRecordAdapter adapter = new MedicalRecordAdapter(medicalRecordList);
        recyclerMedicalHistory.setAdapter(adapter);

        // Set click listener for add medical record button
        btnAddMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMedicalRecordDialog();
            }
        });

        return view;
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String date = editDate.getText().toString();
                String doctor = editDoctor.getText().toString();
                String notes = editNotes.getText().toString();

                if (!title.isEmpty() && !date.isEmpty()) {
                    // Add new medical record to the list
                    medicalRecordList.add(new MedicalRecord(title, date, doctor, notes));

                    // Update the adapter
                    recyclerMedicalHistory.getAdapter().notifyItemInserted(medicalRecordList.size() - 1);

                    // Notify the listener
                    if (listener != null) {
                        listener.onMedicalRecordAdded();
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

    // Medical record model class
    public static class MedicalRecord {
        private String title;
        private String date;
        private String doctor;
        private String notes;

        public MedicalRecord(String title, String date, String doctor, String notes) {
            this.title = title;
            this.date = date;
            this.doctor = doctor;
            this.notes = notes;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }

        public String getDoctor() {
            return doctor;
        }

        public String getNotes() {
            return notes;
        }
    }

    // Medical record adapter class
    public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.MedicalRecordViewHolder> {

        private List<MedicalRecord> medicalRecords;

        public MedicalRecordAdapter(List<MedicalRecord> medicalRecords) {
            this.medicalRecords = medicalRecords;
        }

        @NonNull
        @Override
        public MedicalRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_record, parent, false);
            return new MedicalRecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MedicalRecordViewHolder holder, int position) {
            MedicalRecord medicalRecord = medicalRecords.get(position);
            holder.bind(medicalRecord);
        }

        @Override
        public int getItemCount() {
            return medicalRecords.size();
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

            public void bind(MedicalRecord medicalRecord) {
                textTitle.setText(medicalRecord.getTitle());
                textDate.setText(medicalRecord.getDate());
                textDoctor.setText(medicalRecord.getDoctor());
                textNotes.setText(medicalRecord.getNotes());
            }
        }
    }
}
