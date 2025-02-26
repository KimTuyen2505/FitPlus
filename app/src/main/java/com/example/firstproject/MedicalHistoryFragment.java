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

import com.example.firstproject.database.MedicalRecordDAO;
import com.example.firstproject.models.MedicalRecord;

import java.util.List;

public class MedicalHistoryFragment extends Fragment {

    private MedicalHistoryListener listener;
    private RecyclerView recyclerMedicalHistory;
    private Button btnAddMedicalRecord;
    private MedicalRecordDAO medicalRecordDAO;
    private MedicalRecordAdapter adapter;

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

        // Khởi tạo DAO
        medicalRecordDAO = new MedicalRecordDAO(getContext());
        medicalRecordDAO.open();

        // Khởi tạo views
        recyclerMedicalHistory = view.findViewById(R.id.recycler_medical_history);
        btnAddMedicalRecord = view.findViewById(R.id.btn_add_medical_record);

        // Thiết lập RecyclerView
        recyclerMedicalHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải danh sách hồ sơ y tế
        loadMedicalRecords();

        // Đặt sự kiện click cho nút thêm hồ sơ y tế
        btnAddMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMedicalRecordDialog();
            }
        });

        return view;
    }

    private void loadMedicalRecords() {
        // Lấy danh sách hồ sơ y tế từ cơ sở dữ liệu
        List<MedicalRecord> records = medicalRecordDAO.getAllMedicalRecords();

        // Nếu danh sách trống, thêm một số hồ sơ mẫu
        if (records.isEmpty()) {
            // Thêm hồ sơ mẫu vào cơ sở dữ liệu
            medicalRecordDAO.insertMedicalRecord(new MedicalRecord("Khám sức khỏe định kỳ", "15/01/2023", "Bác sĩ Nguyễn", "Khám sức khỏe tổng quát. Tất cả các chỉ số đều bình thường."));
            medicalRecordDAO.insertMedicalRecord(new MedicalRecord("Tiêm vắc-xin cúm", "05/10/2022", "Bác sĩ Trần", "Tiêm vắc-xin cúm theo mùa."));
            medicalRecordDAO.insertMedicalRecord(new MedicalRecord("Khám răng", "20/08/2022", "Bác sĩ Lê", "Khám và vệ sinh răng định kỳ. Không phát hiện sâu răng."));

            // Tải lại danh sách
            records = medicalRecordDAO.getAllMedicalRecords();
        }

        // Tạo adapter và gán cho RecyclerView
        adapter = new MedicalRecordAdapter(records);
        recyclerMedicalHistory.setAdapter(adapter);
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
                String title = editTitle.getText().toString().trim();
                String date = editDate.getText().toString().trim();
                String doctor = editDoctor.getText().toString().trim();
                String notes = editNotes.getText().toString().trim();

                if (!title.isEmpty() && !date.isEmpty()) {
                    // Tạo đối tượng MedicalRecord mới
                    MedicalRecord record = new MedicalRecord(title, date, doctor, notes);

                    // Lưu vào cơ sở dữ liệu
                    long result = medicalRecordDAO.insertMedicalRecord(record);

                    if (result > 0) {
                        // Cập nhật ID cho đối tượng MedicalRecord
                        record.setId(result);

                        // Thêm vào adapter
                        adapter.addMedicalRecord(record);

                        // Thông báo cho activity
                        if (listener != null) {
                            listener.onMedicalRecordAdded();
                        }

                        Toast.makeText(getContext(), "Đã thêm hồ sơ y tế mới thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm hồ sơ y tế", Toast.LENGTH_SHORT).show();
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
        if (medicalRecordDAO != null) {
            medicalRecordDAO.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Adapter cho RecyclerView
    public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.MedicalRecordViewHolder> {

        private List<MedicalRecord> medicalRecords;

        public MedicalRecordAdapter(List<MedicalRecord> medicalRecords) {
            this.medicalRecords = medicalRecords;
        }

        public void addMedicalRecord(MedicalRecord record) {
            medicalRecords.add(0, record); // Thêm vào đầu danh sách
            notifyItemInserted(0);
            recyclerMedicalHistory.scrollToPosition(0);
        }

        public void updateMedicalRecord(MedicalRecord record) {
            for (int i = 0; i < medicalRecords.size(); i++) {
                if (medicalRecords.get(i).getId() == record.getId()) {
                    medicalRecords.set(i, record);
                    notifyItemChanged(i);
                    break;
                }
            }
        }

        public void deleteMedicalRecord(long id) {
            for (int i = 0; i < medicalRecords.size(); i++) {
                if (medicalRecords.get(i).getId() == id) {
                    medicalRecords.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }

        @NonNull
        @Override
        public MedicalRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_record, parent, false);
            return new MedicalRecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MedicalRecordViewHolder holder, int position) {
            MedicalRecord record = medicalRecords.get(position);
            holder.bind(record);
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

                // Đặt sự kiện click dài để xóa hồ sơ y tế
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            showDeleteMedicalRecordDialog(medicalRecords.get(position));
                            return true;
                        }
                        return false;
                    }
                });
            }

            public void bind(MedicalRecord record) {
                textTitle.setText(record.getTitle());
                textDate.setText(record.getDate());
                textDoctor.setText(record.getDoctor());
                textNotes.setText(record.getNotes());
            }
        }
    }

    private void showDeleteMedicalRecordDialog(final MedicalRecord record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa hồ sơ y tế");
        builder.setMessage("Bạn có chắc chắn muốn xóa hồ sơ y tế này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xóa khỏi cơ sở dữ liệu
            int result = medicalRecordDAO.deleteMedicalRecord(record.getId());
            if (result > 0) {
                // Xóa khỏi adapter
                adapter.deleteMedicalRecord(record.getId());
                Toast.makeText(getContext(), "Đã xóa hồ sơ y tế", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Lỗi khi xóa hồ sơ y tế", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

