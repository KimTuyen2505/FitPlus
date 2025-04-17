package com.example.firstproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.dao.MedicalRecordDAO;
import com.example.firstproject.models.MedicalRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicalHistoryFragment extends Fragment {

    private MedicalHistoryListener listener;
    private RecyclerView recyclerMedicalHistory;
    private Button btnAddMedicalRecord;
    private EditText editSearch;
    private ImageButton btnClearSearch;
    private TextView textSearchResults;
    private List<MedicalRecord> medicalRecordList;
    private List<MedicalRecord> filteredRecordList;
    private MedicalRecordAdapter adapter;
    private MedicalRecordDAO medicalRecordDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Thêm các biến mới để lọc theo ngày
    private EditText editStartDate, editEndDate;
    private Button btnFilter, btnClearFilter, btnToggleFilter;
    private LinearLayout layoutDateFilter;
    private boolean isFilterExpanded = false;
    private Date startDate = null;
    private Date endDate = null;

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
        editSearch = view.findViewById(R.id.edit_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);
        textSearchResults = view.findViewById(R.id.text_search_results);

        // Ánh xạ các thành phần lọc theo ngày
        layoutDateFilter = view.findViewById(R.id.layout_date_filter);
        editStartDate = view.findViewById(R.id.edit_start_date);
        editEndDate = view.findViewById(R.id.edit_end_date);
        btnFilter = view.findViewById(R.id.btn_apply_filter);
        btnClearFilter = view.findViewById(R.id.btn_clear_filter);
        btnToggleFilter = view.findViewById(R.id.btn_toggle_filter);

        recyclerMedicalHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        medicalRecordList = new ArrayList<>();
        filteredRecordList = new ArrayList<>();
        adapter = new MedicalRecordAdapter(filteredRecordList);
        recyclerMedicalHistory.setAdapter(adapter);

        btnAddMedicalRecord.setOnClickListener(v -> showAddMedicalRecordDialog());

        // Thiết lập sự kiện cho nút hiển thị/ẩn bộ lọc
        btnToggleFilter.setOnClickListener(v -> {
            isFilterExpanded = !isFilterExpanded;
            layoutDateFilter.setVisibility(isFilterExpanded ? View.VISIBLE : View.GONE);
            btnToggleFilter.setText(isFilterExpanded ? "Ẩn bộ lọc ngày" : "Hiện bộ lọc ngày");
        });

        // Thiết lập sự kiện chọn ngày
        editStartDate.setOnClickListener(v -> showDatePickerDialog(editStartDate, true));
        editEndDate.setOnClickListener(v -> showDatePickerDialog(editEndDate, false));

        // Thiết lập sự kiện áp dụng bộ lọc
        btnFilter.setOnClickListener(v -> {
            String keyword = editSearch.getText().toString().trim().toLowerCase();
            filterRecords(keyword);
            Toast.makeText(getContext(), "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
        });

        // Thiết lập sự kiện xóa bộ lọc
        btnClearFilter.setOnClickListener(v -> {
            editStartDate.setText("");
            editEndDate.setText("");
            startDate = null;
            endDate = null;
            String keyword = editSearch.getText().toString().trim().toLowerCase();
            filterRecords(keyword);
            Toast.makeText(getContext(), "Đã xóa bộ lọc ngày", Toast.LENGTH_SHORT).show();
        });

        setupSearch();

        return view;
    }

    // Thêm phương thức hiển thị DatePickerDialog
    private void showDatePickerDialog(EditText dateField, boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Nếu đã có ngày nhập, sử dụng ngày đó làm giá trị khởi tạo
        String currentDate = dateField.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                Date date = dateFormat.parse(currentDate);
                calendar.setTime(date);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    dateField.setText(dateFormat.format(calendar.getTime()));

                    // Lưu ngày đã chọn
                    try {
                        if (isStartDate) {
                            startDate = dateFormat.parse(dateField.getText().toString());
                        } else {
                            endDate = dateFormat.parse(dateField.getText().toString());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Sửa phương thức filterRecords để hỗ trợ lọc theo ngày
    private void filterRecords(String keyword) {
        filteredRecordList.clear();

        // Lọc theo từ khóa và ngày
        for (MedicalRecord record : medicalRecordList) {
            boolean matchesKeyword = keyword.isEmpty() ||
                    record.getTitle().toLowerCase().contains(keyword) ||
                    (record.getDescription() != null && record.getDescription().toLowerCase().contains(keyword)) ||
                    (record.getDoctor() != null && record.getDoctor().toLowerCase().contains(keyword)) ||
                    (record.getHospital() != null && record.getHospital().toLowerCase().contains(keyword));

            boolean matchesDateRange = true;

            // Kiểm tra ngày bắt đầu
            if (startDate != null && record.getDate() != null) {
                Calendar recordDate = Calendar.getInstance();
                recordDate.setTime(record.getDate());
                recordDate.set(Calendar.HOUR_OF_DAY, 0);
                recordDate.set(Calendar.MINUTE, 0);
                recordDate.set(Calendar.SECOND, 0);
                recordDate.set(Calendar.MILLISECOND, 0);

                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);
                startCal.set(Calendar.HOUR_OF_DAY, 0);
                startCal.set(Calendar.MINUTE, 0);
                startCal.set(Calendar.SECOND, 0);
                startCal.set(Calendar.MILLISECOND, 0);

                if (recordDate.getTime().before(startCal.getTime())) {
                    matchesDateRange = false;
                }
            }

            // Kiểm tra ngày kết thúc
            if (endDate != null && record.getDate() != null) {
                Calendar recordDate = Calendar.getInstance();
                recordDate.setTime(record.getDate());
                recordDate.set(Calendar.HOUR_OF_DAY, 23);
                recordDate.set(Calendar.MINUTE, 59);
                recordDate.set(Calendar.SECOND, 59);

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);
                endCal.set(Calendar.HOUR_OF_DAY, 23);
                endCal.set(Calendar.MINUTE, 59);
                endCal.set(Calendar.SECOND, 59);

                if (recordDate.getTime().after(endCal.getTime())) {
                    matchesDateRange = false;
                }
            }

            if (matchesKeyword && matchesDateRange) {
                filteredRecordList.add(record);
            }
        }

        // Cập nhật thông báo kết quả tìm kiếm
        updateSearchResultsText(keyword);

        adapter.notifyDataSetChanged();
    }

    // Thêm phương thức cập nhật thông báo kết quả tìm kiếm
    private void updateSearchResultsText(String keyword) {
        if (!keyword.isEmpty() || startDate != null || endDate != null) {
            StringBuilder filterText = new StringBuilder("Tìm thấy " + filteredRecordList.size() + " kết quả");

            if (!keyword.isEmpty()) {
                filterText.append(" cho '").append(keyword).append("'");
            }

            if (startDate != null || endDate != null) {
                filterText.append(" trong khoảng thời gian");
                if (startDate != null) {
                    filterText.append(" từ ").append(dateFormat.format(startDate));
                }
                if (endDate != null) {
                    filterText.append(" đến ").append(dateFormat.format(endDate));
                }
            }

            textSearchResults.setText(filterText.toString());
            textSearchResults.setVisibility(View.VISIBLE);
        } else {
            textSearchResults.setVisibility(View.GONE);
        }

        btnClearSearch.setVisibility(!keyword.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Sửa phương thức loadMedicalRecords để giữ nguyên bộ lọc khi tải lại dữ liệu
    private void loadMedicalRecords() {
        medicalRecordList.clear();
        medicalRecordList.addAll(medicalRecordDAO.getAllMedicalRecords());

        String currentKeyword = editSearch.getText().toString().trim().toLowerCase();
        filterRecords(currentKeyword);
    }

    // Sửa phương thức setupSearch để cập nhật lọc theo ngày khi tìm kiếm
    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim().toLowerCase();
                filterRecords(keyword);
            }
        });

        btnClearSearch.setOnClickListener(v -> {
            editSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
            filterRecords("");
        });
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
        editDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        editDate.setText(dateFormat.format(calendar.getTime()));
                    }, year, month, day);
            datePickerDialog.show();
        });
        editDate.setText(dateFormat.format(new Date()));
        final AlertDialog dialog = builder.create();
        dialog.show();
        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String dateStr = editDate.getText().toString();
            String doctor = editDoctor.getText().toString();
            String notes = editNotes.getText().toString();
            Date date = null;
            try {
                date = dateFormat.parse(dateStr);
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Thời gian không hợp lệ. Vui lòng sử dụng định dạng DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!title.isEmpty() && date != null) {
                MedicalRecord record = new MedicalRecord(title, notes, date, doctor, "");
                long id = medicalRecordDAO.insertMedicalRecord(record);
                record.setId(id);

                medicalRecordList.add(0, record);

                String currentKeyword = editSearch.getText().toString().trim().toLowerCase();
                filterRecords(currentKeyword);

                recyclerMedicalHistory.scrollToPosition(0);

                if (listener != null) {
                    listener.onMedicalRecordAdded();
                }
                Toast.makeText(getContext(), "Đã thêm hồ sơ y tế", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(),
                        "Vui lòng nhập tiêu đề và ngày",
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
                textDate.setText(dateFormat.format(record.getDate()));
                textDoctor.setText(record.getDoctor());
                textNotes.setText(record.getDescription());
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

                    String currentKeyword = editSearch.getText().toString().trim().toLowerCase();
                    filterRecords(currentKeyword);

                    Toast.makeText(getContext(), "Đã xóa hồ sơ", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
