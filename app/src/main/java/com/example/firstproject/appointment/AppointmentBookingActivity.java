package com.example.firstproject.appointment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.R;
import com.example.firstproject.appointment.adapters.HospitalAdapter;
import com.example.firstproject.appointment.adapters.TimeSlotAdapter;
import com.example.firstproject.appointment.models.Hospital;
import com.example.firstproject.appointment.models.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentBookingActivity extends AppCompatActivity implements TimeSlotAdapter.OnTimeSlotClickListener {

    private Toolbar toolbar;
    private Spinner spinnerSpecialty;
    private EditText editDate;
    private RecyclerView recyclerHospitals;
    private RecyclerView recyclerTimeSlots;
    private TextView textSelectedHospital;
    private TextView textNoTimeSlot;
    private Button btnBookAppointment;

    private HospitalAdapter hospitalAdapter;
    private TimeSlotAdapter timeSlotAdapter;
    private List<Hospital> hospitals;
    private List<TimeSlot> timeSlots;

    private Hospital selectedHospital;
    private TimeSlot selectedTimeSlot;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đặt lịch khám");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        spinnerSpecialty = findViewById(R.id.spinner_specialty);
        editDate = findViewById(R.id.edit_date);
        recyclerHospitals = findViewById(R.id.recycler_hospitals);
        recyclerTimeSlots = findViewById(R.id.recycler_time_slots);
        textSelectedHospital = findViewById(R.id.text_selected_hospital);
        textNoTimeSlot = findViewById(R.id.text_no_time_slot);
        btnBookAppointment = findViewById(R.id.btn_book_appointment);

        // Set up RecyclerViews
        recyclerHospitals.setLayoutManager(new LinearLayoutManager(this));
        recyclerTimeSlots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize data
        setupSpecialtySpinner();
        loadHospitals();

        // Set up date picker
        editDate.setText(dateFormat.format(selectedDate.getTime()));
        editDate.setOnClickListener(v -> showDatePicker());

        // Set up hospital adapter
        hospitalAdapter = new HospitalAdapter(this, hospitals, hospital -> {
            selectedHospital = hospital;
            textSelectedHospital.setText("Bệnh viện đã chọn: " + hospital.getName());
            textSelectedHospital.setVisibility(View.VISIBLE);
            loadTimeSlots();
        });
        recyclerHospitals.setAdapter(hospitalAdapter);

        // Set up time slot adapter
        timeSlots = new ArrayList<>();
        timeSlotAdapter = new TimeSlotAdapter(this, timeSlots, this);
        recyclerTimeSlots.setAdapter(timeSlotAdapter);

        // Set up book button
        btnBookAppointment.setOnClickListener(v -> bookAppointment());
        btnBookAppointment.setEnabled(false);
    }

    private void setupSpecialtySpinner() {
        // Sample specialties
        String[] specialties = {"Chọn chuyên khoa", "Tim mạch", "Nội tiết", "Da liễu", "Sản phụ khoa", "Nhi khoa", "Tai mũi họng", "Mắt"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(adapter);

        spinnerSpecialty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // Filter hospitals by specialty
                    filterHospitalsBySpecialty(specialties[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editDate.setText(dateFormat.format(selectedDate.getTime()));

                    // Reload time slots if hospital is selected
                    if (selectedHospital != null) {
                        loadTimeSlots();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set min date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Set max date to 3 months from now
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 3);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void loadHospitals() {
        // Sample data - in a real app, this would come from a database or API
        hospitals = new ArrayList<>();
        hospitals.add(new Hospital(1, "Bệnh viện Bạch Mai", "Số 78 Giải Phóng, Phương Mai, Đống Đa, Hà Nội", 4.5f, new String[]{"Tim mạch", "Nội tiết", "Da liễu"}));
        hospitals.add(new Hospital(2, "Bệnh viện Việt Đức", "Số 40 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội", 4.6f, new String[]{"Tim mạch", "Ngoại khoa"}));
        hospitals.add(new Hospital(3, "Bệnh viện Phụ sản Trung ương", "Số 43 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội", 4.7f, new String[]{"Sản phụ khoa"}));
        hospitals.add(new Hospital(4, "Bệnh viện Nhi Trung ương", "Số 18/879 La Thành, Đống Đa, Hà Nội", 4.8f, new String[]{"Nhi khoa"}));
        hospitals.add(new Hospital(5, "Bệnh viện Mắt Trung ương", "Số 85 Bà Triệu, Hai Bà Trưng, Hà Nội", 4.5f, new String[]{"Mắt"}));
        hospitals.add(new Hospital(6, "Bệnh viện Tai Mũi Họng Trung ương", "Số 78 Giải Phóng, Phương Mai, Đống Đa, Hà Nội", 4.4f, new String[]{"Tai mũi họng"}));
        hospitals.add(new Hospital(7, "Bệnh viện Đại học Y Hà Nội", "Số 1 Tôn Thất Tùng, Đống Đa, Hà Nội", 4.9f, new String[]{"Tim mạch", "Nội tiết", "Da liễu", "Sản phụ khoa"}));
        hospitals.add(new Hospital(8, "Bệnh viện Hữu nghị Việt Đức", "Số 16 Phủ Doãn, Hoàn Kiếm, Hà Nội", 4.7f, new String[]{"Tim mạch", "Ngoại khoa"}));
    }

    private void filterHospitalsBySpecialty(String specialty) {
        List<Hospital> filteredList = new ArrayList<>();

        for (Hospital hospital : hospitals) {
            for (String hospitalSpecialty : hospital.getSpecialties()) {
                if (hospitalSpecialty.equals(specialty)) {
                    filteredList.add(hospital);
                    break;
                }
            }
        }

        hospitalAdapter.updateHospitals(filteredList);
    }

    private void loadTimeSlots() {
        // Clear previous selection
        selectedTimeSlot = null;
        btnBookAppointment.setEnabled(false);

        // Sample data - in a real app, this would come from a database or API based on hospital and date
        timeSlots.clear();

        // Generate time slots from 8:00 to 16:00 with 30-minute intervals
        Calendar slotTime = (Calendar) selectedDate.clone();
        slotTime.set(Calendar.HOUR_OF_DAY, 8);
        slotTime.set(Calendar.MINUTE, 0);
        slotTime.set(Calendar.SECOND, 0);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Check if selected date is today
        boolean isToday = false;
        Calendar today = Calendar.getInstance();
        if (selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                selectedDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            isToday = true;
        }

        for (int i = 0; i < 16; i++) { // 16 slots from 8:00 to 16:00
            // If today, only show future time slots
            if (isToday && slotTime.before(today)) {
                slotTime.add(Calendar.MINUTE, 30);
                continue;
            }

            String time = timeFormat.format(slotTime.getTime());
            boolean available = Math.random() > 0.3; // Randomly set some slots as unavailable

            timeSlots.add(new TimeSlot(i + 1, time, available));
            slotTime.add(Calendar.MINUTE, 30);
        }

        timeSlotAdapter.notifyDataSetChanged();

        if (timeSlots.isEmpty()) {
            textNoTimeSlot.setVisibility(View.VISIBLE);
            recyclerTimeSlots.setVisibility(View.GONE);
        } else {
            textNoTimeSlot.setVisibility(View.GONE);
            recyclerTimeSlots.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimeSlotClick(TimeSlot timeSlot) {
        selectedTimeSlot = timeSlot;
        btnBookAppointment.setEnabled(true);
    }

    private void bookAppointment() {
        if (selectedHospital != null && selectedTimeSlot != null) {
            String specialty = spinnerSpecialty.getSelectedItem().toString();
            String date = dateFormat.format(selectedDate.getTime());
            String time = selectedTimeSlot.getTime();

            // Tạo view cho dialog xác nhận đặt lịch
            View confirmView = getLayoutInflater().inflate(R.layout.dialog_appointment_confirm, null);
            TextView textSpecialty = confirmView.findViewById(R.id.text_specialty);
            TextView textHospital = confirmView.findViewById(R.id.text_hospital);
            TextView textDateTime = confirmView.findViewById(R.id.text_date_time);
            TextView textAddress = confirmView.findViewById(R.id.text_address);

            // Thiết lập thông tin đặt lịch
            textSpecialty.setText(specialty);
            textHospital.setText(selectedHospital.getName());
            textDateTime.setText(date + " lúc " + time);
            textAddress.setText(selectedHospital.getAddress());

            // Hiển thị dialog xác nhận
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận đặt lịch")
                    .setView(confirmView)
                    .setPositiveButton("Xác nhận đặt lịch", (dialog, which) -> {
                        // Lưu thông tin đặt lịch (trong ứng dụng thực tế sẽ lưu vào database)
                        saveAppointment(specialty, date, time);

                        // Hiển thị màn hình xác nhận đặt lịch thành công
                        showAppointmentSuccess(specialty, date, time);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            Toast.makeText(this, "Vui lòng chọn bệnh viện và thời gian khám", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAppointment(String specialty, String date, String time) {
        // Trong ứng dụng thực tế, bạn sẽ lưu thông tin đặt lịch vào database
        // Ví dụ: appointmentDAO.saveAppointment(new Appointment(...));

        // Hiện tại chỉ hiển thị thông báo
        Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
    }

    private void showAppointmentSuccess(String specialty, String date, String time) {
        // Tạo view cho dialog thành công
        View successView = getLayoutInflater().inflate(R.layout.dialog_appointment_success, null);
        TextView textAppointmentInfo = successView.findViewById(R.id.text_appointment_info);
        Button btnViewAppointments = successView.findViewById(R.id.btn_view_appointments);
        Button btnBackToHome = successView.findViewById(R.id.btn_back_to_home);

        // Thiết lập thông tin
        String appointmentInfo = "Bạn đã đặt lịch khám " + specialty +
                " tại " + selectedHospital.getName() +
                " vào ngày " + date + " lúc " + time;
        textAppointmentInfo.setText(appointmentInfo);

        // Tạo dialog thành công
        androidx.appcompat.app.AlertDialog successDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đặt lịch thành công")
                .setView(successView)
                .setCancelable(false)
                .create();

        // Thiết lập sự kiện cho các nút
        btnViewAppointments.setOnClickListener(v -> {
            // Trong ứng dụng thực tế, chuyển đến màn hình xem lịch hẹn
            Toast.makeText(this, "Chuyển đến màn hình lịch hẹn", Toast.LENGTH_SHORT).show();
            successDialog.dismiss();
            finish();
        });

        btnBackToHome.setOnClickListener(v -> {
            successDialog.dismiss();
            finish();
        });

        successDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
